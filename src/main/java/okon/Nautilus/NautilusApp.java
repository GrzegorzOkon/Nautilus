package okon.Nautilus;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import okon.Nautilus.config.AuthUserReadParams;
import okon.Nautilus.config.GeneralReadParams;
import okon.Nautilus.config.UnixReadParams;
import org.w3c.dom.Element;

import java.io.File;
import java.util.List;
import java.util.Map;

public class NautilusApp extends Application {
    private final static Map<String, List<String>> authUsers;
    private final static Map<String, List<String>> tabNames;
    private final static List<ObservableList<Action>> actions;

    static {
        Element serverAuthRoot = parseConfiguration("./config/server-auth.xml");
        Element hostsRoot = parseConfiguration("./config/hosts.xml");

        authUsers =  AuthUserReadParams.readAuthUsers(serverAuthRoot);;
        tabNames = GeneralReadParams.readTabs(hostsRoot);
        actions = UnixReadParams.readServers(hostsRoot);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        //validateUser();
        stage.setScene(prepareScene());
        stage.setTitle("Nautilus v.1.0.1 (rev. 20190901)");
        stage.show();
    }

    private Scene prepareScene() {
        TabPane tabPanel = new TabPane();
        tabPanel.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        prepareTabs(tabPanel, tabNames);
        prepareUnixData(tabPanel, actions);

        return new Scene(tabPanel, 1000, 600);
    }

    private static Element parseConfiguration(String pathname) {
        ConfigurationParser parser = new ConfigurationParser();

        return parser.parseXml(new File(pathname));
    }

    private void prepareTabs(TabPane tabPanel, Map<String, List<String>> tabNames) {
        for (String tabName : tabNames.keySet()) {
            Tab tab = new Tab(tabName);
            tabPanel.getTabs().add(tab);

            if (tabNames.get(tabName).size() > 0) {
                TabPane subTabPanel = new TabPane();
                subTabPanel.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

                tab.setContent(subTabPanel);

                for (String subTabName : tabNames.get(tabName)) {
                    subTabPanel.getTabs().add(new Tab(subTabName));
                }
            }
        }
    }

    private void prepareUnixData(TabPane tabPanel, List<ObservableList<Action>> unixData) {
        for (int i = 0; i < tabPanel.getTabs().size() && i < unixData.size(); i++) {
            TableView table = new TableView();

            TableColumn ip = new TableColumn("IP");
            ip.setMinWidth(100);
            ip.setSortable(false);
            ip.setCellValueFactory(new PropertyValueFactory<>("ip"));

            TableColumn command = new TableColumn("Command");
            command.setMinWidth(600);
            command.setSortable(false);
            command.setCellValueFactory(new PropertyValueFactory<>("command"));

            TableColumn description = new TableColumn("Description");
            description.setMinWidth(300);
            description.setSortable(false);
            description.setCellValueFactory(new PropertyValueFactory<>("description"));

            table.setItems(unixData.get(i));
            table.getColumns().addAll(ip, command, description);
            table.setRowFactory(tv -> {
                TableRow<Action> row = new TableRow<>();
                row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() == 2 && (!row.isEmpty())) {
                            Action clickedRow = row.getItem();
                            SshConnection ssh;
                            if (clickedRow.getInterfaceNames().get(1).equals("")) {
                                ssh = new SshConnection(clickedRow.getIp(), clickedRow.getPort(),
                                        authUsers.get(clickedRow.getInterfaceNames().get(0)).get(2).equals("") ? authUsers.get(clickedRow.getInterfaceNames().get(0)).get(0) : authUsers.get(clickedRow.getInterfaceNames().get(0)).get(2) + "\\" + authUsers.get(clickedRow.getInterfaceNames().get(0)).get(0),
                                        authUsers.get(clickedRow.getInterfaceNames().get(0)).get(1),
                                        "", "");
                            } else {
                                ssh = new SshConnection(clickedRow.getIp(), clickedRow.getPort(),
                                        authUsers.get(clickedRow.getInterfaceNames().get(0)).get(2).equals("") ? authUsers.get(clickedRow.getInterfaceNames().get(0)).get(0) : authUsers.get(clickedRow.getInterfaceNames().get(0)).get(2) + "\\" + authUsers.get(clickedRow.getInterfaceNames().get(0)).get(0),
                                        authUsers.get(clickedRow.getInterfaceNames().get(0)).get(1),
                                        authUsers.get(clickedRow.getInterfaceNames().get(1)).get(2).equals("") ? authUsers.get(clickedRow.getInterfaceNames().get(1)).get(0) : authUsers.get(clickedRow.getInterfaceNames().get(1)).get(2) + "\\" + authUsers.get(clickedRow.getInterfaceNames().get(1)).get(0),
                                        authUsers.get(clickedRow.getInterfaceNames().get(1)).get(1));
                            }
                            try {
                                ssh.open();
                                String result = ssh.runCommand(clickedRow.getCommand());
                                ssh.close();
                                openTerminalWindow(clickedRow.getIp() + ": " + clickedRow.getCommand(), result);

                                saveRaport(clickedRow);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                return row;
            });

            ((TabPane)tabPanel.getTabs().get(i).getContent()).getTabs().get(0).setContent(table);
        }
    }

    private void openTerminalWindow(String title, String result) {
        Stage terminalWindow = new Stage();
        terminalWindow.setTitle(title);

        BorderPane resultPanel = new BorderPane();

        TextArea resultArea = new TextArea();
        resultArea.setStyle("-fx-control-inner-background: black; " +
                "-fx-highlight-fill: white; " +
                "-fx-highlight-text-fill: black; " +
                "-fx-text-fill: white; ");
        resultArea.setWrapText(true);
        resultPanel.setCenter(resultArea);
        resultArea.appendText(result);

        Scene resultScene = new Scene(resultPanel, 900, 500);
        terminalWindow.setScene(resultScene);

        terminalWindow.show();
    }

    private void saveRaport(Action action) {
        new SqliteConnection(new JobBuilder().build(action.getIp(), action.getCommand())).execute();
    }
}
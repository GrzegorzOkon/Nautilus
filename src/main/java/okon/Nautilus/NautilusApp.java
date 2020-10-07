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
import okon.Nautilus.config.HostConfigReader;
import org.w3c.dom.Element;

import java.io.File;
import java.util.List;
import java.util.Map;

public class NautilusApp extends Application {
    private final static Map<String, List<String>> authUsers;
    private final static Map<String, Map<String, ObservableList<Action>>> actions;

    static {
        Element serverAuthRoot = parseConfiguration("./config/server-auth.xml");
        authUsers =  AuthUserReadParams.readAuthUsers(serverAuthRoot);
        actions = HostConfigReader.readParams(new File("./config/hosts.xml"));
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setScene(prepareScene());
        stage.setTitle("Nautilus v.1.0.2 (rev. 20201007)");
        stage.show();
    }

    private Scene prepareScene() {
        TabPane tabPanel = new TabPane();
        tabPanel.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        prepareView(tabPanel);
        return new Scene(tabPanel, 1000, 600);
    }

    private static Element parseConfiguration(String pathname) {
        ConfigurationParser parser = new ConfigurationParser();
        return parser.parseXml(new File(pathname));
    }

    private void prepareView(TabPane tabPanel) {
        for (String firstLayerTabName : actions.keySet()) {
            Tab firstLayerTab = new Tab(firstLayerTabName);
            tabPanel.getTabs().add(firstLayerTab);
            if (actions.get(firstLayerTabName).size() > 0) {
                TabPane secondLayerTabPanel = new TabPane();
                secondLayerTabPanel.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
                firstLayerTab.setContent(secondLayerTabPanel);
                for (String secondLayerTabName : actions.get(firstLayerTabName).keySet()) {
                    Tab secondLayerTab = new Tab(secondLayerTabName);
                    secondLayerTabPanel.getTabs().add(secondLayerTab);

                    if (actions.get(firstLayerTabName).get(secondLayerTabName).size() > 0) {
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

                        table.setItems(actions.get(firstLayerTabName).get(secondLayerTabName));
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
                        secondLayerTab.setContent(table);
                    }
                }
            }
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
package okon.Nautilus;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import okon.Nautilus.config.AuthUserReadParams;
import okon.Nautilus.config.HostConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.w3c.dom.Element;

import java.io.File;
import java.util.List;
import java.util.Map;

public class NautilusApp extends Application {
    private static final Logger logger = LogManager.getLogger(NautilusApp.class);
    static final String version;
    public final static Map<String, List<String>> authUsers;
    public final static Map<String, Map<String, ObservableList<Action>>> actions;

    static {
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        File file = new File("config/log4j2.xml");
        context.setConfigLocation(file.toURI());
        version = ProgramVersion.getTitleDescription();
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
        stage.setTitle(version);
        stage.show();
    }

    private Scene prepareScene() {
        TabPane tabPanel = new TabPane();
        tabPanel.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        prepareView(tabPanel);
        return new Scene(tabPanel, 1150, 700);
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
                        ip.setMinWidth(80);
                        ip.setSortable(false);
                        ip.setCellValueFactory(new PropertyValueFactory<>("ip"));

                        TableColumn hostname = new TableColumn("Hostname");
                        hostname.setMinWidth(80);
                        hostname.setSortable(false);
                        hostname.setCellValueFactory(new PropertyValueFactory<>("hostname"));

                        TableColumn command = new TableColumn("Command");
                        command.setMinWidth(500);
                        command.setSortable(false);
                        command.setCellValueFactory(new PropertyValueFactory<>("command"));

                        TableColumn secureMode = new TableColumn("Security");
                        secureMode.setMinWidth(60);
                        command.setSortable(false);
                        secureMode.setCellValueFactory(new PropertyValueFactory<>("secureMode"));

                        TableColumn description = new TableColumn("Description");
                        description.setMinWidth(400);
                        description.setSortable(false);
                        description.setCellValueFactory(new PropertyValueFactory<>("description"));

                        table.setItems(actions.get(firstLayerTabName).get(secondLayerTabName));
                        table.getColumns().addAll(ip, hostname, command, secureMode, description);
                        table.setRowFactory(tv -> {
                            TableRow<Action> row = new TableRow<>();
                            row.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    if (event.getClickCount() == 2 && (!row.isEmpty())) {
                                        SshConnection ssh = null;
                                        if (row.getItem().getInterfaceNames().get(1).equals("")) {
                                            ssh = new SshConnection(row.getItem().getIp(), row.getItem().getPort(),
                                                    authUsers.get(row.getItem().getInterfaceNames().get(0)).get(2).equals("") ? authUsers.get(row.getItem().getInterfaceNames().get(0)).get(0) : authUsers.get(row.getItem().getInterfaceNames().get(0)).get(2) + "\\" + authUsers.get(row.getItem().getInterfaceNames().get(0)).get(0),
                                                    authUsers.get(row.getItem().getInterfaceNames().get(0)).get(1),
                                                    "", "");
                                        } else {
                                            ssh = new SshConnection(row.getItem().getIp(), row.getItem().getPort(),
                                                    authUsers.get(row.getItem().getInterfaceNames().get(0)).get(2).equals("") ? authUsers.get(row.getItem().getInterfaceNames().get(0)).get(0) : authUsers.get(row.getItem().getInterfaceNames().get(0)).get(2) + "\\" + authUsers.get(row.getItem().getInterfaceNames().get(0)).get(0),
                                                    authUsers.get(row.getItem().getInterfaceNames().get(0)).get(1),
                                                    authUsers.get(row.getItem().getInterfaceNames().get(1)).get(2).equals("") ? authUsers.get(row.getItem().getInterfaceNames().get(1)).get(0) : authUsers.get(row.getItem().getInterfaceNames().get(1)).get(2) + "\\" + authUsers.get(row.getItem().getInterfaceNames().get(1)).get(0),
                                                    authUsers.get(row.getItem().getInterfaceNames().get(1)).get(1));
                                        }
                                        new Thread(new TerminalWindow(row.getItem(), ssh)).start();
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
}
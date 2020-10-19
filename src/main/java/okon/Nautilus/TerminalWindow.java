package okon.Nautilus;

import com.jcraft.jsch.JSchException;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

public class TerminalWindow implements Runnable {
    private final Action action;
    private final SshConnection ssh;
    Stage stage = new Stage();
    private TextArea workingArea = new TextArea();
    private boolean userActionBlock = false;

    public TerminalWindow(Action action, SshConnection ssh) {
        this.action = action;
        this.ssh = ssh;
        initializeWindow(action);
    }

    private void initializeWindow(Action action) {
        BorderPane workingPanel = new BorderPane();
        Scene workingScene = new Scene(workingPanel, 900, 500);
        stage.setTitle(action.getIp() + ": " + action.getCommand());
        workingArea.setStyle("-fx-control-inner-background: black; " +
                "-fx-highlight-fill: white; " +
                "-fx-highlight-text-fill: black; " +
                "-fx-text-fill: white; ");
        workingArea.setWrapText(true);
        workingPanel.setCenter(workingArea);
        stage.setScene(workingScene);
        stage.show();
    }

    @Override
    public void run() {
        try {
            if (action.isSecureMode()) {
                userActionBlock = true;
                workingArea.appendText("Are you sure? [y/n]:   ");
                workingArea.setOnKeyReleased(event -> {
                    if(event.getCode() == KeyCode.ENTER && userActionBlock == true) {
                        String[] lines = workingArea.getText().split("\n");
                        String input = lines[lines.length - 1].substring(23);
                        try {
                            workingArea.clear();
                            userActionBlock = false;
                            if (input.contains("y")) {
                                ssh.open();
                                String commandResponse = ssh.runCommand(action.getCommand());
                                ssh.close();
                                printResponse(commandResponse);
                            }
                        } catch (JSchException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                ssh.open();
                String commandResponse = ssh.runCommand(action.getCommand());
                ssh.close();
                printResponse(commandResponse);
            }
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printResponse(String response) {
        workingArea.appendText(response);
    }
}
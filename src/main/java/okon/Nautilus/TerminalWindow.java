package okon.Nautilus;

import com.jcraft.jsch.JSchException;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.IOException;

import static okon.Nautilus.NautilusApp.authUsers;

public class TerminalWindow implements Runnable {
    private final Action action;
    private final SshConnection ssh;
    Stage stage = new Stage();
    private TextArea workingArea = new TextArea();

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
            ssh.open();
            String commandResponse = ssh.runCommand(action.getCommand());
            ssh.close();
            printResponse(commandResponse);
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
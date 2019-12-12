package okon.Nautilus;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

public class SshConnection {
    private Session session;
    private String hostname;
    private Integer port;
    private String username;
    private String password;
    private String switchedUsername;
    private String switchedPassword;

    public SshConnection(String hostname, Integer port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public SshConnection(String hostname, Integer port, String username, String password, String switchedUsername, String switchedPassword) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.switchedUsername = switchedUsername;
        this.switchedPassword = switchedPassword;
    }

    public void open() throws JSchException {
        open(this.hostname, this.port, this.username, this.password);
    }

    public void open(String hostname, Integer port,  String username, String password) throws JSchException{
        JSch jSch = new JSch();
        session = jSch.getSession(username, hostname, port);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
        session.setConfig(config);
        session.setPassword(password);
        System.out.println("Connecting SSH to " + hostname + " - Please wait for few seconds... ");
        session.connect();
        System.out.println("Connected!");
    }

    public String runCommand(String command) throws JSchException, IOException {
        String result = "";
        if (!session.isConnected())
            throw new RuntimeException("Not connected to an open session.  Call open() first!");
        ChannelExec channel = null;
        channel = (ChannelExec) session.openChannel("exec");

        //passing creds only when you switch user
        if (!switchedUsername.isEmpty()) {
            command = "su - " + switchedUsername + " -c \"" + command + "\"";
        }

        channel.setCommand(command);
        channel.setInputStream(null);
        PrintStream out = new PrintStream(channel.getOutputStream());
        InputStream in = channel.getInputStream();
        channel.connect();

        //passing creds only when you switch user
        if (!switchedUsername.isEmpty()) {
            System.out.println("Setting suPasswd now....");
            out.write((switchedPassword + "\n").getBytes());
            out.flush();
            System.out.println("Flushed suPasswd to cli...");
        }

        result = getChannelOutput(channel, in);
        channel.disconnect();
        System.out.println("Finished sending commands!");
        return result;
    }

    private String getChannelOutput(Channel channel, InputStream in) throws IOException{
        byte[] buffer = new byte[1024];
        StringBuilder strBuilder = new StringBuilder();

        String line = "";
        while (true){
            while (in.available() > 0) {
                int i = in.read(buffer, 0, 1024);
                if (i < 0) {
                    break;
                }
                strBuilder.append(new String(buffer, 0, i));
                System.out.println(line);
            }

            if(line.contains("logout")){
                break;
            }

            if (channel.isClosed()){
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (Exception ee){}
        }

        return strBuilder.toString();
    }

    public void close(){
        session.disconnect();
        System.out.println("Disconnected channel and session");
    }
}

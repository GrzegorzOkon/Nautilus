package okon.Nautilus;

import org.sqlite.SQLiteDataSource;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class JobBuilder {
    Job job = new Job();

    public Job build(String ip, String command) {
        buildDataSource();
        buildSql(ip, command);
        return job;
    }

    private void buildDataSource() {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite://10.37.0.113/C$/Nautilus/Nautilusdb.sqlite");
        job.setDataSource(dataSource);
    }

    private void buildSql(String ip, String command) {
        job.setSql("insert into raports(date, user, raport) values('" + currentDate() + "', '" + System.getProperty("user.name") + "', '" + report(ip, command) + "')");
    }

    private String currentDate() {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return formatter.format(date);
    }

    private String report(String ip, String command) {
        return "Na serwerze " + ip + " wykonano " + command + ".";
    }
}
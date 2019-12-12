package okon.Nautilus;

import javax.sql.DataSource;

public class Job {
    private DataSource dataSource;
    private String sql;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}

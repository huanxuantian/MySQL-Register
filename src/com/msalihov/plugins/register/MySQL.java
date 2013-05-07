package com.msalihov.plugins.register;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class MySQL {
    
    private Connection con;
    private Statement st;
    private ResultSet rs;
    private PreparedStatement ps;
    
    private Register r;
    
    public MySQL(Register reg) {
        r = reg;
    }
    
    protected boolean loadDriver() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return true;
        }
        catch (ClassNotFoundException ex) {
            r.getLogger().severe("Could not load JDBC driver!");
            ex.printStackTrace();
            return false;
        }
    }
    
    protected String getJdbcUrl() {
        String host = r.getConfig().getString("connection.hostname");
        String port = r.getConfig().getString("connection.port");
        String name = r.getConfig().getString("connection.name");
        String url = "jdbc:mysql://"+host+":"+port+"/"+name;
        return url;
    }
    
    protected Properties getProperties() {
        String user = r.getConfig().getString("connection.username");
        String pass = r.getConfig().getString("connection.password");
        Properties prop = new Properties();
        prop.put("autoReconnect", "true");
	prop.put("user", user);
	prop.put("password", pass);
	prop.put("useUnicode", "true");
	prop.put("characterEncoding", "utf8");
        return prop;
    }
    
    public boolean connect() {
        if(loadDriver()) {
            r.getLogger().info("Connecting to database...");
            try {
                con = DriverManager.getConnection(getJdbcUrl(), getProperties());
                r.getLogger().info("Connected to database!");
                return true;
            }
            catch (SQLException ex) {
                r.getLogger().severe("Could not connect to database!");
                ex.printStackTrace();
                return false;
            }
        }
        else {
            return false;
        }
    }
    
    public boolean close() {
        r.getLogger().info("Disconnecting from database...");
        if(isConn()) {
            try {
                con.close();
                r.getLogger().info("Disconnected from database!");
                return true;
            } catch (SQLException ex) {
                r.getLogger().severe("Could not disconnect from database!");
                return false;
            }
        }
        else {
            r.getLogger().severe("No connection detected!");
            return false;
        }
    }
    
    public boolean isConn() {
        if(con!=null) {
            try {
                if(con.isValid(1)) {
                    return true;
                }
                else {
                    return false;
                }
            }
            catch (SQLException ex) {
                r.getLogger().severe("Database error!");
                ex.printStackTrace();
                return false;
            }
        }
        else{
            return false;
        }
    }
    
    public ResultSet query(String query) throws SQLException {
        if(isConn()) {
            st = con.createStatement();
            st.execute(query);
            rs = st.getResultSet();
            return rs;
        }
        else {
            r.getLogger().severe("Database error!");
            return null;
        }
    }
    
    public ResultSet query(PreparedStatement ps) throws SQLException {
        if(isConn()) {
            ps.execute();
            rs = ps.getResultSet();
            return rs;
        }
        else {
            r.getLogger().severe("Database error!");
            return null;
        }
    }
    
    public PreparedStatement prepare(String query) throws SQLException {
        if(isConn()) {
            ps = con.prepareStatement(query);
            return ps;
        }
        else {
            r.getLogger().severe("Database error!");
            return null;
        }
    }
    
    public boolean isTable(String table)
    {
        try {
            String query = "SELECT * FROM " + table;
            st = con.createStatement();
            st.executeQuery(query);
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }
    
}
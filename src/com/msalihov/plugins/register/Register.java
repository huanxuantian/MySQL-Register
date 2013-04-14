package com.msalihov.plugins.register;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.MySQL;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Register extends JavaPlugin {
    
    public static Database db;
    public PreparedStatement ps;
    
    private String tablename;
    private String usercolumn;
    private String emailcolumn;
    private String passcolumn;
    private String hashing;
    private int passchars;
    
    private HashPassword hash;
    private Utility util;
    
    public static final String PLAYER_ONLY=ChatColor.RED+"Only a player may register!";
    public static final String NO_PERMISSIONS=ChatColor.RED+"You do not have permission to register!";
    public static final String INVALID_EMAIL=ChatColor.RED+"Please enter a valid email!";
    public static final String NOMATCH_PASS=ChatColor.RED+"The two passwords supplied do not match!";
    public static final String REGISTER_SUCCESS=ChatColor.GREEN+"Registration successful!";
    public static final String REGISTER_FAIL=ChatColor.RED+"Registration could not be completed due to a server error! Please contact a member of staff.";
    public static final String CANT_RUN=ChatColor.RED+"Cannot register at this time!";
    public static final String TOO_MANY_ARGS=ChatColor.RED+"Too many arguments!";
    public static final String TOO_LITTLE_ARGS=ChatColor.RED+"Too little arguments!";
    public static final String ALREADY_REG=ChatColor.RED+"You are already registered!";
    
    @Override
    public void onEnable(){
        this.saveDefaultConfig();
        String dbhost=this.getConfig().getString("connection.hostname","localhost");
        int dbport=this.getConfig().getInt("connection.port",3306);
        String dbname=this.getConfig().getString("connection.name","minecraft");
        String dbuser=this.getConfig().getString("connection.username","plugin");
        String dbpass=this.getConfig().getString("connection.password","password");
        tablename=this.getConfig().getString("database.table-name","users");
        usercolumn=this.getConfig().getString("database.user-column","username");
        emailcolumn=this.getConfig().getString("database.email-column","email");
        passcolumn=this.getConfig().getString("database.password-column","password");
        hashing=this.getConfig().getString("hashing","none");
        hash=new HashPassword();
        util=new Utility();
        if("md5".equals(hashing)){
            passchars=32;
            this.getLogger().log(Level.INFO, "MD5 hashing selected!");
        }
        else if("sha1".equals(hashing)){
            passchars=40;
            this.getLogger().log(Level.INFO, "SHA1 hashing selected!");
        }
        else{
            passchars=128;
            this.getLogger().log(Level.INFO, "No hashing selected!");
        }
        this.getLogger().log(Level.INFO, "Connecting to database...");
        db=new MySQL(Logger.getLogger("Minecraft"),"MySQL Register",dbhost,dbport,dbname,dbuser,dbpass);
        if(db.open()){
            this.getLogger().log(Level.INFO, "Connected to database!");
        }
        else{
            this.getLogger().log(Level.SEVERE, "Could not connect to database!");
        }
        if(!db.isTable(tablename)){
            this.getLogger().log(Level.WARNING, "Did not find table specified in cofig! Creating it...");
            try {
                db.query("CREATE TABLE "+tablename+" (id INT PRIMARY KEY AUTO_INCREMENT,"+usercolumn+" VARCHAR(128) NOT NULL,"+passcolumn+" VARCHAR("+passchars+") NOT NULL,"+emailcolumn+" VARCHAR(256) NOT NULL)");
                this.getLogger().log(Level.INFO, "Table created!");
            } catch (SQLException e) {
                this.getLogger().log(Level.SEVERE, "Table creation failed!");
                e.printStackTrace();
            }
        }
    }
    
    public void onDisable(){
        if(db.isOpen()){
            if(db.close()){
                this.getLogger().log(Level.INFO, "Disconnected from database!");
            }
            else{
                this.getLogger().log(Level.SEVERE, "Could not disconnect from database!");
            }
        }
        this.saveConfig();
    }
    
    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args){
        if(db.isOpen()){
            if(cmd.getName().equalsIgnoreCase("register")){
                if(sender instanceof Player){
                    if(args.length>3){
                        sender.sendMessage(TOO_MANY_ARGS);
                        return false;
                    }
                    if(args.length<3){
                        sender.sendMessage(TOO_LITTLE_ARGS);
                        return false;
                    }
                    if(sender.hasPermission("mysqlregister.register")){
                        if(util.canRegister(sender.getName(),tablename,usercolumn)){
                            if(util.isEmailValid(args[0])){
                                if(args[1].equals(args[2])){
                                    try {
                                        String pass=hash.hashPassword(hashing,args[1]);
                                        ps=db.prepare("INSERT INTO "+tablename+" ("+usercolumn+","+passcolumn+","+emailcolumn+") VALUES (?,?,?)");
                                        ps.setString(1, sender.getName());
                                        ps.setString(2, pass);
                                        ps.setString(3, args[0]);
                                        try{
                                            db.query(ps);
                                            sender.sendMessage(REGISTER_SUCCESS);
                                            return true;
                                        }
                                        catch(SQLException e){
                                            this.getLogger().log(Level.SEVERE, "Registration error: could not execute prepared statement!");
                                            sender.sendMessage(REGISTER_FAIL);
                                            e.printStackTrace();
                                        }
                                    } catch (SQLException e) {
                                                                                this.getLogger().log(Level.SEVERE, "Registration error: could not prepare statement/assign values to it!");
                                        sender.sendMessage(REGISTER_FAIL);
                                        e.printStackTrace();
                                    }
                                }
                                else{
                                    sender.sendMessage(NOMATCH_PASS);
                                    return false;
                                }
                            }
                            else{
                                sender.sendMessage(INVALID_EMAIL);
                                return false;
                            }
                        }
                        else{
                            sender.sendMessage(ALREADY_REG);
                            return true;
                        }
                    }
                    else{
                        sender.sendMessage(NO_PERMISSIONS);
                        return false;
                    }
                }
                else{
                    sender.sendMessage(PLAYER_ONLY);
                    return true;
                }
            }
        }
        else{
            sender.sendMessage(CANT_RUN);
        }
        return false;
    }
}
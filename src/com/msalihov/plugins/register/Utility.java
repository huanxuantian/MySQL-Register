package com.msalihov.plugins.register;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Utility {
    
    private MySQL db;
    
    public Utility(MySQL my)
    {
        db = my;
    }
    
    public boolean isEmailValid(String email){
        String[] ill={"'","\"","!","$","%","^","&","*","(",")","-","+","=","~","`",",","<",">","?","/",":",";","\\","|"};
        if(email.contains("@")){
            if(email.contains(".")){
                for(int i=0; i < ill.length; i++){
                    if(email.contains(ill[i])){
                        return false;
                    }
                }
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }
    
    public boolean canRegister(String username,String tablename,String usercolumn){
        try {
            ResultSet rs=db.query("SELECT * FROM "+tablename+" WHERE "+usercolumn+"='"+username+"'");
            int i=0;
            while(rs.next()){
                i++;
            }
            if(i==0){
                return true;
            }
            else{
                return false;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}

package com.msalihov.plugins.register;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashPassword {
    
    public String hashPassword(String algorythm,String password){
        if(algorythm.equalsIgnoreCase("md5")){
            return hashMD5(password);
        }
        else if(algorythm.equalsIgnoreCase("sha1")){
            return hashSHA(password);
        }
        else{
            return password;
        }
    }
    
    public String hashMD5(String password){
        String hashed;
        try{
            MessageDigest hash=MessageDigest.getInstance("MD5");
            hash.update(password.getBytes());
            byte[] digest=hash.digest();
            StringBuilder sb=new StringBuilder();
            for(byte b : digest){
                sb.append(Integer.toHexString((int) (b & 0xff)));
            }
            hashed=sb.toString();
            return hashed;
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }
    
    public String hashSHA(String password){
        String hashed;
        try{
            MessageDigest hash=MessageDigest.getInstance("SHA1");
            hash.update(password.getBytes());
            byte[] digest=hash.digest();
            StringBuilder sb=new StringBuilder();
            for(byte b : digest){
                sb.append(Integer.toHexString((int) (b & 0xff)));
            }
            hashed=sb.toString();
            return hashed;
        }
        catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }
}

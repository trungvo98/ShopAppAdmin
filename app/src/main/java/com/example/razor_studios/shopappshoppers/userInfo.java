package com.example.razor_studios.shopappshoppers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class userInfo {
    public static String USER_NAME, USER_EMAIL, USER_FULLNAME, USER_PASS, PHONE_NUMBER;
    public userInfo(String uName, String uEmail, String uFullName, String uPass, String pNumber) {
        this.USER_NAME     = uName;
        this.USER_EMAIL    = uEmail;
        this.USER_FULLNAME = uFullName;
        this.USER_PASS     = uPass;
        this.PHONE_NUMBER  = pNumber;
    }

    public static boolean checkUserExistance(String userName, String userPass) {
        boolean isUserFound = false;
        Connection con = DBConnect.getConnection();
        if(con == null) {
            isUserFound = false;
        } else {
            String checkQuery = "SELECT * FROM useraccount WHERE userName='" + userName + "' and userPass='" + userPass +"'";
            try {
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(checkQuery);
                while(rs.next()) {
                    isUserFound = true;
                    break;
                }
                con.close();
            } catch (Exception ex){
                isUserFound = false;
            }
        }
        return isUserFound;
    }
}

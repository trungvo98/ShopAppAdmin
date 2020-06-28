package com.example.razor_studios.shopappshoppers;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ProfileActivity extends AppCompatActivity {
    private ImageView closeButton;
    private TextView deliveryDetails, passwordSettings;
    private EditText fullName, emailAddress, phoneNumber, streetAddress, homeNumber, kebele, oldPassword, newPassword;
    private Button saveSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(userInfo.checkUserExistance(userInfo.USER_NAME, userInfo.USER_PASS) != true) {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_profile);

        closeButton      = (ImageView) findViewById(R.id.closeButton);
      //  deliveryDetails  = (TextView) findViewById(R.id.deliveryDetails);
     //   fullName        = (EditText) findViewById(R.id.profile_full_name);
      //  emailAddress     = (EditText) findViewById(R.id.profile_email_address);
        //phoneNumber      = (EditText) findViewById(R.id.profile_phone_number);
      //  streetAddress    = (EditText) findViewById(R.id.profile_street);
       // kebele           = (EditText) findViewById(R.id.profile_kebele);
       // homeNumber       = (EditText) findViewById(R.id.profile_home_number);
        passwordSettings = (TextView) findViewById(R.id.passwordSettings);
        oldPassword      = (EditText) findViewById(R.id.profile_old_password);
        newPassword      = (EditText) findViewById(R.id.profile_new_password);
        saveSettings     = (Button) findViewById(R.id.saveSettings);

        homeNumber.setWidth((getWindowManager().getDefaultDisplay().getWidth() - 140) / 2);

        deliveryDetails.setBackgroundColor(Color.rgb(245,245,245));
        passwordSettings.setBackgroundColor(Color.rgb(245,245,245));
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        saveSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserProfile();
            }
        });
        fetchUserInfo();
    }
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
    }
    public void fetchUserInfo() {
        try {
            Connection conn = DBConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs;

            String Query = "SELECT * FROM useraccount WHERE userName='" + userInfo.USER_NAME + "'";
            rs = stmt.executeQuery(Query);
            rs.next();
            fullName.setText(""+rs.getString("userFullName"));
            emailAddress.setText(""+rs.getString("userEmail"));
            phoneNumber.setText(""+rs.getString("phoneNumber"));
            streetAddress.setText(""+rs.getString("streetAddress"));
            kebele.setText(""+rs.getString("userKebele"));
            homeNumber.setText(""+rs.getString("homeNumber"));
            conn.close();
        } catch (Exception ex) {}
    }
    public void updateUserProfile() {
        Connection con = DBConnect.getConnection();
        String retMessage = "", Query;
        String fName = fullName.getText().toString();
        String email = emailAddress.getText().toString();
        String oldPass = oldPassword.getText().toString();
        String pNumber = phoneNumber.getText().toString();
        String sAddress = streetAddress.getText().toString();
        String hNumber = homeNumber.getText().toString();
        String uKebele = kebele.getText().toString();
        String newPass = newPassword.getText().toString();

        if(con == null) {
            retMessage = "Unable To Connect To The Server";
        } else if(oldPass.equals("")){
            retMessage = "For Security Reason You Have To Enter Your Old Password";
        } else {
            try {
                Query = "SELECT * FROM useraccount WHERE userName='" + userInfo.USER_NAME + "' AND userPass='" + oldPassword.getText().toString() + "'";
                Statement stmt = con.createStatement();
                ResultSet rs;
                rs = stmt.executeQuery(Query);
                if(rs.next()) {
                    if(!fName.equals("")) {
                        Query = "UPDATE SET userFullName='" + fName + "' WHERE useraccount WHERE userName='" + userInfo.USER_NAME + "' AND userPass='" + oldPass + "'";
                        stmt.executeQuery(Query);
                    }
                    if(!email.equals("")) {
                        Query = "UPDATE SET userEmail='" + email + "' WHERE useraccount WHERE userName='" + userInfo.USER_NAME + "' AND userPass='" + oldPass + "'";
                        stmt.executeQuery(Query);
                    }
                    if(!pNumber.equals("")) {
                        Query = "UPDATE SET phoneNumber='" + pNumber + "' WHERE useraccount WHERE userName='" + userInfo.USER_NAME + "' AND userPass='" + oldPass + "'";
                        stmt.executeQuery(Query);
                    }
                    if(!sAddress.equals("")) {
                        Query = "UPDATE SET streetAddress='" + sAddress + "' WHERE useraccount WHERE userName='" + userInfo.USER_NAME + "' AND userPass='" + oldPass + "'";
                        stmt.executeQuery(Query);
                    }
                    if(!hNumber.equals("")) {
                        Query = "UPDATE SET homeNumber='" + hNumber + "' WHERE useraccount WHERE userName='" + userInfo.USER_NAME + "' AND userPass='" + oldPass + "'";
                        stmt.executeQuery(Query);
                    }
                    if(!uKebele.equals("")) {
                        Query = "UPDATE SET userKebele='" + uKebele + "' WHERE useraccount WHERE userName='" + userInfo.USER_NAME + "' AND userPass='" + oldPass + "'";
                        stmt.executeQuery(Query);
                    }
                    if(!newPass.equals("")) {
                        Query = "UPDATE SET userPass='" + newPass + "' WHERE useraccount WHERE userName='" + userInfo.USER_NAME + "' AND userPass='" + oldPass + "'";
                        stmt.executeQuery(Query);
                    }
                    retMessage = "Delivery Setting is Sucessfully Updated";
                } else {
                    retMessage = "Your Old Password is Not Correct";
                }
            } catch (Exception ex){}
        }

        Toast.makeText(this, ""+retMessage, Toast.LENGTH_SHORT).show();
    }
}

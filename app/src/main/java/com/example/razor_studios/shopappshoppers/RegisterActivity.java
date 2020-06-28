package com.example.razor_studios.shopappshoppers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    EditText fullName, userEmail, phoneNumber, streetAddress, homeNumber, newPassword, confirmPassword;
    String retMessage;
    boolean isSucess = false;
    Button registerButton;
    ImageView closeActivity;

    Spinner spinner;
    Locale myLocale;
    String currentLanguage = "en", currentLang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        selectLanguage();

        fullName       = (EditText) findViewById(R.id.userfullname);
        userEmail       = (EditText) findViewById(R.id.register_email_address);
        phoneNumber     = (EditText) findViewById(R.id.register_phone_number);
        streetAddress   = (EditText) findViewById(R.id.register_street);
        newPassword     = (EditText) findViewById(R.id.register_password);
        confirmPassword = (EditText) findViewById(R.id.register_confirm_password);
        homeNumber      = (EditText) findViewById(R.id.register_home_number);
        registerButton  = (Button) findViewById(R.id.registerButton);
        closeActivity   = (ImageView) findViewById(R.id.closeActivity);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUser authUser = new AuthUser();
                authUser.execute("");
            }
        });

        fullName.setWidth((getWindowManager().getDefaultDisplay().getWidth() - 140) / 2);
        homeNumber.setWidth((getWindowManager().getDefaultDisplay().getWidth() - 140) / 2);
        closeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    protected void selectLanguage() {
        currentLanguage = getIntent().getStringExtra(currentLang);

        spinner = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();

        list.add(" ");
        list.add("English");
        list.add("አማርኛ");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        setLocale("en");
                        break;
                    case 2:
                        setLocale("et");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            myLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, MainActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        }
    }
    public boolean checkUserExistance() {
        boolean isUserFound;
        Connection con = DBConnect.getConnection();
        if(con == null) {
            isUserFound = true;
            retMessage = "Không thể kết nối đến server";
        } else {
            String checkQuery = "SELECT * FROM useraccount WHERE userEmail='" + userEmail.getText().toString().toLowerCase() + "'";
            try {
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(checkQuery);
                if(rs.next()) {
                    retMessage = "Người dùng đã đăng ký";
                    isUserFound = true;
                } else {
                    checkQuery = "SELECT * FROM useraccount WHERE phoneNumber='" + phoneNumber.getText().toString() + "'";
                    stmt = con.createStatement();
                    rs = stmt.executeQuery(checkQuery);
                    if(rs.next()) {
                        retMessage = "Người dùng đã đăng ký";
                        isUserFound = true;
                    } else {
                        retMessage = "Thành công";
                        isUserFound = false;
                    }
                }
                con.close();
            } catch (Exception ex){
                isUserFound = true;
                retMessage = "Error Connecting to the Database";
            }
        }
        return isUserFound;
    }
    public int countUserDatabase() {
        int a = 0;
        try {
            Connection conn = DBConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs;

            String Query = "SELECT * FROM userAccount";
            rs = stmt.executeQuery(Query);
            while (rs.next()) {
                a++;
            }
            conn.close();
        } catch (Exception ex) {
        }
        return a;
    }
    public class AuthUser extends AsyncTask<String,String,String> {
        String uEmail       = userEmail.getText().toString().toLowerCase();
        String uPassNew     = newPassword.getText().toString();
        String uPassConfirm = confirmPassword.getText().toString();
        String uFullName    = fullName.getText().toString();
        String pNumber      = phoneNumber.getText().toString();
        String sAddress     = streetAddress.getText().toString();
        String hNumber      = homeNumber.getText().toString();
       // String uKebele      = kebele.getText().toString();
        String uName        = "user_" + (countUserDatabase() + 1);

        String retMessage;
        ProgressDialog loading;

        protected void onPreExecute() {
            loading = ProgressDialog.show(RegisterActivity.this, "Đang xác thực...",null, true, true);
        }
        protected void onPostExecute(String retMessage) {
            loading.dismiss();
            if(isSucess == true) {
                Toast.makeText(getApplicationContext(),""+retMessage,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(),""+retMessage,Toast.LENGTH_LONG).show();
            }
        }
        protected String doInBackground(String... params) {
            Connection con = DBConnect.getConnection();
            if(con == null) {
                retMessage = "Không thể kết nối đến server";
            } else {
                if(uEmail.equals("") || uFullName.equals("") || uPassNew.equals("") || uPassConfirm.equals("") || uFullName.equals("") ||
                        pNumber.equals("") || sAddress.equals("") || hNumber.equals("") ) {
                    retMessage = "Vui lòng điền tất cả các trường yêu cầu";
                } else if(!uPassNew.equals(uPassConfirm)) {
                    retMessage = "Mật khẩu không trùng khớp";
                } else if(checkUserExistance() == true) {
                    retMessage = "Email hay số điện thoại đã được đăng ký trước đó";
                } else {
                    String registerQuery = "INSERT INTO useraccount(userName,userEmail,userPass,userFullName,phoneNumber,streetAddress,homeNumber,userKebele) " +
                            "VALUES('"+ uName +"','"+ uEmail +"','"+ uPassNew +"','"+ uFullName +"','"+ pNumber +"','"+ sAddress +"','"+ hNumber +"','"+ 122 +"')";
                    String createCartTable = "CREATE TABLE "+uName+"(" +
                            "orderID INT NOT NULL AUTO_INCREMENT primary key," +
                            "itemID INT," +
                            "orderNumPics INT," +
                            "orderState VARCHAR(10)" +
                            ")";
                    try {
                        Statement stmt = con.createStatement();
                        stmt.executeUpdate(registerQuery);
                        stmt.executeUpdate(createCartTable);
                        retMessage = "Đăng ký thành công";
                        isSucess = true;
                        con.close();
                    } catch (Exception ex) {
                        retMessage = "Lỗi xảy ra khi kết nối server";
                    }
                }
            }

            return retMessage;
        }
    }
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}

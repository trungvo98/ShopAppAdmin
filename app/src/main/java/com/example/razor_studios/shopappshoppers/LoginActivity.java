package com.example.razor_studios.shopappshoppers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    public Button loginButton;
    public LinearLayout loginArea;
    public EditText loginEmail, loginPass;
    public boolean isSucess = false;

    Spinner spinner;
    Locale myLocale;
    String currentLanguage = "en", currentLang, userLanguage = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        selectLanguage();

        loginArea = (LinearLayout) findViewById(R.id.loginArea);
        loginButton = (Button) findViewById(R.id.loginButton);
        ImageView closeActivity = (ImageView) findViewById(R.id.closeActivity);
        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPass = (EditText) findViewById(R.id.login_password);

        loginArea.setPadding(0,MainActivity.screenHeight/3,0,0);

        closeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AuthUser().execute();
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
                        userLanguage = "en";
                        setLocale("en");
                        break;
                    case 2:
                        userLanguage = "et";
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
            Intent refresh = new Intent(this, LoginActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        }
    }
    public class AuthUser extends AsyncTask<String,String,String> {
        String userEmail = loginEmail.getText().toString().toLowerCase();
        String userPass = loginPass.getText().toString();
        String userFullName;
        String phoneNumber;
        String loggedUserName;
        String retMessage;
        ProgressDialog loading;

        protected void onPreExecute() {
            System.out.println(userEmail);
            System.out.println(userPass);
            loading = ProgressDialog.show(LoginActivity.this, "Đang xác thực...",null, true, true);
        }

        protected void onPostExecute(String retMessage) {
            loading.dismiss();
            if(isSucess == true) {
                SQLiteDBHelper sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());
                SQLiteDatabase sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
                sqLiteDBHelper.addAccount(loggedUserName, userEmail, userPass, userFullName, phoneNumber, userLanguage, sqLiteDatabase);
                new userInfo(loggedUserName, userEmail, userFullName, userPass, phoneNumber);
                sqLiteDBHelper.close();

                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(),""+retMessage,Toast.LENGTH_LONG).show();
            }
        }

        protected String doInBackground(String... params) {
            Connection con = DBConnect.getConnection();
            if(con == null) {
                retMessage = "Unable To Connect To The Server";
            } else {
                System.out.println("Connected");
                String loginQuery = "SELECT * FROM useraccount WHERE userEmail='" + userEmail + "' and userPass='" + userPass + "'";
                try {
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(loginQuery);
                    if(rs.next()) {
                        retMessage = "Login Sucessfull";
                        isSucess = true;
                        loggedUserName = rs.getString("userName");
                        phoneNumber    = rs.getString("phoneNumber");
                        userFullName   = rs.getString("userFullName");
                    } else {
                        loginQuery = "SELECT * FROM useraccount WHERE phoneNumber='" + userEmail + "' and userPass='" + userPass + "'";
                        stmt = con.createStatement();
                        rs = stmt.executeQuery(loginQuery);
                        if(rs.next()) {
                            retMessage = "Login Sucessful";
                            phoneNumber    = userEmail;
                            loggedUserName = rs.getString("userName");
                            userEmail      = rs.getString("userEmail");
                            userFullName   = rs.getString("userFullName");
                            isSucess = true;
                        } else {
                            retMessage = "Invalid Email or Password";
                        }
                    }
                    con.close();
                } catch (Exception ex){
                    retMessage = "Something is Wrong in the database";
                }
            }

            return retMessage;
        }
    }
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }
}

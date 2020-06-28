package com.example.razor_studios.shopappshoppers;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static int screenWidth, screenHeight;
    public String USER_NAME, USER_EMAIL, USER_PASS, USER_FULLNAME, PHONE_NUMBER;
    Spinner spinner;
    Locale myLocale;
    public String currentLanguage = "en", currentLang, USER_LANG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // selectLanguage();

        SQLiteDBHelper sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = sqLiteDBHelper.getReadableDatabase();
        Cursor cursor = sqLiteDBHelper.readAccount(sqLiteDatabase);
        if(cursor.moveToNext()) {
            USER_NAME     = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.USER_NAME));
            USER_EMAIL    = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.USER_EMAIL));
            USER_PASS     = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.USER_PASS));
            USER_FULLNAME = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.USER_FULLNAME));
            PHONE_NUMBER  = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.PHONE_NUMBER));
            USER_LANG     = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.USER_LANG));
            setLocale(USER_LANG);
            if(userInfo.checkUserExistance(USER_NAME, USER_PASS) == true) {
                new userInfo(USER_NAME, USER_EMAIL, USER_FULLNAME, USER_PASS, PHONE_NUMBER);
                sqLiteDBHelper.close();

                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
            }
        }

        this.screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        this.screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        viewActionListeners();
    }

    protected void selectLanguage() {
        currentLanguage = getIntent().getStringExtra(currentLang);

        spinner = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();

        list.add(" ");
        list.add("English");
       // list.add("አማርኛ");

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
    public void viewActionListeners() {
        Button openLoginActivity = (Button) findViewById(R.id.openLoginActivity);
        openLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        Button openRegisterActivity = (Button) findViewById(R.id.openRegisterActivity);
        openRegisterActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

}

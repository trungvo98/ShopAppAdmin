package com.example.razor_studios.shopappshoppers;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DeliverDetail extends AppCompatActivity {
    String itemID, userName,numItem;
    ImageView closeButton, itemIcon;
    public EditText userFullName, userStreetAddress, userHomeNumber, userKebele;
    public TextView itemTitle, itemPrice, itemNumPics;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_detail);

        itemID      = getIntent().getStringExtra("itemID");
        userName     = getIntent().getStringExtra("userName");
        numItem     = getIntent().getStringExtra("numItem");

        System.out.println("itemID : "+itemID);
        System.out.println("userName : "+userName);
        System.out.println("numItem : " +numItem);

        closeButton = (ImageView) findViewById(R.id.closeButton);
        itemIcon    = (ImageView) findViewById(R.id.itemIcon);
        itemTitle   = (TextView) findViewById(R.id.itemTitle);
        itemPrice   = (TextView) findViewById(R.id.itemPrice);
        userFullName = (EditText) findViewById(R.id.user_full_name);
        userStreetAddress = (EditText) findViewById(R.id.user_street_address);
        userHomeNumber    = (EditText) findViewById(R.id.user_home_number);
        userKebele        = (EditText) findViewById(R.id.user_kebele);
        itemNumPics       = (TextView) findViewById(R.id.itemNumPics);

       /* if(itemID == null) {
            onBackPressed();
        }*/
        fetchItem();
        fetchUser();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        viewOnClickListeners();
    }

    public void viewOnClickListeners() {
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void fetchItem() {
        itemNumPics.setText(numItem);
        try{
            Connection conn = DBConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs, rs1;
            String Query = "SELECT * FROM shopitemslist WHERE itemID=" + itemID;
            rs = stmt.executeQuery(Query);
            if(rs.next()) {
                itemTitle.setText(rs.getString("itemTitle")) ;
                itemPrice.setText(rs.getString("itemPrice")) ;
                itemIcon.setBackground(new BitmapDrawable(BitmapFactory.decodeByteArray(rs.getBytes("itemIcon"),0,rs.getBytes("itemIcon").length)));
            } else {
                onBackPressed();
            }
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    public void fetchUser() {
        try{
            Connection conn = DBConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs;
            String Query = "SELECT * FROM useraccount WHERE userName='" + userName + "'";
            rs = stmt.executeQuery(Query);
            if(rs.next()) {
               userFullName.setText(rs.getString("userFullName"));
               userStreetAddress.setText(rs.getString("streetAddress"));
               userHomeNumber.setText(rs.getString("homeNumber"));
               userKebele.setText(rs.getString("userKebele"));
            } else {
                onBackPressed();
            }
            conn.close();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.razor_studios.shopappshoppers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddItem extends AppCompatActivity implements View.OnClickListener {
    public Bitmap imageToUpload = null;
    public Uri imagePath;
    public EditText itemTitle, itemPrice, itemNumPics;
    public ImageView itemIcon, closeButton;
    public Button addToShop;

    String retMessage, itemCategory = "";
    Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        itemTitle   = (EditText) findViewById(R.id.itemTitle);
        itemPrice   = (EditText) findViewById(R.id.itemPrice);
        itemNumPics = (EditText) findViewById(R.id.itemNumPics);
        itemIcon    = (ImageView) findViewById(R.id.itemIcon);
        closeButton = (ImageView) findViewById(R.id.closeButton);
        addToShop   = (Button) findViewById(R.id.addToShop);

        dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"Loại món","Đồ ăn nhanh","Đồ nướng","Đồ hải sản","Nước ngọt"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        itemCategory = "";
                        break;
                    case 1:
                        itemCategory = "Đồ ăn nhanh";
                        break;
                    case 2:
                        itemCategory = "Đồ nướng";
                        break;
                    case 3:
                        itemCategory = "Đồ hải sản";
                        break;
                    case 4:
                        itemCategory = "Nước ngọt";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        addToShop.setOnClickListener(this);
        itemIcon.setOnClickListener(this);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void onClick(View view) {
        if(view == itemIcon) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        }

        if(view == addToShop) {
            try {
                addToDatabase();
                Toast.makeText(this, "" + retMessage, Toast.LENGTH_LONG).show();
            } catch (Exception ex) {}
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && data != null && data.getData() != null) {

            imagePath = data.getData();
            try {
                imageToUpload = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
                itemIcon.setBackground(new BitmapDrawable(imageToUpload));
            } catch (IOException ex){}
        }
    }
    public byte[] convertImage() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if(getFileExtension().toLowerCase().equals("jpg") || getFileExtension().toLowerCase().equals("jpeg")) {
            imageToUpload.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        } else {
            imageToUpload.compress(Bitmap.CompressFormat.PNG, 100, bos);
        }
        byte[] bArray = bos.toByteArray();
        return bArray;
    }
    public String getFileExtension() {
        String ext = "";
        for(int i=imagePath.toString().length() - 1; i>=0; i--) {
            if(imagePath.toString().charAt(i) == '.') {
                break;
            }
            ext = ext.concat(""+imagePath.toString().charAt(i));
        }
        return new StringBuffer(ext).reverse().toString();
    }
    public void addToDatabase() {
        Connection con = DBConnect.getConnection();

        String itemT = itemTitle.getText().toString();
        int itemNum  = Integer.parseInt(itemNumPics.getText().toString());
        Double itemP = Double.parseDouble(itemPrice.getText().toString());
        if(con == null) {
            retMessage = "Không thể kết nối đến server";
        } else if(imageToUpload == null || itemT.equals("") || itemNum <= 0 || itemP < 0 ) {
            retMessage = "Hãy điền tất cả các trường yêu cầu";
        } else {
            try {
                String Query = "INSERT INTO shopitemslist (itemTitle, itemIcon, itemPrice, itemCategory, itemLeft, itemProvider, adminName)" +
                        "VALUES(?,?,?,?,?,?,?) ";
                System.out.println(userInfo.USER_FULLNAME);
                System.out.println(userInfo.USER_NAME);
                PreparedStatement ps = con.prepareStatement(Query);
                ps.setString(1,itemT);
                ps.setBytes(2, convertImage());
                ps.setDouble(3, itemP);
                ps.setString(4, itemCategory);
                ps.setInt(5, itemNum);
                ps.setString(6, userInfo.USER_FULLNAME);
                ps.setString(7, userInfo.USER_NAME);
                ps.executeUpdate();
                retMessage = "Thêm thành công";
            } catch (Exception ex) {
                retMessage = "Có lỗi khi thêm . Vui lòng thử lại!";
                ex.printStackTrace();
            }
        }
    }
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
    }
}

package com.example.razor_studios.shopappshoppers;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.razor_studios.shopappshoppers.DBConnect;
import com.example.razor_studios.shopappshoppers.HomeActivity;
import com.example.razor_studios.shopappshoppers.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class OrdersDetail extends AppCompatActivity {
    private ImageView closeButton, minuButton, plusButton, itemIcon;
    private Button updateItemInfo;
    private Button deleteItem;
    public TextView itemTitle, itemLeft, itemCategory;
    public EditText itemNumPics, itemPrice;
    String itemID;
    int numPicsLeft = 0;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_detail);
        builder = new AlertDialog.Builder(this);
        itemID = getIntent().getStringExtra("itemID");
        if(itemID == null) {
            onBackPressed();
        }

        itemTitle    = (TextView) findViewById(R.id.itemTitle);
        minuButton   = (ImageView) findViewById(R.id.minusButton);
        plusButton   = (ImageView) findViewById(R.id.plusButton);
        itemNumPics  = (EditText) findViewById(R.id.itemNumPics);
        closeButton  = (ImageView) findViewById(R.id.closeButton);
        updateItemInfo    = (Button) findViewById(R.id.updateItemInfo);
        deleteItem    = (Button) findViewById(R.id.deleteItem);
        itemLeft     = (TextView) findViewById(R.id.itemLeft);
        itemCategory = (TextView) findViewById(R.id.itemCategory);
        itemPrice    = (EditText) findViewById(R.id.itemPrice);
        itemIcon     = (ImageView) findViewById(R.id.itemIcon);

        fetchItem();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        viewOnClickListeners();
    }

    public void viewOnClickListeners() {
        updateItemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateItem();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setMessage("Bạn có muốn xóa item này ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteItem();
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        minuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.parseInt(itemNumPics.getText().toString());
                if(num > 0) {
                    itemNumPics.setText("" + (num - 1));
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.parseInt(itemNumPics.getText().toString());
                itemNumPics.setText("" + (num + 1));
            }
        });
    }
    public void fetchItem() {
        try {
            Connection conn = DBConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs, rs1;
            String Query = "SELECT * FROM shopitemslist WHERE itemID=" + itemID;
            rs = stmt.executeQuery(Query);
            if(!rs.next()) {
                onBackPressed();
            } else {
                numPicsLeft = rs.getInt("itemLeft");
                itemTitle.setText(""+rs.getString("itemTitle"));
                itemCategory.setText(""+rs.getString("itemCategory"));
                itemLeft.setText(rs.getString("itemLeft") + " Còn lại");
                itemNumPics.setText(rs.getString("itemLeft") + "");
                itemPrice.setText(rs.getDouble("itemPrice") + " VNĐ ");

                byte[] icon = rs.getBytes("itemIcon");
                itemIcon.setBackground(new BitmapDrawable(BitmapFactory.decodeByteArray(icon,0,icon.length)));
            }
            conn.close();
        } catch (Exception ex) {
            Toast.makeText(this,""+ex.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    public void updateItem() {
        try {
            Connection conn = DBConnect.getConnection();
            Statement stmt = conn.createStatement();
            String numPics = itemNumPics.getText().toString(), price = itemPrice.getText().toString();
            if(numPics.equals("")) {
                numPics = "0";
            }
            if(price.equals("")) {
                price = "0";
            }

            String Query;
            Query = "UPDATE shopitemslist SET itemLeft=" + numPics + ", itemPrice=" + price + " WHERE itemID='" + itemID + "'";
            stmt.executeUpdate(Query);
            Toast.makeText(this, " Cập nhật thành công ", Toast.LENGTH_SHORT).show();
            conn.close();
        } catch (Exception ex) {
        }
    }

    public void deleteItem() {
        try {
            Connection conn = DBConnect.getConnection();
            Statement stmt = conn.createStatement();
            String Query;
            Query = "DELETE FROM shopitemslist WHERE itemID='" + itemID + "'";
            stmt.executeUpdate(Query);
            Toast.makeText(this, " Xóa thành công", Toast.LENGTH_SHORT).show();
            conn.close();
        } catch (Exception ex) {
        }
    }
}

package com.example.razor_studios.shopappshoppers;

import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {
    private ListView listOrders;
    private DisplayOrdersAdapter ordersAdapter;
    public TextView orderTotalOrder;
    public TextView orderPrice;
    public ImageView removeButton;
    public ConstraintLayout ordersActivity;
    public int itemID[], itemNumPics[], orderID[];
    public String userPayed[],state[];
    public ImageView closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(userInfo.checkUserExistance(userInfo.USER_NAME, userInfo.USER_PASS) != true) {
            System.out.println(userInfo.USER_NAME);
            System.out.println(userInfo.USER_PASS);
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }
        setContentView(R.layout.activity_orders);
        listOrders      = (ListView) findViewById(R.id.listview);
        ordersActivity  = (ConstraintLayout) findViewById(R.id.OrdersActivity);
        closeButton     = (ImageView) findViewById(R.id.closeButton);
        orderTotalOrder = (TextView) findViewById(R.id.orderTotalOrder);
        ordersActivity.setBackgroundColor(Color.rgb(245,245,245));
        ordersAdapter = new DisplayOrdersAdapter(getApplicationContext(), R.layout.orders_display_area);

        setElementListProperty();
        // count order not confirmed yet
        int a = countOrderDatabase();
        orderID = new int[a];
        userPayed = new String[a];
        itemID = new int[a];
        itemNumPics = new int[a];
        state = new String[a];

        System.out.println("number order :"+ a);
        //set data for order from db
        fetchOrders();

        for(int i=0; i<orderID.length; i++) {
            System.out.println(orderID[i]);
        }
        for(int i=0; i<userPayed.length; i++) {
            System.out.println(userPayed[i]);
        }
        for(int i=0; i<itemID.length; i++) {
            System.out.println(itemID[i]);
        }
        for(int i=0; i<itemNumPics.length; i++) {
            System.out.println(itemNumPics[i]);
        }

        for(int i=0; i<a; i++) {
            fetchItemsInfo(itemID[i],itemNumPics[i],orderID[i],state[i],userPayed[i],itemNumPics[i]);
        }
        orderTotalOrder.setText(""+a);
        viewOnClickListeners();
    }

    public void setElementListProperty() {
        listOrders.setAdapter(ordersAdapter);
        listOrders.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listOrders.setAdapter(ordersAdapter);
        ordersAdapter.registerDataSetObserver(new DataSetObserver() {
            public void onChanged() {
                super.onChanged();
                listOrders.setSelection(ordersAdapter.getCount() - 1);
            }
        });
    }

    public void viewOnClickListeners() {
        listOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    Intent intent = new Intent(getApplicationContext(), DeliverDetail.class);
                    TextView itemID = (TextView) view.findViewById(R.id.itemIDD);
                    TextView username = (TextView) view.findViewById(R.id.username);
                    TextView numItem = (TextView) view.findViewById(R.id.numItem);
                    intent.putExtra("itemID", itemID.getText());
                    intent.putExtra("userName", username.getText());
                    intent.putExtra("numItem", numItem.getText());
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    public int countOrderDatabase() {
        int a = 0;
        try {
            Connection conn = DBConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs;
            System.out.println("username:"+userInfo.USER_NAME);
            //String Query = "SELECT * FROM shoppay WHERE payedAdmin='" + userInfo.USER_NAME + "' AND paymentState='Not Confirmed'";
            String Query = "SELECT * FROM shopitemuser";
            rs = stmt.executeQuery(Query);
            while (rs.next()) {
                a++;
            }
            conn.close();
        } catch (Exception ex) {}
        return a;
    }
    public void fetchItemsInfo(int itemID, int itemNumPics, int orderID,String state,String userName,int numItem)  {
        try {
            Connection conn = DBConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs;
            String Query = "SELECT * FROM shopitemslist WHERE itemID=" + itemID;
            rs = stmt.executeQuery(Query);
            rs.next();
            //orderTotal = orderTotal + (itemNumPics * rs.getDouble("itemPrice"));
            displayOrders(orderID,(itemNumPics * rs.getDouble("itemPrice")), rs.getBytes("itemIcon"),state,userName,numItem,itemID);
            conn.close();
        } catch (Exception ex) {}
    }
    public void fetchOrdersDetail(String userName, int orderID, int index) {
        try {
            Connection conn = DBConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs;
            //String Query = "SELECT * FROM " + userName + " WHERE orderID=" + orderID;
            String Query = "SELECT * FROM shopitemuser WHERE orderID='" + orderID + "' ";
            rs = stmt.executeQuery(Query);
            if(rs.next()) {
                itemID[index]      = rs.getInt("itemID");
                itemNumPics[index] = rs.getInt("orderNumPics");
            }
            conn.close();
        } catch (Exception ex) {}
    }
    public void fetchOrders2() {
        int i = 0;
        try {
            Connection conn = DBConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs;
            //String Query = "SELECT * FROM shoppay WHERE payedAdmin='" + userInfo.USER_NAME + "' AND paymentState='Not Confirmed'";
            String Query = "SELECT * FROM shopitemuser WHERE orderState='Not Confirmed'";
            rs = stmt.executeQuery(Query);
            while(rs.next()) {
                System.out.println(rs.getInt("orderID"));
                System.out.println(rs.getString("userName"));
                orderID[i]   = rs.getInt("orderID");
                userPayed[i] = rs.getString("userName");
                i++;
            }
            conn.close();
            for(int j=0; j<i; j++) {
                    fetchOrdersDetail(userPayed[j], orderID[j], j);
            }
        } catch (Exception ex) {}
    }

    public void fetchOrders() {
        int i = 0;
        try {
            Connection conn = DBConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs;
            //String Query = "SELECT * FROM shoppay WHERE payedAdmin='" + userInfo.USER_NAME + "' AND paymentState='Not Confirmed'";
            String Query = "SELECT * FROM shopitemuser ";
            rs = stmt.executeQuery(Query);
            while(rs.next()) {
                orderID[i]   = rs.getInt("orderID");
                userPayed[i] = rs.getString("userName");
                itemID[i] = rs.getInt("itemID");
                itemNumPics[i] = rs.getInt("orderNumPics");
                state[i] =rs.getString("orderState");
                i++;
            }
            conn.close();
        } catch (Exception ex) {}
    }
    private boolean displayOrders(int orderID,double orderPrice, byte[] itemIcon,String stateOrder,String userName,int numItem, int itemID) {
        ordersAdapter.add(new DisplayOrders(orderID,orderPrice, itemIcon, stateOrder,userName,numItem,itemID));
        return true;
    }
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
    }
}

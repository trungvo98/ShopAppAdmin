package com.example.razor_studios.shopappshoppers;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.sql.Connection;
import java.sql.Statement;

public class DisplayOrders {
    public String orderProvider,stateOrder,userName;
    int itemID, orderID, orderNumPics;
    double orderPrice;
    public Drawable orderIcon;

    public DisplayOrders(int orderID, double orderPrice, byte[] itemIcon , String stateOrder,String username,int orderNumPics,int itemID) {
        super();
        this.orderID = orderID;
        this.orderPrice = orderPrice;
        this.stateOrder = stateOrder;
        this.orderIcon = new BitmapDrawable(BitmapFactory.decodeByteArray(itemIcon, 0, itemIcon.length));
        this.userName = username;
        this.orderNumPics=orderNumPics;
        this.itemID = itemID;
    }
}

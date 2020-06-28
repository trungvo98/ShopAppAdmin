package com.example.razor_studios.shopappshoppers;

import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class DisplayItems {
    public String itemTitle, itemID;
    Double itemPrice;
    public Drawable itemIcon;
    public DisplayItems(String itemID,String itemTitle,Double itemPrice, byte[] itemIcon) {
        super();
        this.itemID       = itemID;
        this.itemTitle    = itemTitle;
        this.itemPrice    = itemPrice;
        this.itemIcon     = new BitmapDrawable(BitmapFactory.decodeByteArray(itemIcon,0,itemIcon.length));
    }
}

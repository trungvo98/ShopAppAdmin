package com.example.razor_studios.shopappshoppers;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DisplayItemsAdapter extends ArrayAdapter<DisplayItems> {
    private TextView itemTitle, itemPrice, itemID;
    private ImageView itemIcon;
    private List<DisplayItems> itemList = new ArrayList<>();
    private LinearLayout layout;

    public DisplayItemsAdapter(Context context, int textViewResourceId) {
        super(context,textViewResourceId);
    }
    public void add(DisplayItems obj) {
        itemList.add(obj);
        super.add(obj);
    }
    public int getCount() {
        return this.itemList.size();
    }
    public DisplayItems getItem(int index) {
        return this.itemList.get(index);
    }
    public View getView(int position, View ConvertView, ViewGroup parent) {
        View v = ConvertView;
        if(v == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_display_area, parent, false);
        }
        layout       = (LinearLayout) v.findViewById(R.id.elementDisplayLayout);
        itemTitle    = (TextView) v.findViewById(R.id.itemTitle);
        itemPrice    = (TextView) v.findViewById(R.id.itemPrice);
        itemIcon     = (ImageView) v.findViewById(R.id.itemIcon);
        itemID       = (TextView) v.findViewById(R.id.itemID);

        final DisplayItems disObj = getItem(position);
        itemTitle.setText(""+disObj.itemTitle);
        itemPrice.setText(disObj.itemPrice + " " + parent.getResources().getString(R.string.orders_currenc));
        itemID.setText(""+disObj.itemID);
        itemIcon.setBackground(disObj.itemIcon);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixelOne = (int) (30 * scale + 0.5f);
        int pixelTwo = (int) (130 * scale + 0.5f);

        int screenWidth = (int) MainActivity.screenWidth / 2;
        layout.setBackgroundColor(Color.rgb(240,240,240));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                pixelTwo,
                pixelTwo
        );

        int imageLayoutWidth = (int) (screenWidth - (pixelOne + pixelTwo)) / 2;
        params.setMargins(imageLayoutWidth,0,0,0);

        itemIcon.setLayoutParams(params);
        return v;
    }
}

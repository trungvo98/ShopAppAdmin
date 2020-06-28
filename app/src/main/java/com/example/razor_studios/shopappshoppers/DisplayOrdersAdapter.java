package com.example.razor_studios.shopappshoppers;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DisplayOrdersAdapter extends ArrayAdapter<DisplayOrders>{
    private ImageView orderIcon;
    private TextView orderTitle, numItem, itemID, orderID , orderPrice , stateOrder,userName;
    private List<DisplayOrders> ordersList = new ArrayList<>();
    LinearLayout ordersDetail;

    public DisplayOrdersAdapter(Context context, int textViewResourceId) {
        super(context,textViewResourceId);
    }
    public void add(DisplayOrders obj) {
        ordersList.add(obj);
        super.add(obj);
    }
    public int getCount() {
        return this.ordersList.size();
    }
    public DisplayOrders getItem(int index) {
        return this.ordersList.get(index);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public View getView(final int position, View ConvertView, final ViewGroup parent) {
        View v = ConvertView;
        if(v == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.orders_display_area, parent, false);
        }
        orderID   = (TextView) v.findViewById(R.id.orderIdd);
        orderIcon    = (ImageView) v.findViewById(R.id.orderIcon);
        stateOrder = (TextView) v.findViewById(R.id.stateOrder);
        ordersDetail = (LinearLayout) v.findViewById(R.id.ordersDetail);
        itemID       = (TextView) v.findViewById(R.id.itemIDD);
       // orderID      = (TextView) v.findViewById(R.id.orderID);
        orderPrice   =(TextView) v.findViewById(R.id.orderPrice);
        userName =(TextView) v.findViewById(R.id.username);
        numItem =(TextView) v.findViewById(R.id.numItem);
        final DisplayOrders disObj = getItem(position);
        //orderTitle.setText(disObj.orderID);
        orderIcon.setBackground(disObj.orderIcon);
        //orderNumPics.setText(disObj.orderNumPics+"pcs");
        itemID.setText(""+disObj.itemID);
        orderID.setText(""+disObj.orderID);
        orderPrice.setText(""+disObj.orderPrice);
        stateOrder.setText(disObj.stateOrder);
        userName.setText(disObj.userName);
        numItem.setText(""+disObj.orderNumPics);

        if (disObj.stateOrder.equalsIgnoreCase("deleted")) {
            stateOrder.setTextColor(Color.RED);
        }else if (disObj.stateOrder.equalsIgnoreCase("completed")){
            stateOrder.setTextColor(Color.BLUE);
        }else{
            stateOrder.setTextColor(Color.GREEN);
        }
        return v;
    }
}

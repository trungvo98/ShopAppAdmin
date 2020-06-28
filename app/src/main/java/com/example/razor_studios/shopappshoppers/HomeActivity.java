package com.example.razor_studios.shopappshoppers;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ListView listItems, listItemsTwo,listItemsNew;
    private DisplayItemsAdapter listAdapter, listAdapterTwo;
    private ImageView toogleBar, viewCart;
    private DrawerLayout mDrawerLayout;
    public int QueueChecker = 0, itemFetchNumber = 0, numOfItems, itemsPrinted = 0;
    public String[] itemTitle;
    public Double  itemPrice;
    ScrollView scrollView;
    boolean isResultFound = false;

    Bitmap imageToUpload;

    Spinner spinner;
    Locale myLocale;
    String currentLanguage = "en", currentLang, userLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(userInfo.checkUserExistance(userInfo.USER_NAME, userInfo.USER_PASS) != true) {
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

        setContentView(R.layout.activity_home);
        selectLanguage();
        setActivityLisener();
        getAllViews();
        viewOnClickListeners();
        setListViewAdpaterProperty();

        itemFetchNumber = getNumberOfItems();
        numOfItems = itemFetchNumber;
        fetchItemsInfo();
        if(isResultFound == true) {
            isResultFound = false;
        }
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
                        updateLanguage("en");
                        setLocale("en");
                        break;
                    case 2:
                        updateLanguage("et");
                        setLocale("et");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    public void updateLanguage(String userLang) {
        SQLiteDBHelper sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = sqLiteDBHelper.getWritableDatabase();
        sqLiteDBHelper.updateUserLanguage(userLang,userInfo.USER_NAME,sqLiteDatabase);
        sqLiteDBHelper.close();
    }
    public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            myLocale = new Locale(localeName);
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(this, HomeActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        }
    }
    public void setListViewAdpaterProperty() {
        listAdapter = new DisplayItemsAdapter(getApplicationContext(), R.layout.item_display_area);
        setItemListProperty();
        listAdapterTwo = new DisplayItemsAdapter(getApplicationContext(), R.layout.item_display_area);
        setItemListTwoProperty();

        final float scale = this.getResources().getDisplayMetrics().density;
        int pixelOne = (int) (250 * scale + 0.5f);
        int pixelTwo = (int) (150 * scale + 0.5f);

        LinearLayout.LayoutParams startIconParam = setLayoutParams(pixelOne, pixelTwo);
        startIconParam.setMargins((MainActivity.screenWidth - pixelOne) / 2, 0 ,0,0);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MainActivity.screenWidth / 2, LinearLayout.LayoutParams.FILL_PARENT);

        listItems.setLayoutParams(params);
        listItemsTwo.setLayoutParams(params);
    }
    public void viewOnClickListeners() {
        viewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OrdersActivity.class);
                startActivity(intent);
            }
        });
        toogleBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            public void onScrollChanged() {
                if (scrollView.getChildAt(0).getBottom() == (scrollView.getHeight() + scrollView.getScrollY())) {
                    if((listItems.getCount() + listItemsTwo.getCount()) < numOfItems) {
                        fetchItemsInfo();
                    }
                }
            }
        });

        listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), OrdersDetail.class);
                TextView itemID = (TextView) view.findViewById(R.id.itemID);

                intent.putExtra("itemID",itemID.getText());
                startActivity(intent);
            }
        });

        listItemsTwo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),OrdersDetail.class);
                TextView itemID = (TextView) view.findViewById(R.id.itemID);

                intent.putExtra("itemID",itemID.getText());
                startActivity(intent);
            }
        });
    }
    public void getAllViews() {
        scrollView    = (ScrollView) findViewById(R.id.scrollView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        listItems     = (ListView) findViewById(R.id.listview);
        listItemsTwo  = (ListView) findViewById(R.id.listview_two);
        toogleBar     = (ImageView) findViewById(R.id.toogleBar);
        viewCart      = (ImageView) findViewById(R.id.viewCart);
        //new add
        //listItemsNew      = (ListView) findViewById(R.id.listitem);
    }
    public int getNumberOfItems()  {
        int numItems = 0;
        try {
            Connection conn = DBConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs;

            String Query = "SELECT COUNT(*) FROM shopitemslist WHERE adminName='" + userInfo.USER_NAME + "'";
            rs = stmt.executeQuery(Query);
            rs.next();
            numItems = rs.getInt(1);
            conn.close();
        } catch (Exception ex) {}
        return numItems;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.navOrders) {
            startActivity(new Intent(this,OrdersActivity.class));
        } else if (id == R.id.navProfile) {
            startActivity(new Intent(this,ProfileActivity.class));
        } else if (id == R.id.navHelp) {
        } else if (id == R.id.navAbout) {
            startActivity(new Intent(this, AboutActivity.class));
        } else if(id == R.id.navNew) {
            startActivity(new Intent(this, AddItem.class));
        }else if(id == R.id.navSignOut) {
            SQLiteDBHelper sqLiteDBHelper = new SQLiteDBHelper(getApplicationContext());
            SQLiteDatabase sqLiteDatabase = sqLiteDBHelper.getReadableDatabase();
            sqLiteDBHelper.removeAccount(userInfo.USER_NAME,sqLiteDatabase);
            userInfo.USER_NAME = "";
            userInfo.USER_EMAIL = "";
            userInfo.USER_PASS = "";
            userInfo.USER_FULLNAME = "";
            userInfo.PHONE_NUMBER = "";

            startActivity(new Intent(this,LoginActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private boolean displayItems(String itemID, String itemTitle, Double itemPrice, byte[] itemIcon) {
        listAdapter.add(new DisplayItems(itemID, itemTitle, itemPrice, itemIcon));
        return true;
    }
    private boolean displayItemsTwo(String itemID, String itemTitle, Double itemPrice, byte[] itemIcon) {
        listAdapterTwo.add(new DisplayItems(itemID, itemTitle, itemPrice, itemIcon));
        return true;
    }
    public void setItemListProperty() {
        listItems.setAdapter(listAdapter);
        listItems.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listItems.setAdapter(listAdapter);
        listAdapter.registerDataSetObserver(new DataSetObserver() {
            public void onChanged() {
                super.onChanged();
                listItems.setSelection(listAdapter.getCount() - 1);
            }
        });
    }
    public void setItemListTwoProperty() {
        listItemsTwo.setAdapter(listAdapterTwo);
        listItemsTwo.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listItemsTwo.setAdapter(listAdapterTwo);
        listAdapterTwo.registerDataSetObserver(new DataSetObserver() {
            public void onChanged() {
                super.onChanged();
                listItemsTwo.setSelection(listAdapterTwo.getCount() - 1);
            }
        });
    }
    public void setActivityLisener() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public void fetchItemsInfo() {
        int curLeftItems;
        try {
            Connection conn = DBConnect.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs;

            numOfItems = getNumberOfItems();

            curLeftItems = itemFetchNumber - 8;
            String Query = "SELECT * FROM shopitemslist WHERE adminName='" + userInfo.USER_NAME + "' ORDER BY itemID DESC";

            rs = stmt.executeQuery(Query);
            int i = 0;
            while (rs.next() && curLeftItems != itemFetchNumber) {
                isResultFound = true;
                if(i >= itemsPrinted) {
                    itemFetchNumber--;
                    if (QueueChecker == 0) {
                        displayItems("" + rs.getInt("itemID"), rs.getString("itemTitle"), rs.getDouble("itemPrice"), rs.getBytes("itemIcon"));
                        QueueChecker = 1;
                    } else {
                        displayItemsTwo("" + rs.getInt("itemID"), rs.getString("itemTitle"), rs.getDouble("itemPrice"), rs.getBytes("itemIcon"));
                        QueueChecker = 0;
                    }
                }i++;
            }
            itemsPrinted+=8;
            conn.close();
        } catch (Exception ex) {

            Toast.makeText(this, ""+ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public static LinearLayout.LayoutParams setLayoutParams(int Width, int Height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                Width,
                Height
        );
        return params;
    }

}



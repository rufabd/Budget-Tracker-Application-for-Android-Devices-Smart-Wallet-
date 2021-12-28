package com.example.app_dev_money_tracking;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    private LinearLayout category_text_linear;

    private RecyclerView categoryRecycler;
    private CategoriesAdapter adapter;

    private List<Categories> categoriesList;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        User_settings settings = User_settings.instanciate("user1", getApplicationContext());
        Database db = new Database(this);
        UserModel user = db.getUserByEmail(settings.getUserEmail());

        categoryRecycler = findViewById(R.id.recViewCategories);
        categoriesList = db.getCategories();
//        if (categoriesList == null) {
//            categoriesList = Categories.getData(this);
//        }
        adapter = new CategoriesAdapter(categoriesList, this);
        categoryRecycler.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        categoryRecycler.setLayoutManager(manager);
        categoryRecycler.setAdapter(adapter);

        // Menu navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_categories);
        View header = navigationView.getHeaderView(0);
        TextView emailDisplay = header.findViewById(R.id.userEmailDisplay);
        User_settings user_settings = User_settings.instanciate("user1", this);
        ImageView imageDisplay = header.findViewById(R.id.userImageDisplay);
        String facebookId= db.getUserByEmail(user_settings.getUserEmail()).getFbid();
        if(!facebookId.equals("")){

            Picasso.get().load("https://graph.facebook.com/"+facebookId+"/picture?type=large").into(imageDisplay);
        }
        emailDisplay.setText(user_settings.getUserEmail());
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(CategoriesActivity.this, Home_activity.class));
                    break;
                case R.id.nav_new_record:
                    startActivity(new Intent(CategoriesActivity.this, NewRecord.class));
                    break;
                case R.id.nav_categories:
                    startActivity(new Intent(CategoriesActivity.this, CategoriesActivity.class));
                    break;
                case R.id.nav_converter:
                    if (user.getAdmin() == 0) {
                        Toast.makeText(CategoriesActivity.this, "This feature only available for premium members", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(CategoriesActivity.this, Convert_currency_activity.class));
                    }
                    break;

                case R.id.nav_receipts:
                    startActivity(new Intent(CategoriesActivity.this, ReceiptGallery.class));
                    break;
                case R.id.nav_tryPremium:
                    startActivity(new Intent(CategoriesActivity.this, PremiumContent.class));
                    break;
                case R.id.nav_logout:
                    startActivity(new Intent(CategoriesActivity.this, Logout.class));
                    break;
                case R.id.nav_myPlannedPayments:
                    startActivity(new Intent(CategoriesActivity.this, PlannedPayments.class));
                    break;
            }
            return true;
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

}
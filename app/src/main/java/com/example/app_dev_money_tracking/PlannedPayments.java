package com.example.app_dev_money_tracking;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PlannedPayments extends AppCompatActivity {

    private Button btn_open_new_planned_payment;
    private TextView tv_no_any_planned;

    UserModel user;

    public void init() {
        btn_open_new_planned_payment = (Button) findViewById(R.id.btn_open_new_planned_payment);
        tv_no_any_planned = (TextView) findViewById(R.id.tv_not_any_planned);
    }

    //    DatabaseAdapter databaseAdapter;
    Database plannedPaymentsDB = new Database(this);
    RecyclerView rv_planned_payments;
    PlannedPaymentsAdapter plannedPaymentsAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<PlannedPaymentsModel> paymentsList;
    private Database db;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planned_payments);
        init();

        User_settings user_settings = User_settings.instanciate("user1", getApplicationContext());
        db = new Database(this);
        user = db.getUserByEmail(user_settings.getUserEmail());

        int balance = user.getBalance();
        paymentsList = plannedPaymentsDB.getPlannedPayments();
        rv_planned_payments = findViewById(R.id.rvPlannedPayments);
        rv_planned_payments.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rv_planned_payments.setLayoutManager(layoutManager);
        plannedPaymentsAdapter = new PlannedPaymentsAdapter(this, paymentsList, rv_planned_payments);
        rv_planned_payments.setAdapter(plannedPaymentsAdapter);

        compareDates(paymentsList, plannedPaymentsDB, balance);





        btn_open_new_planned_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PlannedPayments.this, NewPlannedPayment.class));
            }
        });

        // Menu navigation

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_myPlannedPayments);
        View header = navigationView.getHeaderView(0);
        TextView emailDisplay = header.findViewById(R.id.userEmailDisplay);
        ImageView imageDisplay = header.findViewById(R.id.userImageDisplay);
        String facebookId= db.getUserByEmail(user_settings.getUserEmail()).getFbid();
        if(!facebookId.equals("")){
            Picasso.get().load("https://graph.facebook.com/"+facebookId+"/picture?type=large").into(imageDisplay);
        }
        emailDisplay.setText(user_settings.getUserEmail());
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_home:
                    startActivity(new Intent(PlannedPayments.this, Home_activity.class));
                    break;
                case R.id.nav_new_record:
                    startActivity(new Intent(PlannedPayments.this, NewRecord.class));
                    break;
                case R.id.nav_categories:
                    startActivity(new Intent(PlannedPayments.this, CategoriesActivity.class));
                    break;
                case R.id.nav_converter:
                    if (user.getAdmin() == 0) {
                        Toast.makeText(PlannedPayments.this, "This feature only available for premium members", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(PlannedPayments.this, Convert_currency_activity.class));
                    }
                    break;
                case R.id.nav_receipts:
                    startActivity(new Intent(PlannedPayments.this, ReceiptGallery.class));
                    break;
                case R.id.nav_tryPremium:
                    startActivity(new Intent(PlannedPayments.this, PremiumContent.class));
                    break;
                case R.id.nav_logout:
                    startActivity(new Intent(PlannedPayments.this, Logout.class));
                    break;
                case R.id.nav_myPlannedPayments:
                    startActivity(new Intent(PlannedPayments.this, PlannedPayments.class));
                    break;

            }

            return true;
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //
    }

    public void compareDates(List<PlannedPaymentsModel> list, Database db, int balance) {

        int expenses = 0;
        Date currentDate = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedCurrentDate = formatter.format(currentDate);
//        System.out.println("Current date: " + formattedCurrentDate);

        if(paymentsList != null) {
            for(int i = 0; i < paymentsList.size(); i++) {
                String savedDate = paymentsList.get(i).getDate();
                PlannedPaymentsModel plannedPaymentsModel = new PlannedPaymentsModel(paymentsList.get(i).getId(), paymentsList.get(i).getNote(), paymentsList.get(i).getAmount(),
                        paymentsList.get(i).getCategoryId(), paymentsList.get(i).getStatus(), paymentsList.get(i).getCurrency(), paymentsList.get(i).getRecordType(), paymentsList.get(i).getDate());
                if(savedDate.equals(formattedCurrentDate)) {
//                    System.out.println(savedDate);
                    paymentsList.get(i).setStatus("Done");
                    plannedPaymentsModel.setStatus(paymentsList.get(i).getStatus());
                    expenses += paymentsList.get(i).getAmount();
                    balance -= expenses;
                    boolean done = db.updateData(plannedPaymentsModel);
                    if(done == true) {
                        Toast.makeText(this, "Status and balance updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Nothing to be updated", Toast.LENGTH_SHORT).show();
                    }
                } else if(!savedDate.equals(formattedCurrentDate) && paymentsList.get(i).getStatus() == "Done") {
                    paymentsList.get(i).setStatus("Done");
                    plannedPaymentsModel.setStatus(paymentsList.get(i).getStatus());
                    boolean done = db.updateData(plannedPaymentsModel);
                }

            }
        } else {
            tv_no_any_planned.setVisibility(View.VISIBLE);
        }
    }
}














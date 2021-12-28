package com.example.app_dev_money_tracking;


import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_dev_money_tracking.RecordTypeModel.RecordTypeKey;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import java.util.Calendar;

import java.util.Currency;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Home_activity extends AppCompatActivity {
    private PieChart pieChart;
    private List<RecordsModel> records;
    private List<PlannedPaymentsModel> plannedPayments;
    private RecyclerView Records_recycler;
    private User_settings user_settings;
    private DrawerLayout drawer;

    private Button show_more;


    UserModel user;
    Database db;
    RecyclerView recordsFromPie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user_settings = User_settings.instanciate("user1", this);
//      Create notification channel
        createNotificationChannel("TrackReminder",
                "Notifications from money tracking app",
                Notification.CHANNEL_ID);

        db = new Database(this);
        user = db.getUserByEmail(user_settings.getUserEmail());
        int balance = user.getBalance();
        String userCurrency = user.getCurrency();
        records = db.getRecords();
        plannedPayments = db.getPlannedPayments();
        EditText balanceText = findViewById(R.id.homeBalanceDisplay);
        Button adjustBalance = findViewById(R.id.btn_adjust_b);
        show_more = findViewById(R.id.Btn_show_more);
        TextView calculatedBalance = findViewById(R.id.calculatedBalance);
        recordsFromPie = findViewById(R.id.recordsFromPie);
        Button addRecordHomeButton = findViewById(R.id.add_record_home_button);
        addRecordHomeButton.setOnClickListener(onAddRecordButtonClick());


        adjustBalance.setOnClickListener(v -> {
            HelperFunctions.hideSoftKeyboard(Home_activity.this, v);
            user.setBalance(Integer.parseInt(balanceText.getText().toString()));
            boolean inserted = db.updateUser(user);
            if (inserted) {
                Toast.makeText(Home_activity.this, "Balance updated", Toast.LENGTH_SHORT).show();
                calculatedBalance.setText(String.valueOf(calculateCurrentBalance(records, user.getBalance(), plannedPayments)));
            } else {
                Toast.makeText(Home_activity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        });

        show_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call notification
                Notification.ShowNotification("Smart wallet", "Don't forget to track your daily finances!",
                        NewRecord.class, Notification.notificationID, getApplicationContext());
                startActivity(new Intent(Home_activity.this, All_records.class));
            }
        });

        balanceText.setText(String.valueOf(balance));
        Currency c = Currency.getInstance(user.getCurrency());
        String currencySymbol = c.getSymbol();
        calculatedBalance.setText(String.valueOf(calculateCurrentBalance(records, balance, plannedPayments) + currencySymbol));

        user_settings.set_currency("EUR");

        pieChart = findViewById(R.id.Piechart_view);
        Records_recycler = findViewById(R.id.Rec_view_expanses);
        SetupPieChart();
        loadData();

        setAdapters();

        // Menu navigation

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);
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
                    startActivity(new Intent(Home_activity.this, Home_activity.class));
                    break;
                case R.id.nav_new_record:
                    startActivity(new Intent(Home_activity.this, NewRecord.class));
                    break;
                case R.id.nav_categories:
                    startActivity(new Intent(Home_activity.this, CategoriesActivity.class));
                    break;
                case R.id.nav_converter:
                    if (user.getAdmin() == 0) {
                        Toast.makeText(Home_activity.this, "This feature only available for premium members", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(Home_activity.this, Convert_currency_activity.class));
                    }
                    break;
                case R.id.nav_receipts:
                    startActivity(new Intent(Home_activity.this, ReceiptGallery.class));
                    break;
                case R.id.nav_tryPremium:
                    startActivity(new Intent(Home_activity.this, PremiumContent.class));
                    break;
                case R.id.nav_logout:
                    startActivity(new Intent(Home_activity.this, Logout.class));
                    break;
                case R.id.nav_myPlannedPayments:
                    startActivity(new Intent(Home_activity.this, PlannedPayments.class));
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

    private int calculateCurrentBalance(List<RecordsModel> records, int balance, List<PlannedPaymentsModel> planneds)
    {
        int expences = 0;
        int intakes = 0;
        NumberFormat formatter = new DecimalFormat("#0.00");
        if (records != null) {
            for (RecordsModel record : records) {
                double amount = record.getAmount();
                if (record.getRecordType().equals(RecordTypeKey.E)) {
                    if (!user.getCurrency().equals(record.getCurrency())) {
                        Currency_conversion_data curr = new Currency_conversion_data(Home_activity.this);
                        amount = Double.parseDouble(formatter.format(curr.convert(record.getCurrency(), user.getCurrency(), amount)));
                    }
                    expences += amount;
                } else {
                    if (!user.getCurrency().equals(record.getCurrency())) {
                        Currency_conversion_data curr = new Currency_conversion_data(Home_activity.this);
                        amount = Double.parseDouble(formatter.format(curr.convert(record.getCurrency(), user.getCurrency(), amount)));
                    }
                    intakes += amount;
                }
            }

        }
        if(planneds != null) {
            for(PlannedPaymentsModel plannedPayment : planneds) {
                double plannedAmount = plannedPayment.getAmount();
                if(plannedPayment.getStatus().equals("Done")) {
                    expences += plannedAmount;
                }
            }
        }
        return balance - expences + intakes;
//        return balance;
    }

    private View.OnClickListener onAddRecordButtonClick() {
        return v -> startActivity(new Intent(Home_activity.this, NewRecord.class));
    }


    private void setAdapters() {
        Expanse_list_adapter adapter_exp = new Expanse_list_adapter(Home_activity.this, records);
        RecyclerView.LayoutManager layout_manager2 = new LinearLayoutManager(getApplicationContext());
        Records_recycler.setLayoutManager(layout_manager2);
        Records_recycler.setItemAnimator(new DefaultItemAnimator());
        Records_recycler.setAdapter(adapter_exp);
    }


    private void SetupPieChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.setAlpha(0.85f);
//        pieChart.setEntryLabelColor(Color.BLACK);
//        pieChart.setEntryLabelTextSize(10);
//        pieChart.setCenterText("Expanses");
        pieChart.setHoleRadius(10f);
        pieChart.setTransparentCircleRadius(15f);
        pieChart.setCenterTextSize(12);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setTextColor(Color.WHITE);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadData() {
        List<Categories> categories = db.getCategories();
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (records != null) {
            Map<Integer, Long> counted = records.stream()
                    .collect(Collectors.groupingBy(RecordsModel::getCategoryId, Collectors.counting()));


            int totalRecords = records.size();

            counted.forEach((key, value) -> {
                Categories cat = categories.stream().filter(c -> c.getId() == key).findAny().get();
                float weight = value.floatValue() / totalRecords;
                entries.add(new PieEntry(weight, cat.getCategoryName()));

            });
        }

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }
        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }
        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(colors);

        PieData data = new PieData(dataset);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));

        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        data.setHighlightEnabled(true);

        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.animateY(1400, Easing.EaseInOutQuad);
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                ArrayList<RecordsModel> records2 = new ArrayList<>();
                int hIndex = (int) h.getX();
                PieEntry entry = entries.get(hIndex);
                String label = entry.getLabel();
                for (RecordsModel record : records) {
                    int recordId = record.getCategoryId();
                    String c = db.getCategoryById(recordId).getCategoryName();
                    if (c.equals(label))
                        records2.add(record);
                }
                Expanse_list_adapter adapter_exp = new Expanse_list_adapter(Home_activity.this, records2);
                RecyclerView.LayoutManager layout_manager2 = new LinearLayoutManager(getApplicationContext());
                recordsFromPie.setLayoutManager(layout_manager2);
                recordsFromPie.setItemAnimator(new DefaultItemAnimator());
                recordsFromPie.setAdapter(adapter_exp);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Creating a channel for Notifications
    private void createNotificationChannel(String channel_name, String channel_description, String CHANNEL_ID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channel_name;
            String description = channel_description;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
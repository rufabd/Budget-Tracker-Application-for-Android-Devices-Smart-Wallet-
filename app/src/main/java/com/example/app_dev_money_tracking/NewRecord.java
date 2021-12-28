package com.example.app_dev_money_tracking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.compose.ui.text.intl.Locale;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app_dev_money_tracking.RecordTypeModel.RecordTypeKey;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewRecord extends AppCompatActivity {

    //    private ImageView imgViewClose, ImgViewDone;
    private TextView txtOperationRecord, txtCurrencyRecord, txtCategoryChoose, txtChosenCategory;
    private TextView txtAccountChoose, txtChosenAccount;
    private Button btnIncome, btnExpense, btnAddRecord, btn_open_category_input;
    private EditText editTxtAmount;
    private TextView categoryName, error;

    private LinearLayout category_text_linear;

    private RecyclerView categoryRecycler;
    private CategoriesAdapter adapter;

    private List<Categories> categoriesList;
    private DrawerLayout drawer;
    private int selectedCategoryId = -1;
    private RecordTypeKey selectedRecordType = RecordTypeKey.E;

    ArrayList<Integer> pictures = new ArrayList<>(
            Arrays.asList(R.drawable.charity, R.drawable.clotheshanger, R.drawable.commission, R.drawable.debt, R.drawable.dumbbell,
                    R.drawable.family, R.drawable.fastfoodd, R.drawable.giftboxwithbow, R.drawable.governance, R.drawable.graduationcapp,
                    R.drawable.healthcare, R.drawable.housedecorationn, R.drawable.insurance, R.drawable.investment,
                    R.drawable.key, R.drawable.movies, R.drawable.pawprint, R.drawable.piggybankk, R.drawable.salary, R.drawable.subscription,
                    R.drawable.utilities, R.drawable.car, R.drawable.shoppingbag));
    GridView gridView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        User_settings settings = User_settings.instanciate("user1", getApplicationContext());
        Database db = new Database(this);
        UserModel user = db.getUserByEmail(settings.getUserEmail());

        btn_open_category_input = (Button) findViewById(R.id.open_category_input);
//        imgViewClose = (ImageView)findViewById(R.id.imageClose);
//        ImgViewDone = (ImageView)findViewById(R.id.imageDone);
        btnIncome = (Button) findViewById(R.id.btnRecordTypeIncome);
        btnExpense = (Button) findViewById(R.id.btnRecordTypeExpense);
        btnAddRecord = findViewById(R.id.add_record);
        editTxtAmount = (EditText) findViewById(R.id.editAmount);
        txtOperationRecord = (TextView) findViewById(R.id.operationSignRecord);
        txtCategoryChoose = (TextView) findViewById(R.id.categoryChooseHeading);
        txtChosenCategory = (TextView) findViewById(R.id.chosenCategory);
        category_text_linear = (LinearLayout) findViewById(R.id.text_layout_category_item);
        categoryName = (TextView) findViewById(R.id.categories_item_name);
        gridView = findViewById(R.id.simpleGridView);
        TextView currencyDisplay = findViewById(R.id.recordCurrency);
        error = findViewById(R.id.new_record_error);
        currencyDisplay.setText(user.getCurrency());


        btnExpense.setBackgroundColor(Color.rgb(153, 50, 204));
        editTxtAmount.setMovementMethod(null);


        categoryRecycler = (RecyclerView) findViewById(R.id.CategoriesRecycle);
        categoriesList = db.getCategories();
//        if (categoriesList == null) {
//            categoriesList = Categories.getData(this);
//        }
        adapter = new CategoriesAdapter(categoriesList, this);
        categoryRecycler.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        categoryRecycler.setLayoutManager(manager);
        categoryRecycler.setAdapter(adapter);

        adapter.setOnItemClickListener(new CategoriesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Categories categories, int position) {
                Toast.makeText(getApplicationContext(), "Category " + categories.getCategoryName() + " was chosen", Toast.LENGTH_SHORT).show();
                txtChosenCategory.setText(categories.getCategoryName());
                selectedCategoryId = position + 1;
            }
        });


        // Actions for user creating new category
        btn_open_category_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewRecord.this);
                final View categoryLayout = getLayoutInflater().inflate(R.layout.user_category_input_layout, null);
                IconAdapter iconAdapter = new IconAdapter(NewRecord.this, pictures);
//                final View chooseIcon = getLayoutInflater().inflate(R.layout.user_category_input_layout, null);
                builder.setView(categoryLayout); // setting icons view in the alert box
                gridView = categoryLayout.findViewById(R.id.simpleGridView);
                gridView.setAdapter(iconAdapter);
                builder.setView(categoryLayout);

                final int[] iconId = {pictures.get(0)};
                gridView.setOnItemClickListener((adapterView, view1, i, l) -> {
                    ImageView image = view1.findViewById(R.id.categoryIcon);
                    image.setBackgroundColor(Color.GRAY);
                    iconId[0] = pictures.get(i);
                });

                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText cat_name_input = categoryLayout.findViewById(R.id.category_input);
                        if (cat_name_input.getText().toString().length() != 0) {
                            Toast.makeText(NewRecord.this, "Your category was created", Toast.LENGTH_SHORT).show();
                            Categories newCategory = new Categories(-1, cat_name_input.getText().toString(), iconId[0]);

                            categoriesList.add(newCategory);
                            db.addCategory(newCategory);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(NewRecord.this, "Category name can't be empty. Try again", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(NewRecord.this, "", Toast.LENGTH_SHORT);
                });

                builder.setTitle("New Category");
                builder.setMessage("Add your new category");


                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btnExpense.setOnClickListener(view -> {
            btnExpense.setBackgroundColor(Color.rgb(153, 50, 204));
            btnIncome.setBackgroundColor(Color.rgb(121, 60, 201));
            txtOperationRecord.setText("-");
            txtCategoryChoose.setText("Category");
            txtChosenCategory.setText("Select Category Below");
            selectedRecordType = RecordTypeKey.E;
//              txtAccountChoose.setText("Account");
        });
//        ImgViewDone.setOnClickListener(view -> startActivity(new Intent(NewRecord.this, Home_activity.class)));
//        imgViewClose.setOnClickListener(view -> startActivity(new Intent(NewRecord.this, Home_activity.class)));

        btnIncome.setOnClickListener(view -> {
            btnIncome.setBackgroundColor(Color.rgb(153, 50, 204));
            btnExpense.setBackgroundColor(Color.rgb(121, 60, 201));
            txtOperationRecord.setText("+");
            txtCategoryChoose.setText("Category");
            txtChosenCategory.setText("Select Category Below");
            selectedRecordType = RecordTypeKey.I;
//                txtAccountChoose.setText("Account");
        });
        btnAddRecord.setOnClickListener(v -> {

            DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
            Date todayDate = new Date();
            String date = formater.format(todayDate);
            String amountString = editTxtAmount.getText().toString();
            if (!amountString.equals("") || amountString.equals(null)) {
                if (selectedCategoryId >= 0) {
                    Database recordsDB = new Database(NewRecord.this);
                    RecordsModel newRecord = new RecordsModel(-1, Integer.parseInt(amountString), date, selectedCategoryId, selectedRecordType, user.getCurrency());
                    boolean successfullInsert = recordsDB.addRecord(newRecord);
                    if (successfullInsert) {
                        startActivity(new Intent(NewRecord.this, Home_activity.class));
                    } else {
                        Toast.makeText(NewRecord.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    error.setText("You must choose a category");
                }
            } else {
                error.setText("Amount field can not be blank");
            }

        });

        editTxtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int txtLength = editTxtAmount.getText().length();
                if (txtLength > 5 && txtLength < 8) {
                    editTxtAmount.setTextSize(60);
                } else if (txtLength >= 8 && txtLength < 11) {
                    editTxtAmount.setTextSize(40);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Menu navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_new_record);
        toolbar.setTitle("Add new record");
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
                    startActivity(new Intent(NewRecord.this, Home_activity.class));
                    break;
                case R.id.nav_new_record:
                    startActivity(new Intent(NewRecord.this, NewRecord.class));
                    break;
                case R.id.nav_categories:
                    startActivity(new Intent(NewRecord.this, CategoriesActivity.class));
                    break;
                case R.id.nav_converter:
                    if (user.getAdmin() == 0) {
                        Toast.makeText(NewRecord.this, "This feature only available for premium members", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(NewRecord.this, Convert_currency_activity.class));
                    }
                    break;
                case R.id.nav_tryPremium:
                    startActivity(new Intent(NewRecord.this, PremiumContent.class));
                    break;
                case R.id.nav_logout:
                    startActivity(new Intent(NewRecord.this, Logout.class));
                    break;
                case R.id.nav_myPlannedPayments:
                    startActivity(new Intent(NewRecord.this, PlannedPayments.class));
                    break;
            }
            return true;
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
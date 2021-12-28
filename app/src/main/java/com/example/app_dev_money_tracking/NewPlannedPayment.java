package com.example.app_dev_money_tracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class NewPlannedPayment extends AppCompatActivity {

    private Button btn_date, btn_save_payment;
    private Spinner sp_plannedPayments_category;
    private EditText etxt_title, etxt_note, etxt_amount;
    private TextView tv_date, tv_status;

    int cyear, cmonth, cday;

    // DB setup
    Database plannedPaymentsDB = new Database(this);
    List<PlannedPaymentsModel> paymentsList;
    List<Categories> categoryList = new ArrayList<>();

    Date date1 = new Date();
    String date2 = "";
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private List<PlannedPaymentsModel> all_plans;

    private int selectedCategoryId = -1;

    private RecordTypeModel.RecordTypeKey selectedRecordType = RecordTypeModel.RecordTypeKey.E;

    public void init() {
        btn_date = (Button) findViewById(R.id.btn_date);
        tv_date = (TextView) findViewById(R.id.textViewDate);
        btn_save_payment = (Button) findViewById(R.id.btn_save_planned_payment);


        etxt_note = (EditText) findViewById(R.id.editTextNote);
        etxt_amount = (EditText) findViewById(R.id.editTextAmount);
        sp_plannedPayments_category = (Spinner) findViewById(R.id.sp_planned_category_list);

        tv_status = (TextView) findViewById(R.id.tv_plannedPaymentList_status);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_planned_payment);

        Database db = new Database(this);
        init();

        paymentsList = plannedPaymentsDB.getPlannedPayments();

        // Spinner for Categories
        categoryList = db.getCategories();
        ArrayAdapter<Categories> spinnerAdapter = new ArrayAdapter<Categories>(this,
                android.R.layout.simple_spinner_item, categoryList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_plannedPayments_category.setAdapter(spinnerAdapter);

//        List<Categories> finalCategoryList = categoryList;

        sp_plannedPayments_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategoryId = i + 1;
                setSpinText(sp_plannedPayments_category, categoryList.get(i).getCategoryImg(), categoryList.get(selectedCategoryId-1).getCategoryName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        User_settings settings = User_settings.instanciate("user1", getApplicationContext());
        UserModel user = db.getUserByEmail(settings.getUserEmail());

        Date tomorrowData = new Date();
        String tomDate = format.format(tomorrowData);
        System.out.println(tomDate);

        all_plans = db.getPlannedPayments();

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                final int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker =
                        new DatePickerDialog(NewPlannedPayment.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(final DatePicker view, final int year, final int month,
                                                  final int dayOfMonth) {

                                @SuppressLint("SimpleDateFormat")
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                                calendar.set(year, month, dayOfMonth);
                                String dateString = sdf.format(calendar.getTime());

                                tv_date.setText(dateString); // set the date
                            }
                        }, year, month, day); // set date picker to current date
                datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePicker.show();

                datePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(final DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });

        btn_save_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateString = tv_date.getText().toString();
                try {
                    date1 = format.parse(dateString);
                    date2 = format.format(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
//                Date date = formatter.parse(String.valueOf(tv_date.getText()));
                String note = etxt_note.getText().toString();
                String amountString = etxt_amount.getText().toString();
                String status = "Waiting";

                if(!amountString.equals("") || amountString.equals(null) && !tv_date.equals("") && !note.equals("")) {
                    if (selectedCategoryId >= 0) {
                        Database plannedPaymentsDB = new Database(NewPlannedPayment.this);
                        PlannedPaymentsModel newPlan = new PlannedPaymentsModel(-1, note, Integer.parseInt(amountString), selectedCategoryId, status, user.getCurrency(), selectedRecordType, date2);
                        boolean successfullInsert = plannedPaymentsDB.addPlannedPayment(newPlan);
                        if (successfullInsert) {
                            startActivity(new Intent(NewPlannedPayment.this, PlannedPayments.class));
                        } else {
                            Toast.makeText(NewPlannedPayment.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(NewPlannedPayment.this, "Fill in all the blanks", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setSpinText(Spinner spin, int drawing, String text) {
        for(int i = 0; i < spin.getAdapter().getCount(); i++) {
            if(spin.getAdapter().getItem(i).toString().contains(text)) {
                if(spin.getAdapter().getItem(i).toString().contains(String.valueOf(drawing)))
                spin.setSelection(i, false);
            }
        }
    }
}
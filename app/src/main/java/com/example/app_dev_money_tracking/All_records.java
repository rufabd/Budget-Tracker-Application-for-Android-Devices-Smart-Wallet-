package com.example.app_dev_money_tracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.app_dev_money_tracking.RecordTypeModel.RecordTypeKey;

public class All_records extends AppCompatActivity
{
    TextView date_from;
    TextView date_to;
    Calendar cal;
    DatePickerDialog.OnDateSetListener listener_from;
    DatePickerDialog.OnDateSetListener listener_to;
    Date_custom DT_date_fr;
    Date_custom DT_date_to;
    DatePickerDialog picker1;
    DatePickerDialog picker2;
    Database db;
    List<RecordsModel> all_records;
    List<RecordsModel> filtered_records;
    List<Categories> categories;
    private RecyclerView Records_recycler;
    Spinner filter_type;
    Spinner filter_cat;
    ArrayAdapter<String> ft_cats;
    ArrayAdapter<String> types;
    RecordTypeKey[] typeString_codes;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_records);
        cal = Calendar.getInstance();
        db = new Database(this);
        all_records = db.getRecords();
        filtered_records = new ArrayList<>();
        filtered_records.addAll(all_records);
        Records_recycler = findViewById(R.id.rec_view_all_records);
        date_from = findViewById(R.id.Txt_date_from);
        date_to = findViewById(R.id.Txt_date_to);
        categories = db.getCategories();
//      Initialise spinners
        spinners();

        if (DT_date_fr == null) {
            DT_date_fr = new Date_custom(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        }
        date_from.setText(DT_date_fr.toString());

        listener_from = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                DT_date_fr.setYear("" + year);
                DT_date_fr.setMonth("" + month);
                DT_date_fr.setDay("" + dayOfMonth);
                date_from.setText(DT_date_fr.toString());
            }
        };

        picker1 = new DatePickerDialog(All_records.this,
                android.R.style.Theme_DeviceDefault_Dialog,
                listener_from, Integer.parseInt(DT_date_fr.getYear()), Integer.parseInt(DT_date_fr.getMonth()),
                Integer.parseInt(DT_date_fr.getDay()));

        if (DT_date_to == null) {
            DT_date_to = new Date_custom(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        }
        date_to.setText(DT_date_to.toString());

        listener_to = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                DT_date_to.setYear("" + year);
                DT_date_to.setMonth("" + month);
                DT_date_to.setDay("" + dayOfMonth);
                date_to.setText(DT_date_to.toString());
            }
        };

        picker2 = new DatePickerDialog(All_records.this,
                android.R.style.Theme_DeviceDefault_Dialog,
                listener_to, Integer.parseInt(DT_date_to.getYear()), Integer.parseInt(DT_date_to.getMonth()),
                Integer.parseInt(DT_date_to.getDay()));

//      Listeners for date picking
        date_from.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                picker1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                picker1.show();
            }
        });

        date_to.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                picker2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                picker2.show();
            }
        });

        set_adapters();
    }

    public void set_adapters()
    {
        Expanse_list_adapter adapter_exp = new Expanse_list_adapter(All_records.this, filtered_records);
        RecyclerView.LayoutManager layout_manager2 = new LinearLayoutManager(getApplicationContext());
        Records_recycler.setLayoutManager(layout_manager2);
        Records_recycler.setItemAnimator(new DefaultItemAnimator());
        Records_recycler.setAdapter(adapter_exp);
    }

    public void spinners()
    {
        //  Map category names to spinner
        String[] cat_names = new String[categories.size() + 1];
        cat_names[0] = "All";
        int i = 1;
        for (Categories c : categories) {
            cat_names[i] = c.getCategoryName();
            i++;
        }

        //Spinners
        ft_cats = new ArrayAdapter<>(All_records.this, android.R.layout.simple_list_item_1, cat_names);
        filter_cat = findViewById(R.id.Sp_filter_category);
        ft_cats.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter_cat.setAdapter(ft_cats);

        String[] typeString = new String[]{"All", "Income", "Expanse"};
        typeString_codes = new RecordTypeKey[]{RecordTypeKey.A, RecordTypeKey.I, RecordTypeKey.E};
        types = new ArrayAdapter<>(All_records.this, android.R.layout.simple_list_item_1, typeString);
        filter_type = findViewById(R.id.SP_filter_type);
        types.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter_type.setAdapter(types);
    }


    public void On_filter_click(View view)
    {
        Date fr_date = null;
        Date to_date = null;
        Date cmp_date = null;

        filtered_records.clear();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            fr_date = dt.parse(DT_date_fr.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            to_date = dt.parse(DT_date_to.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (RecordsModel rec : all_records) {
            boolean b_date = false;
            boolean b_type = false;
            boolean b_cat = false;

            try {
                cmp_date = dt.parse(rec.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int lower = cmp_date.compareTo(fr_date);
            int upper = cmp_date.compareTo(to_date);

            if (lower >= 0 && upper <= 0) {
                b_date = true;
            }

            RecordTypeModel.RecordTypeKey tp_key = rec.getRecordType();
            RecordTypeKey tp_spin = typeString_codes[filter_type.getSelectedItemPosition()];

            if (tp_spin.equals(RecordTypeKey.A) || tp_key.equals(tp_spin)) {
                b_type = true;
            }

            int pos = filter_cat.getSelectedItemPosition() - 1;
            if (pos >= 0) {
                int cat_id = categories.get(pos).getId();
                if (filter_cat.getSelectedItemPosition() == 0 || rec.getCategoryId() == cat_id) {
                    b_cat = true;
                }
            }
            else
            {
                b_cat = true;
            }

            if (b_date && b_type && b_cat) {
                filtered_records.add(rec);
            }
        }
        set_adapters();
    }

}
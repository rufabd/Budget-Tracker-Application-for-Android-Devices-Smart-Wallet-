package com.example.app_dev_money_tracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.github.mikephil.charting.charts.PieChart;

import java.util.List;

public class Logout extends AppCompatActivity {

    private User_settings user_settings;
    UserModel user;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user_settings = User_settings.instanciate("user1", this);
        db = new Database(this);
        user = db.getUserByEmail(user_settings.getUserEmail());
        if(!user.getFbid().equals(""))
        {
            LoginManager.getInstance().logOut();
        }
            startActivity(new Intent(Logout.this, LoginActivity.class));
    }
}
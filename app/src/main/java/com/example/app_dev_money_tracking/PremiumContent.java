package com.example.app_dev_money_tracking;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.share.Share;
import com.facebook.share.ShareApi;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

public class PremiumContent extends AppCompatActivity {
    private User_settings user_settings;
    private DrawerLayout drawer;
    Button shareFacebookButton;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium_content);

        user_settings = User_settings.instanciate("user1", this);
        Database db = new Database(this);
        UserModel user = db.getUserByEmail(user_settings.getUserEmail());

        TextView Status = findViewById(R.id.premium_user_status);
        Button tryPremiumButton = findViewById(R.id.try_premium_button);

        shareFacebookButton = findViewById(R.id.share_facebook_Button);
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);


        shareFacebookButton.setOnClickListener(v -> {
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("https://quizmart.io/"))
                        .setShareHashtag(new ShareHashtag.Builder()
                                .setHashtag("#moneyTrackingApp")
                                .build())
                        .setQuote("I reached my saving goal for this month. Download money tracking app to make saving money easy")
                        .build();
                shareDialog.show(linkContent);
            }
        });


        if (user.getAdmin() == 0) {
            Status.setText("");
            tryPremiumButton.setText("Try now!!!");
        } else {
            Status.setText("Premium mode activated");
            tryPremiumButton.setText("Remove premium");
        }

        tryPremiumButton.setOnClickListener(v -> {

            if (user.getAdmin() == 0) {
                user.setAdmin(1);
                boolean updated = db.updateUser(user);
                if (updated) {
                    Toast.makeText(PremiumContent.this, "Premium activated", Toast.LENGTH_SHORT).show();
                    Status.setText("Premium mode activated");
                    tryPremiumButton.setText("Remove premium");
                } else {
                    Toast.makeText(PremiumContent.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            } else {
                user.setAdmin(0);
                boolean updated = db.updateUser(user);
                if (updated) {
                    Toast.makeText(PremiumContent.this, "Premium removed", Toast.LENGTH_SHORT).show();
                    Status.setText("");
                    tryPremiumButton.setText("Try now!!!");
                } else {
                    Toast.makeText(PremiumContent.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Menu navigation

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_tryPremium);
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
                    startActivity(new Intent(PremiumContent.this, Home_activity.class));
                    break;
                case R.id.nav_new_record:
                    startActivity(new Intent(PremiumContent.this, NewRecord.class));
                    break;
                case R.id.nav_categories:
                    startActivity(new Intent(PremiumContent.this, CategoriesActivity.class));
                    break;
                case R.id.nav_converter:
                    if (user.getAdmin() == 0) {
                        Toast.makeText(PremiumContent.this, "This feature only available for premium members", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(PremiumContent.this, Convert_currency_activity.class));
                    }
                    break;
                case R.id.nav_receipts:
                    startActivity(new Intent(PremiumContent.this, ReceiptGallery.class));
                    break;
                case R.id.nav_tryPremium:
                    startActivity(new Intent(PremiumContent.this, PremiumContent.class));
                    break;
                case R.id.nav_logout:
                    startActivity(new Intent(PremiumContent.this, Logout.class));
                    break;
                case R.id.nav_myPlannedPayments:
                    startActivity(new Intent(PremiumContent.this, PlannedPayments.class));
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

}
package com.example.app_dev_money_tracking;

import static android.view.View.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    TextView emailError, passwordError;
    private User_settings settings;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(Profile.getCurrentProfile() != null && AccessToken.getCurrentAccessToken() != null){
            startActivity(new Intent(LoginActivity.this, Home_activity.class));
        }

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(onLoginButtonClick());
        LoginButton facebookButton = findViewById(R.id.loginFacebookIcon);
        Button signupButton = findViewById(R.id.loginSignUp);
        signupButton.setOnClickListener(onSignupButtonClick());

        emailInput = findViewById(R.id.loginEmailEditText);
        emailError = findViewById(R.id.loginEmailError);
        passwordInput = findViewById(R.id.loginPasswordEditText);
        passwordError = findViewById(R.id.loginPasswordError);

        ClearErrorMessage(emailInput, emailError);
        ClearErrorMessage(passwordInput, passwordError);

        settings = User_settings.instanciate("user1", getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        facebookButton.setPermissions(Arrays.asList("email"));

        facebookButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Demo", "Login succesfull");
            }

            @Override
            public void onCancel() {
                Log.d("Demo", "Login cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("Demo", "Login error" + exception);
            }
        });


    }

    private OnClickListener onSignupButtonClick() {
        return v -> startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }


    private OnClickListener onLoginButtonClick() {
        return v -> {
            startActivity(new Intent(LoginActivity.this, splash.class));
//            onLoginButtonFunction(v);
        };
    }

    private void onLoginButtonFunction(View v) {
        HelperFunctions.hideSoftKeyboard(LoginActivity.this, v);
        LoginValidator loginValidator = new LoginValidator().invoke();
        String email = loginValidator.getEmail();
        String password = loginValidator.getPassword();

        if (loginValidator.isValid()) {
            Database db = new Database(LoginActivity.this);
            UserModel userModel = db.getUserByEmail(email);
            if (userModel != null) {
                if (userModel.getPassword().equals(password)) {
                    settings.setUserEmail(email);
                    startActivity(new Intent(LoginActivity.this, splash.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "User with this email does not exists", Toast.LENGTH_SHORT).show();

            }

        }
    }


    private void setErrorMessage(TextView errorField, String errorMessage) {
        errorField.setText(errorMessage);
    }

    private void ClearErrorMessage(EditText input, final TextView errorField) {
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) setErrorMessage(errorField, "");

            }
        });
    }

    private class LoginValidator {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public LoginValidator invoke() {
            email = emailInput.getText().toString();
            password = passwordInput.getText().toString();
            if (email.isEmpty()) setErrorMessage(emailError, "Email is required");
            if (password.isEmpty()) setErrorMessage(passwordError, "Password is required");
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                setErrorMessage(emailError, "Must be an email");

            return this;
        }

        private boolean isValid() {
            return !password.isEmpty() && !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(@Nullable JSONObject jsonObject, @Nullable GraphResponse graphResponse) {
                String response = jsonObject.toString();
                Log.d("Demo", jsonObject.toString());
                String email = null;
                try {
                    email = jsonObject.getString("email");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String fbId = null;
                try {
                    fbId = jsonObject.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Database db = new Database(LoginActivity.this);
                UserModel userModel = db.getUserByEmail(email);
                if (userModel != null) {
                    settings.setUserEmail(email);
                } else {
                    UserModel user = new UserModel(-1, email, fbId, 0, 0, fbId,"EUR");
                    db.addUser(user);
                    settings.setUserEmail(email);
                }
                startActivity(new Intent(LoginActivity.this, splash.class));
                finish();

            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields", " name, id,email, first_name,last_name");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                LoginManager.getInstance().logOut();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }
}
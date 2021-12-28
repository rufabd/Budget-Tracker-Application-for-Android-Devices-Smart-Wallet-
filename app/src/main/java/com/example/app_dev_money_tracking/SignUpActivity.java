package com.example.app_dev_money_tracking;

import static com.example.app_dev_money_tracking.HelperFunctions.setErrorMessage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Currency;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {
    EditText emailInput, passwordInput, password2Input;
    TextView emailError, passwordError, password2Error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Button signupButton = findViewById(R.id.signupButton);
        signupButton.setOnClickListener(onSignupButtonClick());
        ImageView googleButton = findViewById(R.id.signupGoogleIcon);
        googleButton.setOnClickListener(onSocialButtonClick());
        ImageView facebookButton = findViewById(R.id.signupFacebookIcon);
        facebookButton.setOnClickListener(onSocialButtonClick());
        Button loginButton = findViewById(R.id.signupLogin);
        loginButton.setOnClickListener(onLoginButtonClick());

        emailInput = findViewById(R.id.signupEmailEditText);
        emailError = findViewById(R.id.signupEmailError);
        passwordInput = findViewById(R.id.signupPasswordEditText);
        passwordError = findViewById(R.id.signupPasswordError);
        password2Input = findViewById(R.id.signupPassword2EditText);
        password2Error = findViewById(R.id.signupPassword2Error);

        ClearErrorMessage(emailInput, emailError);
        ClearErrorMessage(passwordInput, passwordError);
        ClearErrorMessage(password2Input, password2Error);

    }

    private View.OnClickListener onLoginButtonClick() {
        return v -> startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
    }

    private View.OnClickListener onSocialButtonClick() {
        return v -> Toast.makeText(SignUpActivity.this, "Feature coming soon", Toast.LENGTH_SHORT).show();
    }

    private View.OnClickListener onSignupButtonClick() {
        return v -> {
            HelperFunctions.hideSoftKeyboard(SignUpActivity.this, v);
            SignUpActivity.SignupValidator signupValidator = new SignUpActivity.SignupValidator().invoke();
            String email = signupValidator.getEmail();
            String password = signupValidator.getPassword();
            String password2 = signupValidator.getPassword2();
            if (signupValidator.isValid()) {
                if (!password.equals(password2)) {
                    setErrorMessage(password2Error, "Passwords must match");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    setErrorMessage(emailError, "Must be an email");
                else {
                    Database db = new Database(SignUpActivity.this);
                    if (db.getUserByEmail(email) == null) {
                        Locale locale = Locale.getDefault();
                        Currency currency = Currency.getInstance(locale);
                        UserModel userModel = new UserModel(-1, email, password,0,0,"",currency.toString());
                        boolean successFullInsert = db.addUser(userModel);
                        if (successFullInsert) {
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        } else {
                            Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "User with this email already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        };
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

    private class SignupValidator {

        private String email;
        private String password;
        private String password2;

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getPassword2() {
            return password2;
        }

        public SignUpActivity.SignupValidator invoke() {
            email = emailInput.getText().toString();
            password = passwordInput.getText().toString();
            password2 = password2Input.getText().toString();
            if (email.isEmpty()) setErrorMessage(emailError, "Email is required");
            if (password.isEmpty()) setErrorMessage(passwordError, "Password is required");
            if (password2.isEmpty()) setErrorMessage(password2Error, "Password repeat is required");
            return this;
        }

        private boolean isValid() {
            return !password.isEmpty() && !email.isEmpty() && !password2.isEmpty();
        }


    }



}
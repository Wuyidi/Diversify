package com.techhawk.diversify.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.techhawk.diversify.R;

public class AuthMethodPickerActivity extends BaseActivity implements View.OnClickListener {

    // Instance variable
    private Button emailButton;
    private Button registerButton;
    private Button skipButton;

    private EditText inputEmail;
    private EditText inputPassword;

    // [START declare_auth]
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_method_picker);
        emailButton = findViewById(R.id.email_button);
        registerButton = findViewById(R.id.btn_register);
        skipButton = findViewById(R.id.btn_skip);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);


        auth = FirebaseAuth.getInstance();
        emailButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.email_button:
                signInWithEmail(inputEmail.getText().toString().trim(), inputPassword.getText().toString().trim());
                break;
            case R.id.btn_skip:
                startActivity(new Intent(AuthMethodPickerActivity.this, MainActivity.class));
                break;
            case R.id.btn_register:
                startActivity(new Intent(AuthMethodPickerActivity.this, SignUpActivity.class));
                break;
        }


    }


    private void signInWithEmail(String email, String password) {
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(AuthMethodPickerActivity.this, MainActivity.class));
                            finish();
                        } else {

                            feedback(getString(R.string.auth_failed));
                        }

                        hideProgressDialog();
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        // Validate user input
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Required");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Required");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        if (password.length() < 6) {
            inputPassword.setError("More than 6 letters.");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        return valid;

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}

package com.techhawk.diversify.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.techhawk.diversify.R;
import com.techhawk.diversify.model.User;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {


    // Instance variable
    private EditText inputEmail;
    private EditText inputName;
    private EditText inputPassword;
    private Button registerBtn;
    private Button signInBtn;
    private FirebaseAuth auth;
    private DatabaseReference ref;
//    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        ref = FirebaseDatabase.getInstance().getReference();

        // Get reference from activity
        registerBtn = findViewById(R.id.btn_register);
        inputEmail = findViewById(R.id.email);
        inputName = findViewById(R.id.name);
        inputPassword = findViewById(R.id.password);
//        progressBar = findViewById(R.id.signUp_progressBar);
        signInBtn = findViewById(R.id.btn_sign_in);

        registerBtn.setOnClickListener(this);
        signInBtn.setOnClickListener(this);
    }

    // Convert the email to user name
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    // Write a new user to firebase
    private void createNewUser(String userId, String name) {
        User user = new User(name);
        user.setImgUrl("");
        ref.child("users").child(userId).setValue(user);
    }

    // Create a new user
    private void onAuthSuccess(FirebaseUser user) {
        String name = inputName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            name = usernameFromEmail(user.getEmail());
        }

        createNewUser(user.getUid(), name);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_register:
                signUp(inputEmail.getText().toString().trim(), inputPassword.getText().toString().trim());
                break;
            case R.id.btn_sign_in:
                finish();
                break;
            default:
                break;
        }

    }



    private void signUp(String email, String password) {
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        feedback("Join in successfully!");
                        hideProgressDialog();
                        if (!task.isSuccessful()) {
                            feedback("Authentication failed. " + task.getException());
                        } else {
                            onAuthSuccess(task.getResult().getUser());
                            startActivity(new Intent(SignUpActivity.this, AuthMethodPickerActivity.class));
                            finish();
                        }

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

}

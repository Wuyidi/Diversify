package com.techhawk.diversify.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.techhawk.diversify.R;

public class AuthMethodPickerActivity extends AppCompatActivity implements View.OnClickListener {

    // Instance variable
    private Button emailButton;
    private Button registerButton;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_method_picker);
        emailButton = findViewById(R.id.email_button);
        registerButton = findViewById(R.id.btn_register);
        emailButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
       if (view.getId() == emailButton.getId()) {
           startActivity(new Intent(AuthMethodPickerActivity.this,MainActivity.class));
       } else if (view.getId() == registerButton.getId()) {
           startActivity(new Intent(AuthMethodPickerActivity.this, SignUpActivity.class));
       }
    }
}

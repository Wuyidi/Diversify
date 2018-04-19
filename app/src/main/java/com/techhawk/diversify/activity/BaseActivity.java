package com.techhawk.diversify.activity;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.techhawk.diversify.R;

/**
 * Created by Yidi Wu on 22/3/18.
 */

public class BaseActivity extends AppCompatActivity {
    // Get current user ID from Firebase
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    // Show a feedback
    public void feedback(String msg) {
        Toast toast = Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT);
        View v = toast.getView();
        v.setBackgroundResource(R.drawable.custom_toast);
        toast.setView(v);
        toast.show();
    }
}

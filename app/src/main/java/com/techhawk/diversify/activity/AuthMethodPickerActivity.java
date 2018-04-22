package com.techhawk.diversify.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.techhawk.diversify.R;
import com.techhawk.diversify.model.User;

public class AuthMethodPickerActivity extends BaseActivity implements View.OnClickListener {

    // Instance variable
    private Button emailButton;
    private Button registerButton;
    private Button skipButton;
    private Button googleButton;
    private LoginButton facebookLoginButton;
    private Button facebookButton;

    private EditText inputEmail;
    private EditText inputPassword;

    // [START declare_auth_google]
    private FirebaseAuth auth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    // [START declare_auth_google]
    private static final String EMAIL = "email";
    private CallbackManager mCallbackManager;

    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_method_picker);
        emailButton = findViewById(R.id.email_button);
        registerButton = findViewById(R.id.btn_register);
        skipButton = findViewById(R.id.btn_skip);
        googleButton = findViewById(R.id.google_button);
        facebookLoginButton = findViewById(R.id.login_button);
        facebookButton = findViewById(R.id.facebook_button);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);

        userRef = FirebaseDatabase.getInstance().getReference().child("users");

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        auth = FirebaseAuth.getInstance();
        emailButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        skipButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        facebookLoginButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
        // [START initialize_fblogin]
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        facebookLoginButton.setReadPermissions(EMAIL, "public_profile");
        facebookLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


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
            case R.id.google_button:
                signInWithGoogleAccount();
                break;
            case R.id.facebook_button:
                facebookLoginButton.performClick();
                break;

        }


    }


    private void signInWithEmail(String email, String password) {
        if (!validateForm()) {
            return;
        }


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

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressDialog();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNew) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                createNewUser(user.getUid(), user.getDisplayName());
                            }
                            startActivity(new Intent(AuthMethodPickerActivity.this, MainActivity.class));
                            finish();
                        } else {
                            feedback(getString(R.string.auth_failed));
                        }
                        hideProgressDialog();
                    }
                });

    }


    private void signInWithGoogleAccount() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Write a new user to firebase
    private void createNewUser(String userId, String name) {
        User user = new User(name);
        userRef.child(userId).setValue(user);
    }

    // Convert the email to user name
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNew) {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                createNewUser(user.getUid(), user.getDisplayName());
                            }
                            startActivity(new Intent(AuthMethodPickerActivity.this, MainActivity.class));
                            finish();

                        } else {
                            feedback(getString(R.string.auth_failed));
                        }
                        hideProgressDialog();
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                System.out.println(e.getMessage());
            }
        }

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
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

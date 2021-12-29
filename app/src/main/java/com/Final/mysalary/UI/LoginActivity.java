package com.Final.mysalary.UI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.Controller.Actions.LoginActions;
import com.Final.mysalary.Model.DTO.User;
import com.Final.mysalary.R;
import com.Final.mysalary.Model.Callback;
import com.Final.mysalary.Model.DB;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 120;
    public FirebaseAuth mAuth;
    User curUser;
    public static GoogleSignInClient mGoogleSignInClient;
    LoginActions actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        actions = new LoginActions(this);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void onStart() {
        super.onStart();
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String mail;
        if (googleAccount != null) mail = googleAccount.getEmail();
        else if (currentUser != null) mail = currentUser.getEmail();
        else return;
        DB.getUserByUserMail(mail, new Callback<User>() {
            @Override
            public void play(User user) {
                if (user == null) return;
                curUser = user;
                actions.moveToMainScreen(curUser);
            }
        });


    }

    public void google_sign(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                actions.handleSignInResult(task);
            } else {
                System.out.println(task.getException());
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void login(View view) {
        actions.login();
    }

    public void register(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void forgot(View view) {
        actions.forgot();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void testDB(View view) {

        String title = ((EditText) findViewById(R.id.input_username)).getText().toString();
        String message = ((EditText) findViewById(R.id.input_pass)).getText().toString();
        actions.sendNotificationToUserMail("shlomog12@googlemail.com",title,message);
        actions.sendNotificationToAllUsers(title,message);
    //        DBTest.test();
    }







}
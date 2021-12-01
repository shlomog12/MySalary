package com.Final.mysalary.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.Final.mysalary.DTO.Worker;
import com.Final.mysalary.R;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;


import com.Final.mysalary.DTO.Shift;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.R;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    public FirebaseAuth mAuth;
    User curUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

//        String serverClientId = getString(R.string.server_client_id);
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
//                .requestServerAuthCode(serverClientId)
//                .requestEmail()
//                .build();

    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;
        DB.getUserByUserName(currentUser.getDisplayName(), new Callback() {
            @Override
            public void play(User user) {
                curUser = user;
                System.out.println(user);
                moveToTheMainScreen();
            }

            @Override
            public void play(boolean bool) {

            }

            @Override
            public void play(ArrayList<Shift> shifts) {

            }

            @Override
            public void play(double num) {

            }
        });

    }



    public void login(View view) {
        EditText userEditText = findViewById(R.id.input_username);
        EditText passEditText = findViewById(R.id.input_pass);
        int checkId = rg.getCheckedRadioButtonId();
        mAuth.signInWithEmailAndPassword(userEditText.getText().toString(), passEditText.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            switch (checkId) {
                                case -1:
                                    Toast.makeText(LoginActivity.this, "you must enter type", Toast.LENGTH_SHORT).show();
                                case R.id.rdoBtnBoss:
                                    startActivity(new Intent(LoginActivity.this, BossActivity.class));
                                case R.id.rdoBtnWork:
                                    startActivity(new Intent(LoginActivity.this, WorkerActivity.class));
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void register(View view) {
//        EditText userEditText = findViewById(R.id.input_username);
//        EditText passEditText = findViewById(R.id.input_pass);
//        mAuth.createUserWithEmailAndPassword(userEditText.getText().toString(), passEditText.getText().toString())
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(LoginActivity.this, "registered successfully", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(LoginActivity.this, "register failed", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void forgotThePasswordׂ(View view){ }
    public void moveToTheRegisterScreen(View view) {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);

    }

    public void forgotThePasswordׂ(View view) {
    }

    private void moveToTheRegisterScreen(View view) {
    }



    private void moveToTheMainScreen() {
        Intent intent;
        if (curUser.getType() == "worker") intent = new Intent(this,WorkerActivity.class);
        else intent = new Intent(this,BossActivity.class);
        startActivity(intent);
    }

    public void moveToBossScreen(View view) {
        Intent intent = new Intent(this, BossActivity.class);
        startActivity(intent);
    }

    public void moveToWorkerScreen(View view) {
        Intent intent = new Intent(this,WorkerActivity.class);
        startActivity(intent);
    }

    private boolean VerifyUsernameAndPassword() {
        return true;
    }
    private void PopUpMessageIncorrectPassword() {}




    public  void loginWithFire(){
        String mail = "";
        String password ="";
        mAuth.signInWithEmailAndPassword(mail,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
//

                        }else {
                            System.out.println(task.getException());

                        }
                    }
                });

    }



}
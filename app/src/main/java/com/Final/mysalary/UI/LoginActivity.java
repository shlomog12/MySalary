package com.Final.mysalary.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RadioGroup rg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        rg = findViewById(R.id.radiogroup);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.isEmailVerified())
                startActivity(new Intent(this, WorkerActivity.class));
            else startActivity(new Intent(this, BossActivity.class));
        }
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


    public void loginGoogle(View view) {
    }

    public void forgotThePasswordׂ(View view) {
    }

    private void moveToTheRegisterScreen(View view) {
    }

    private boolean VerifyUsernameAndPassword() {
        return true;
    }

    private void PopUpMessageIncorrectPassword() {
        Toast.makeText(LoginActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
    }

    public void google_sign(View view) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
//                        } else {
//                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
//                        }
//
//                        // ...
//                    }
//                });
    }
}
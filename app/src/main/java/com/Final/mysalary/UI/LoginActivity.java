package com.Final.mysalary.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
        if (VerifyUsernameAndPassword()) moveToTheMainScreen();
        else PopUpMessageIncorrectPassword();
    }
    public void loginGoogle(View view){
        moveToTheMainScreen();
    }
    public void forgotThePasswordׂ(View view){ }
    public void moveToTheRegisterScreen(View view) {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
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
package com.Final.mysalary.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.DTO.Type;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.R;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    public FirebaseAuth mAuth;
    User curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;
        DB.getUserByUserName(currentUser.getDisplayName(), new Callback<User>() {
            @Override
            public void play(User user) {
                curUser = user;
                System.out.println(user);
                moveToTheMainScreen();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void login(View view){
        String mail = ((EditText)findViewById(R.id.input_username)).getText().toString();
        String password = ((EditText)findViewById(R.id.input_pass)).getText().toString();
        if (mail.length() < 5){
            popUpMessage("מייל לא תקין");
            return;
        }
        if(password.length() < 6){
            popUpMessage("סיסמה לא תקינה");
            return;
        }
        mAuth.signInWithEmailAndPassword(mail,password)

                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Intent i = new Intent(LoginActivity.this,WorkerActivity.class);
                        i.putExtra("user",mail);
                        startActivity(i);
//                        if (task.isSuccessful()){
//                            System.out.println("1mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm");
//                            System.out.println(mAuth.getCurrentUser().getDisplayName());
//                            DB.getUserByUserName(mAuth.getCurrentUser().getDisplayName(), new Callback<User>() {
//                                @Override
//                                public void play(User user) {
//                                    System.out.println(user);
//                                    curUser = user;
//                                    System.out.println(curUser);
//                                    moveToTheMainScreen();
//                                }
//                            });
//                        }else {
//                            popUpMessage("ההתחברות נכשלה");
//                            System.out.println(task.getException());
//                        }
                    }
                });
    }

    public void register(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void moveToTheMainScreen() {
        System.out.println("************************************************************************************************");
        System.out.println(curUser.getType());
        System.out.println("************************************************************************************************");
        if (curUser.getType() == Type.WORKER) startActivity(new Intent(this, WorkerActivity.class));
        else startActivity(new Intent(this, BossActivity.class));
    }

    private void popUpMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void google_sign(View view) {
        //        String serverClientId = getString(R.string.server_client_id);
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
//                .requestServerAuthCode(serverClientId)
//                .requestEmail()
//                .build();
    }

    public void forgot(View view) {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void testDB(View view) throws InterruptedException {
//        System.out.println("108");
        DBTest.test("avha");
    }
}


//30652896089-12272i8lgqvqlbb6ber8rs16v2kig76j.apps.googleusercontent.com
//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
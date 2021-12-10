package com.Final.mysalary.UI;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.DTO.Type;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.R;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 120;
    public FirebaseAuth mAuth;
    User curUser;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String mail;
        if (account != null) mail = account.getEmail();
        else if (currentUser != null) mail = currentUser.getEmail();
        else return;
        DB.getUserByUserMail(mail, new Callback<User>() {
            @Override
            public void play(User user) {
                curUser = user;
                moveToMainScreen();
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
            if (task.isSuccessful()) handleSignInResult(task);
            else System.out.println(task.getException());
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            DB.CheckIfTheUserMailIsExists(account.getEmail(), new Callback<Boolean>() {
                @Override
                public void play(Boolean isExits) {
                    if (isExits){
                        DB.getUserByUserMail(account.getEmail(), new Callback<User>() {
                            @Override
                            public void play(User user) {
                                curUser = user;
                                moveToMainScreen();
                            }
                        });
                    }
                    else {

                        User user = new User(account.getEmail(),account.getGivenName(),account.getFamilyName(),"",account.getDisplayName(),Type.WORKER);

                    }
                }
            });

            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$113");
            System.out.println(account.getEmail());
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$115");

//            if ()
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$120");
//            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//            System.out.println(firebaseUser.getEmail());



//            updateUI(account);
        } catch (ApiException e) {
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$126");
            System.out.println("ERROR 120");
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$128");
//            updateUI(null);
        }
    }



    private void moveToMainScreen() {
        if (curUser == null) return;
        Intent intent;
        if (curUser.getType() == Type.WORKER.ordinal()) intent = new Intent(this,WorkerActivity.class);
        else intent = new Intent(this,BossActivity.class);
        intent.putExtra("userMail",  curUser.getMail());
        startActivity(intent);
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
        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    DB.getUserByUserMail(mAuth.getCurrentUser().getEmail(), new Callback<User>() {
                        @Override
                        public void play(User user) {
                            curUser = user;
                            moveToMainScreen();
                        }
                    });
                }else {
                    popUpMessage("ההתחברות נכשלה");
                    System.out.println(task.getException());
                }
            }
        });
    }
    public void register(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }
    private void popUpMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }



    public void forgot(View view) {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void testDB(View view) {

        DBTest.test();
//        showRadioButtonDialog();
    }

//    private void showRadioButtonDialog() {
//        // custom dialog
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_layout);
//        List<String> stringList=new ArrayList<>();  // here is list
//        for(int i=0;i<2;i++) {
//            if (i==0){
//                stringList.add("Number Mode");
//            }else {
//                stringList.add("Character Mode");
//            }
//
//        }
//        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);
//        for(int i=0;i<stringList.size();i++){
//            RadioButton rb=new RadioButton(this); // dynamically creating RadioButton and adding to RadioGroup.
//            rb.setText(stringList.get(i));
//            rg.addView(rb);
//        }
//    }
}


//30652896089-12272i8lgqvqlbb6ber8rs16v2kig76j.apps.googleusercontent.com
//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
package com.Final.mysalary.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.Controller.UiActions;
import com.Final.mysalary.Controller.Validate;
import com.Final.mysalary.db.DTO.Type;
import com.Final.mysalary.db.DTO.User;
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

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 120;
    public FirebaseAuth mAuth;
    User curUser;
    public static GoogleSignInClient mGoogleSignInClient;
    UiActions actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        actions = new UiActions(this);
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
                handleSignInResult(task);
            } else {
                System.out.println(task.getException());
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            DB.CheckIfTheUserMailIsExists(account.getEmail(), new Callback<Boolean>() {
                @Override
                public void play(Boolean isExits) {
                    if (isExits) {
                        DB.getUserByUserMail(account.getEmail(), new Callback<User>() {
                            @Override
                            public void play(User user) {
                                curUser = user;
                                actions.moveToMainScreen(curUser);
                            }
                        });
                    } else {
                        curUser = new User(account.getEmail(), account.getGivenName(), account.getFamilyName(), "", account.getDisplayName(), Type.WORKER);
                        showSelectTypeDialog();
                    }
                }
            });
        } catch (ApiException e) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void login(View view) {
        String mail = ((EditText) findViewById(R.id.input_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.input_pass)).getText().toString();
        if (mail.length() < 5) {
            actions.popUpMessage(getApplicationContext().getString(R.string.mail_incorrect));
            return;
        }
        if (password.length() < 6) {
            actions.popUpMessage(getApplicationContext().getString(R.string.wrong_password));
            return;
        }
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DB.getUserByUserMail(mAuth.getCurrentUser().getEmail(), new Callback<User>() {
                        @Override
                        public void play(User user) {
                            curUser = user;
                            actions.moveToMainScreen(curUser);
                        }
                    });
                } else {
                    actions.popUpMessage(getApplicationContext().getString(R.string.login_failed));
                    System.out.println(task.getException());
                }
            }
        });
    }

    public void register(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    public void forgot(View view) {
        final EditText email = new EditText(this);
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        email.setHint(getResources().getString(R.string.mail));
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.enterMail))
                .setView(email)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String mail = email.getText().toString();
                        if (!Validate.isValidEmail(mail)) {
                            actions.popUpMessage(getApplicationContext().getString(R.string.mail_incorrect));
                            return;
                        }
                        FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            actions.popUpMessage(getApplicationContext().getString(R.string.thanks_forgot));
                                            dialog.dismiss();
                                        } else
                                            actions.popUpMessage(getApplicationContext().getString(R.string.mail_not_exist));
                                    }
                                });
                    }
                });
            }
        });
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void testDB(View view) {

        String title = ((EditText) findViewById(R.id.input_username)).getText().toString();
        String message = ((EditText) findViewById(R.id.input_pass)).getText().toString();
        actions.sendNotificationToUserMail("shlomog12@googlemail.com",title,message);
        actions.sendNotificationToAllUsers(title,message);
    //        DBTest.test();
    }

    private void showSelectTypeDialog() {
        String[] types = {getResources().getString(R.string.worker), getResources().getString(R.string.boss)};
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(getResources().getString(R.string.choose_user_type));
        builder.setSingleChoiceItems(types, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String type = types[which];
                if (type == "מנהל") curUser.setType(Type.BOSS.ordinal());
            }
        });
        builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                actions.popUpMessage(getResources().getString(R.string.thank_you));
                dialog.dismiss();
                DB.setUser(curUser);
                actions.moveToMainScreen(curUser);
            }
        });
        builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }





}
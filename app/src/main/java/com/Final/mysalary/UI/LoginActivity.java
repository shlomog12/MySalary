package com.Final.mysalary.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.IDNA;
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

import com.Final.mysalary.DTO.Type;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.R;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;
import com.Final.mysalary.notfication.FcmNotificationsSender;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.internal.GoogleServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.installations.remote.FirebaseInstallationServiceClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;


public class LoginActivity extends AppCompatActivity {



    String token11 = "30652896089-mndnm281amtdb8f5ei48ikgadrlj4fmg.apps.googleusercontent.com";
    String token22 = "30652896089-q7g8b3sqqvp12l95b7lcect2p6h3ek7t.apps.googleusercontent.com";
    String default_web_client_id = "30652896089-hc3frmehqg3gi41hm01hneh8vbms442a.apps.googleusercontent.com";



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
                .requestIdToken(default_web_client_id)
                .requestEmail()
                .build();
        System.out.println(gso);
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    public void onStart() {
        super.onStart();
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String mail;
         if (currentUser != null) {
            mail = currentUser.getEmail();
        }
         else if (googleAccount != null){
             mail = googleAccount.getEmail();
        } else return;
        DB.getUserByUserMail(mail, new Callback<User>() {
            @Override
            public void play(User user) {
                curUser = user;
                updateToken();
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
                                updateToken();
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
        email.setHint("Email...");
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Enter your email")
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
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        String title = ((EditText) findViewById(R.id.input_username)).getText().toString();
        String message = ((EditText) findViewById(R.id.input_pass)).getText().toString();
        String tok = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImQ5OGY0OWJjNmNhNDU4MWVhZThkZmFkZDQ5NGZjZTEwZWEyM2FhYjAiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIzMDY1Mjg5NjA4OS02c2U5ZnB2dWlhZ2Fsc2VuMmE2ZzNudmc2Z2Nqamo1bC5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImF1ZCI6IjMwNjUyODk2MDg5LWhjM2ZybWVocWczZ2k0MWhtMDFobmVoOHZibXM0NDJhLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTA4NDI3MTI4OTQxNTE4MjE2MTI4IiwiZW1haWwiOiJzaGxvbW9nMTJAZ29vZ2xlbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6Itep15zXnteUINeS15zXmdenIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FBVFhBSndyTTFzc1NHSkZkQnhZcWc0NHprdDRSNzlLMUI0OC0zVUhRRExpPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6Itep15zXnteUIiwiZmFtaWx5X25hbWUiOiLXktec15nXpyIsImxvY2FsZSI6ImhlIiwiaWF0IjoxNjQwMjE0MTY2LCJleHAiOjE2NDAyMTc3NjZ9.grKBq_m0OBfyFf52fGNvqoXgtXu4jImNNBi4xxUGvM-z2qXy5kPvh4uQli0wnkaqDjdEdvOkRIEeXnnJN-PAJ1gtRQ7aowUX0TTmXqdWWKNCC6Mib0nFIZu-vFmDnwYk3fLnL_SRE365AETGhgK8ssiDIyvHu-esIBxzFPy4V2gG_qg3Z9ZU39hOaXxSnGrgM8NzSLYOoitr5FSzm4sT2fylfN8tUUDq7raMkQyHSAfQ-b8bRwQJp6Ta-ncgXWQvodwjOIGOW1iTtVIF7IGW74z6cF_L2hdRCrrZN5NBdhvYubYEXnRooTDTFJlPAJEv45pE_voeAdAYLncdhtWqcA";
        String tokTo = "cHdwJr1jT52mEEiSNZ1FYt:APA91bGjwt93tRMcGj3QVNk41dthiziBOjkDhkRMqOniItW4VrNyFw--vPbuN6h5qH6AYr55qicf4QArQxGrQwdrrxt0eHWaQ-jgkY_FQaB5m-NwXm3RadVvy2scgrhNI2rszLB_94uQ";
        //        actions.sendNotificationToTokenId(tok,title,message);
        actions.sendNotificationToTokenId(tokTo,title,message);
//
//        String token2 = "/topics/all";
//        String token3 = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImQ5OGY0OWJjNmNhNDU4MWVhZThkZmFkZDQ5NGZjZTEwZWEyM2FhYjAiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIzMDY1Mjg5NjA4OS02c2U5ZnB2dWlhZ2Fsc2VuMmE2ZzNudmc2Z2Nqamo1bC5hcHBzLmdvb2dsZXVzZXJjb250ZW50LmNvbSIsImF1ZCI6IjMwNjUyODk2MDg5LWhjM2ZybWVocWczZ2k0MWhtMDFobmVoOHZibXM0NDJhLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTA4NDI3MTI4OTQxNTE4MjE2MTI4IiwiZW1haWwiOiJzaGxvbW9nMTJAZ29vZ2xlbWFpbC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwibmFtZSI6Itep15zXnteUINeS15zXmdenIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hL0FBVFhBSndyTTFzc1NHSkZkQnhZcWc0NHprdDRSNzlLMUI0OC0zVUhRRExpPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6Itep15zXnteUIiwiZmFtaWx5X25hbWUiOiLXktec15nXpyIsImxvY2FsZSI6ImhlIiwiaWF0IjoxNjQwMjE0MTY2LCJleHAiOjE2NDAyMTc3NjZ9.grKBq_m0OBfyFf52fGNvqoXgtXu4jImNNBi4xxUGvM-z2qXy5kPvh4uQli0wnkaqDjdEdvOkRIEeXnnJN-PAJ1gtRQ7aowUX0TTmXqdWWKNCC6Mib0nFIZu-vFmDnwYk3fLnL_SRE365AETGhgK8ssiDIyvHu-esIBxzFPy4V2gG_qg3Z9ZU39hOaXxSnGrgM8NzSLYOoitr5FSzm4sT2fylfN8tUUDq7raMkQyHSAfQ-b8bRwQJp6Ta-ncgXWQvodwjOIGOW1iTtVIF7IGW74z6cF_L2hdRCrrZN5NBdhvYubYEXnRooTDTFJlPAJEv45pE_voeAdAYLncdhtWqcA";
//        String token6 = "cHdwJr1jT52mEEiSNZ1FYt:APA91bGjwt93tRMcGj3QVNk41dthiziBOjkDhkRMqOniItW4VrNyFw--vPbuN6h5qH6AYr55qicf4QArQxGrQwdrrxt0eHWaQ-jgkY_FQaB5m-NwXm3RadVvy2scgrhNI2rszLB_94uQ";
//        FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token6,title
//                ,message,getApplicationContext(),LoginActivity.this);
//        notificationsSender.SendNotifications();
//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();


//        DBTest.test();
    }

    private void showSelectTypeDialog() {
        String[] types = {"עובד", "מנהל"};
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("בחר את סוג המשתמש");
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
                actions.popUpMessage("תודה");
                dialog.dismiss();
                GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);
                curUser.setTokenID(googleAccount.getIdToken());
                DB.setUser(curUser);
                updateToken();
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

    private void updateToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()){
                    String token = task.getResult();
                    DB.setToken(curUser.getMail(),token);
                }
            }
        });
    }


}
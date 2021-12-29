package com.Final.mysalary.Controller.Actions;

import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.Controller.*;
import com.Final.mysalary.R;
import com.Final.mysalary.Model.Callback;
import com.Final.mysalary.Model.DB;
import com.Final.mysalary.Model.DTO.Type;
import com.Final.mysalary.Model.DTO.User;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActions extends UiActions{

    public LoginActions(AppCompatActivity activity) {
        super(activity);
    }


    public void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            DB.CheckIfTheUserMailIsExists(account.getEmail(), new Callback<Boolean>() {
                @Override
                public void play(Boolean isExits) {
                    if (isExits) {
                        DB.getUserByUserMail(account.getEmail(), new Callback<User>() {
                            @Override
                            public void play(User curUser) {
                                moveToMainScreen(curUser);
                            }
                        });
                    } else {
                        User curUser = new User(account.getEmail(), account.getGivenName(), account.getFamilyName(), "", account.getDisplayName(), Type.WORKER);
                        showSelectTypeDialog(curUser);
                    }
                }
            });
        } catch (ApiException e) {
        }
    }


    private void showSelectTypeDialog(User curUser) {
        String[] types = {activity.getResources().getString(R.string.worker), activity.getResources().getString(R.string.boss)};
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getResources().getString(R.string.choose_user_type));
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
                popUpMessage(activity.getResources().getString(R.string.thank_you));
                dialog.dismiss();
                DB.setUser(curUser);
                moveToMainScreen(curUser);
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
    public void forgot() {
        final EditText email = new EditText(activity);
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        email.setHint(activity.getResources().getString(R.string.mail));
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(activity.getResources().getString(R.string.enterMail))
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
                            popUpMessage(activity.getApplicationContext().getString(R.string.mail_incorrect));
                            return;
                        }
                        FirebaseAuth.getInstance().sendPasswordResetEmail(mail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            popUpMessage(activity.getApplicationContext().getString(R.string.thanks_forgot));
                                            dialog.dismiss();
                                        } else
                                            popUpMessage(activity.getApplicationContext().getString(R.string.mail_not_exist));
                                    }
                                });
                    }
                });
            }
        });
        dialog.show();
    }

    public void login(){
        String mail = ((EditText) activity.findViewById(R.id.input_username)).getText().toString();
        String password = ((EditText) activity.findViewById(R.id.input_pass)).getText().toString();
        if (mail.length() < 5) {
            popUpMessage(activity.getApplicationContext().getString(R.string.mail_incorrect));
            return;
        }
        if (password.length() < 6) {
            popUpMessage(activity.getApplicationContext().getString(R.string.wrong_password));
            return;
        }
        mAuth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    DB.getUserByUserMail(mAuth.getCurrentUser().getEmail(), new Callback<User>() {
                        @Override
                        public void play(User curUser) {
                            moveToMainScreen(curUser);
                        }
                    });
                } else {
                    popUpMessage(activity.getApplicationContext().getString(R.string.login_failed));
                    System.out.println(task.getException());
                }
            }
        });
    }

}

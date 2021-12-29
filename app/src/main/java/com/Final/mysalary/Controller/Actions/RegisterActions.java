package com.Final.mysalary.Controller.Actions;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.R;
import com.Final.mysalary.Model.DB;
import com.Final.mysalary.Model.DTO.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActions extends UiActions{


    public RegisterActions(AppCompatActivity activity) {
        super(activity);
    }



    public void registerWithFireBase(User newUser) {
        mAuth.createUserWithEmailAndPassword(newUser.getMail(), newUser.Password())
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            popUpMessage(activity.getApplicationContext().getString(R.string.register_success));
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(newUser.getUserName()).build();
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            System.out.println(newUser.getUserName());
                            firebaseUser.updateProfile(profileChangeRequest);
                            DB.setUser(newUser);
                            moveToMainScreen(newUser);
                        } else {
                            System.out.println(task.getException());
                            popUpMessage(activity.getApplicationContext().getString(R.string.register_failed));
                        }
                    }
                });
    }



}

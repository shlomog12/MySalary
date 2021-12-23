package com.Final.mysalary.Controller;

import android.os.Build;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {

    public static boolean isValidEmail(String mail) {
        return (!TextUtils.isEmpty(mail) && Patterns.EMAIL_ADDRESS.matcher(mail).matches());
    }

    public static boolean isValidInput(String firstName) {
        if (firstName.length() < 1) return false;
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean isValidDateTime(LocalDateTime a, LocalDateTime b) {
        return a.isBefore(b);
    }

    public static boolean isValidPassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}

package com.Final.mysalary.db;

import com.Final.mysalary.DTO.Shift;
import com.Final.mysalary.DTO.User;

import java.util.ArrayList;
import java.util.List;

public interface Callback {

    public void play(User user);
    public void play(boolean bool);
    public void play(ArrayList<Shift> shifts);
    public void play(double num);
}

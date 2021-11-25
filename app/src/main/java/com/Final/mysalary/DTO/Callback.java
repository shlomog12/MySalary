package com.Final.mysalary.DTO;

import java.util.ArrayList;
import java.util.List;

public interface Callback {

    public void play(User user);
    public void play(boolean bool);
    public void play(ArrayList<Shift> shifts);
}

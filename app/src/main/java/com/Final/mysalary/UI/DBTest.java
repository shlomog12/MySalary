package com.Final.mysalary.UI;

import android.os.Build;
import androidx.annotation.RequiresApi;
import com.Final.mysalary.DTO.Job;
import com.Final.mysalary.DTO.Shift;
import com.Final.mysalary.DTO.Type;
import com.Final.mysalary.DTO.User;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class DBTest{


    static User user;
    private static void initUser() {
        user = new User();
        user.setUserName("testDb4");
        user.setFirstName("testDB9_12");
        user.setLastName("LAtestDB9_12");
        user.setMail("tv1@gmail.com");
        user.setType(Type.WORKER.ordinal());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void test(){
//        initUser();
//        testSetUser();
//        testSetJobs();
//        testSetShifts();
//        testGetShifts();
//        testGetUser();
//        testCheckIfTheUserNameIsExists();
//        testGetJobs();
//        testgetShiftsByBossMail();
        testgetMailsOfWorkersByBossMail();

    }

    private static void testgetMailsOfWorkersByBossMail() {
        DB.getMailsOfWorkersByBossMail("btest1@gmail.com", new Callback<ArrayList<String>>() {
            @Override
            public void play(ArrayList<String> strings) {
                System.out.println("MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMm");
                System.out.println(Arrays.toString(strings.toArray()));
                System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIIIIIIIIIIIIIIIIILLLLLLLLLLLLLL");
            }


        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void testgetShiftsByBossMail() {
        DB.getShiftsByBossMail(LocalDateTime.MIN, LocalDateTime.MAX, "btest1@gmail.com", new Callback<ArrayList<Shift>>() {
            @Override
            public void play(ArrayList<Shift> shifts) {
                System.out.println("********************8start**********************************");
                System.out.println(Arrays.toString(shifts.toArray()));
                System.out.println("********************end**********************************");
            }

        });
    }

    private static void testSetUser() {

        DB.setUser(user);
    }
    private static void testSetJobs() {
        Job job = new Job("bosstest1@gmail.com","50",user.getMail(),"teacher");
        DB.setInJobs(job);
//        Job job2 = new Job("bosstest2@gmail.com","200",user.getMail(),"programmer");
//        DB.setInJobs(job2);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void testSetShifts(){
        Shift shift =new Shift(LocalDateTime.of(2021,12,9,20,55,44),LocalDateTime.of(2021,12,10,07,22,04),user.getMail(),"שש");
        DB.setInShifts(shift);


        Shift shift2 =new Shift(LocalDateTime.of(2020,11,30,20,55,44),LocalDateTime.of(2020,12,1,07,22,04),user.getMail(),"מלצרות");
        DB.setInShifts(shift2);
    }
    private static void testGetJobs() {
        DB.getJobs(user.getMail(), new Callback<ArrayList<String>>() {
            @Override
            public void play(ArrayList<String> jobs) {
                System.out.println("************************************TEST**************************************8");
                for (String jobName:jobs) {
                    System.out.println(jobName);
                }
                System.out.println("**************************************************************************8");
            }
        });



    }
    private static void testCheckIfTheUserNameIsExists() {
        DB.CheckIfTheUserMailIsExists(user.getMail(), new Callback<Boolean>() {
            @Override
            public void play(Boolean isExits) {
                System.out.println("************************************TEST**************************************8");
                System.out.println(isExits);
                System.out.println("**************************************************************************8");
            }
        });

    }
    private static void testGetUser() {
        DB.getUserByUserMail(user.getMail(), new Callback<User>() {
            @Override
            public void play(User user) {
                System.out.println("************************************TEST**************************************8");
                System.out.println(user);
                System.out.println("**************************************************************************8");
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void testGetShifts() {
        DB.getShifts("",  LocalDateTime.MIN ,LocalDateTime.MAX,user.getMail(), new Callback<ArrayList<Shift>>() {
            @Override
            public void play(ArrayList<Shift> shifts) {
                System.out.println("************************************TEST**************************************8");
                for (Shift shift: shifts) {
                    System.out.println(shift);
                }
                System.out.println("**************************************************************************8");
            }
        });


    }



}
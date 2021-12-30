package com.Final.mysalary;

import static com.google.common.collect.Comparators.max;
import static com.google.common.collect.Comparators.min;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static java.lang.Thread.sleep;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.Final.mysalary.Model.Callback;
import com.Final.mysalary.Model.DB;
import com.Final.mysalary.Model.DTO.Job;
import com.Final.mysalary.Model.DTO.Shift;
import com.Final.mysalary.Model.DTO.Type;
import com.Final.mysalary.Model.DTO.User;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DBTest {
    private static Random random =new Random(1);
    static User user;
    String jobName;
    String mailBoss;

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.Final.mysalary", appContext.getPackageName());
    }


    @BeforeClass
    public static void beforeClass() throws Exception {
        initUser();
    }

    private static void initUser() {
        user = new User();
        user.setUserName(getRandomString());
        user.setFirstName(getRandomString());
        user.setLastName(getRandomString());
        user.setMail(getRandomString()+"@gmail.com");
        user.setType(Type.WORKER.ordinal());
    }

    @Test
    public void setAndGetUserTest() {
        DB.setUser(user);
        DB.getUserByUserMail(user.getMail(), new Callback<User>() {
            @Override
            public void play(User userFromDb) {
                assertEquals(user.getMail(), userFromDb.getMail());
                assertEquals(user.getType(), userFromDb.getType());
                assertEquals(user.getUserName(), userFromDb.getUserName());
                assertEquals(user.getFirstName(), userFromDb.getFirstName());
                assertEquals(user.getLastName(), userFromDb.getLastName());
            }
        });
    }

    @Test
    public void setAndGetJobsTest() {
        mailBoss = getRandomString()+"@gmail.com";
        String SalaryForHour = String.valueOf(random.nextDouble());
        jobName = getRandomString();
        Job job = new Job(mailBoss,SalaryForHour,user.getMail(),jobName);
        DB.setInJobs(job);
        waitToUpdate();
        DB.getJobs(user.getMail(), new Callback<ArrayList<String>>() {
            @Override
            public void play(ArrayList<String> jobs) {
                assertTrue(jobs.contains(jobName));
            }
        });

        DB.getJobsByBossMail(mailBoss, new Callback<ArrayList<Job>>() {
            @Override
            public void play(ArrayList<Job> jobs) {
                    boolean exits = false;
                    for (Job jobFromDb: jobs) {
                        String jobName = jobFromDb.getJobName();
                        String MailWorker = jobFromDb.getMailWorker();
                        if (jobName.equals(job.getJobName()) && MailWorker.equals(job.getMailWorker())){
                            exits = true;
                            assertEquals(job.getMailBoss(),jobFromDb.getMailBoss());
                            assertEquals(job.getSalaryForHour(),jobFromDb.getSalaryForHour());
                            break;
                        }
                    }
                    assertTrue(exits);
            }
        });
    }

    @Test
    public void setAndGetShifts() {
        waitToUpdate();
        LocalDateTime time1 = getRandomDate();
        LocalDateTime time2 = getRandomDate();
        LocalDateTime start = min(time1,time2);
        LocalDateTime end = max(time1,time2);
        Shift shift = new Shift(start,end,user.getMail(),jobName);
        DB.setInShifts(shift);
        waitToUpdate();
        DB.getShifts("",  LocalDateTime.MIN ,LocalDateTime.MAX,user.getMail(), new Callback<ArrayList<Shift>>() {
            @Override
            public void play(ArrayList<Shift> shifts) {
                boolean exits = false;
                for (Shift shiftFromDb: shifts) {
                    String jobName = shiftFromDb.JobName();
                   String id = shiftFromDb.gShiftId();
                   if (jobName.equals(shift.JobName()) && id.equals(shiftFromDb.gShiftId())){
                       exits = true;
                       assertEquals(shift.getStart(),shiftFromDb.getStart());
                       assertEquals(shift.getEnd(),shiftFromDb.getEnd());
                       break;
                   }
                }
                assertTrue(exits);
            }
        });
        DB.getShiftsByBossMail(LocalDateTime.MIN, LocalDateTime.MAX, mailBoss, new Callback<ArrayList<Shift>>() {
            @Override
            public void play(ArrayList<Shift> shifts) {
                boolean exits = false;
                for (Shift shiftFromDb: shifts) {
                    String jobName = shiftFromDb.JobName();
                    String id = shiftFromDb.gShiftId();
                    if (jobName.equals(shift.JobName()) && id.equals(shiftFromDb.gShiftId())){
                        exits = true;
                        assertEquals(shift.getStart(),shiftFromDb.getStart());
                        assertEquals(shift.getEnd(),shiftFromDb.getEnd());
                        break;
                    }
                }
                assertTrue(exits);
            }

        });

    }

    @Test
    public void checkIfTheUserNameIsExistsTest() {
        DB.CheckIfTheUserMailIsExists(user.getMail(), new Callback<Boolean>() {
            @Override
            public void play(Boolean isExits) {
                assertTrue(isExits);
            }
        });










    }




    private LocalDateTime getRandomDate() {
        int year = getRandomInt(1990,2025);
        int month = getRandomInt(1,12);
        int dayInMonth = getRandomInt(1,30);
        int hour = getRandomInt(1,24);
        int minute = getRandomInt(0,59);
        return LocalDateTime.of(year,month,dayInMonth,hour,minute);
    }

    public static String getRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;


        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }

    public int getRandomInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private void waitToUpdate() {
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
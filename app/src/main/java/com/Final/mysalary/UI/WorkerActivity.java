package com.Final.mysalary.UI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.DTO.Job;
import com.Final.mysalary.DTO.Shift;
import com.Final.mysalary.DTO.ShiftsAdapter;
import com.Final.mysalary.R;
import com.Final.mysalary.db.Callback;
import com.Final.mysalary.db.DB;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class WorkerActivity extends AppCompatActivity {

    ShiftsAdapter myAdapter;
    FirebaseAuth mAuth;
    String user_name;
    ArrayList<Shift> list;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);

        list = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser curr_user = mAuth.getCurrentUser();
        user_name = curr_user.getDisplayName();

//        recyclerView = findViewById(R.id.shiftsList);
        DB.getShifts(-1, LocalDateTime.MIN, LocalDateTime.MAX, user_name, new Callback<ArrayList<Shift>>() {
            @Override
            public void play(ArrayList<Shift> shifts) {
                // Now create the instance of the NumebrsViewAdapter and pass
                // the context and arrayList created above
                ShiftsAdapter shiftsArrayAdapter = new ShiftsAdapter(WorkerActivity.this, shifts);

                // create the instance of the ListView to set the numbersViewAdapter
                ListView shiftsListView = findViewById(R.id.listView);

                // set the numbersViewAdapter for ListView
                shiftsListView.setAdapter(shiftsArrayAdapter);

//                list.add(shift);
//                shiftsArrayAdapter.notifyDataSetChanged();
            }
        });
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        myAdapter = new ShiftsAdapter(this, list);
//        recyclerView.setAdapter(myAdapter);


//        DB.getShifts();

//        database.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot datasnapshot : snapshot.getChildren()) {
//                    Shift shift = datasnapshot.getValue(Shift.class);
//                    list.add(shift);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    private void showTotalSumOfSalary() {
    }

    private void showListOfShifts() {
    }


    private void addJob() {
        openWindowAddJob();
        Job newJob = new Job(getUserNameWarker(), getUserNameBoss(), "22");
        DB.setInJobs(newJob);
        closeWindowAddJob();
    }

    private int getJobIdFromScrean() {
        return 0;
    }

    private String getUserNameBoss() {
        return "";
    }

    private String getUserNameWarker() {
        return "";
    }

    private void closeWindowAddJob() {

    }

    private void openWindowAddJob() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addNewShift() {
        popUpAddShiftWindow();
        Shift shift = new Shift(getStart(), getEnd(), getMail(), getJobId());
        DB.setInShifts(shift);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void StartAndEndShift() {
        if (ButtonIsStart()) {
            Shift shift = new Shift(LocalDateTime.now(), null, getMail(), getJobId());
            DB.setInShifts(shift);
        } else updateEndOfShift();
    }

    private void sortShifts() {
    }


    private void updateEndOfShift() {
    }

    private boolean ButtonIsStart() {
        return false;
    }

    private int getJobId() {
        return 0;
    }

    private String getMail() {
        return "";
    }

    private LocalDateTime getEnd() {
        return null;
    }

    private LocalDateTime getStart() {
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveShift() {
//        LocalDateTime start = LocalDateTime.of(2021,11,15,21,22);
//        LocalDateTime end = LocalDateTime.of(2021,11,15,21,22);
        Shift shift = new Shift(null, null, "0", 0);
        DB.setInShifts(shift);
        closeAddShiftWindow();
    }

    private void popUpAddShiftWindow() {
    }

    private void closeAddShiftWindow() {
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }
}
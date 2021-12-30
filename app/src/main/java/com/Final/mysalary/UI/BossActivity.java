package com.Final.mysalary.UI;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.Final.mysalary.Controller.Actions.BossActions;
import com.Final.mysalary.R;

@RequiresApi(api = Build.VERSION_CODES.O)
public class BossActivity extends AppCompatActivity {

    BossActions actions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss);
        actions = new BossActions(this);
    }

    public void onStart() {
        super.onStart();
        actions.updateUser();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.boss_menu, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_boss_search:
                actions.showSearch();
                return true;
            case R.id.menu_boss_logout:
                actions.logout();
                return true;
            case R.id.refresh_shifts:
                actions.refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
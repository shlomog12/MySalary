package com.Final.mysalary.UI;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
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
        actions.setTimer();
    }

    public void onStart() {
        super.onStart();
        actions.updateUser();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.boss_menu, menu);
//        ActionBar bar = getSupportActionBar();
//        bar.setCustomView(R.layout.live_switch_layout);
//        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);
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
            case R.id.live_switch_bar:
                Switch toggle = (Switch) findViewById(R.id.live_switch_bar);
                toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            actions.popUpMessage("ON");
                        }
                        else{
                            actions.popUpMessage("OFF");
                        }
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
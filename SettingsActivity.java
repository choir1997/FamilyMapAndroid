package edu.byu.cs240.familymap.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Family Map: Settings");

        SwitchCompat lifeStoryLines;
        SwitchCompat familyTreeLines;
        SwitchCompat spouseLines;
        SwitchCompat fatherSide;
        SwitchCompat motherSide;
        SwitchCompat maleEvent;
        SwitchCompat femaleEvent;
        Button logout;

        lifeStoryLines = findViewById(R.id.switch1);
        familyTreeLines = findViewById(R.id.switch2);
        spouseLines = findViewById(R.id.switch3);
        fatherSide = findViewById(R.id.switch4);
        motherSide = findViewById(R.id.switch5);
        maleEvent = findViewById(R.id.switch6);
        femaleEvent = findViewById(R.id.switch7);

        logout = findViewById(R.id.logoutBtn);

        if (DataCache.isShowLifeStoryLines()) {
            lifeStoryLines.setChecked(true);
        }

        if (DataCache.isShowFamilyTreeLines()) {
            familyTreeLines.setChecked(true);
        }

        if (DataCache.isShowSpouseLines()) {
            spouseLines.setChecked(true);
        }

        if (DataCache.isShowFatherSide()) {
            fatherSide.setChecked(true);
        }

        if (DataCache.isShowMotherSide()) {
            motherSide.setChecked(true);
        }

        if (DataCache.showMaleEvents()) {
            maleEvent.setChecked(true);
        }

        if (DataCache.showFemaleEvents()) {
            femaleEvent.setChecked(true);
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager = v.getContext().getPackageManager();
                Intent intent = packageManager.getLaunchIntentForPackage(v.getContext().getPackageName());
                v.getContext().startActivity(Intent.makeRestartActivityTask(intent.getComponent()));
                Runtime.getRuntime().exit(0);
            }
        });

        familyTreeLines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCache.setShowFamilyTreeLines(familyTreeLines.isChecked());
            }
        });

        lifeStoryLines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCache.setShowLifeStoryLines(lifeStoryLines.isChecked());
            }
        });

        spouseLines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCache.setShowSpouseLines(spouseLines.isChecked());
            }
        });

        fatherSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCache.setShowFatherSide(fatherSide.isChecked());
            }
        });

        motherSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCache.setShowMotherSide(motherSide.isChecked());
            }
        });

        maleEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: need to fix the toggle on/off
                DataCache.setShowMaleEvents(maleEvent.isChecked());
            }
        });

        femaleEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataCache.setShowFemaleEvents(femaleEvent.isChecked());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}
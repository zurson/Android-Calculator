package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.main_menu);
        prepareButtons();
    }

    private void prepareButtons() {
        findViewById(R.id.button_simple).setOnClickListener(v -> openSimpleCalc());
        findViewById(R.id.button_advanced).setOnClickListener(v -> openAdvancedCalc());
        findViewById(R.id.button_about).setOnClickListener(v -> openAboutWindow());

        findViewById(R.id.button_exit).setOnClickListener(v -> {
            finish();
            System.exit(0);
        });

    }

    private void openSimpleCalc() {
        startActivity(getIntent(SimpleCalculator.class));
    }

    private void openAdvancedCalc() {
        startActivity(getIntent(AdvancedCalculator.class));
    }

    private void openAboutWindow() {
        startActivity(getIntent(InformationActivity.class));
    }

    private Intent getIntent(Class<? extends AppCompatActivity> activityClass) {
        return new Intent(this, activityClass);
    }

}
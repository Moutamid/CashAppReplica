package com.moutamid.cashapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.moutamid.cashapp.databinding.ActivityMainBinding;
import com.moutamid.cashapp.utils.Constants;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.payBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SelectContactActivity.class)
                    .putExtra(Constants.PARAMS, b.currentCount.getText().toString()));
            Animatoo.INSTANCE.animateSlideUp(MainActivity.this);
        });

    }

    boolean isFirstTime = true;

    public void clickListener(View view) {
        String tag = view.getTag().toString();

        if (tag.equals("b")) {

            if (isFirstTime)
                return;

            String amount = b.currentCount.getText().toString();

            if (amount.length() == 1) {
                b.currentCount.setText("0");
                isFirstTime = true;
                return;
            }

            amount = amount.substring(0, amount.length() - 1);

            b.currentCount.setText(amount);
            return;
        }

        if (isFirstTime) {
            // FIRST TIME
            isFirstTime = false;

            if (tag.equals(".")) {
                b.currentCount.setText("0.");

            } else
                b.currentCount.setText(tag);
        } else {
            // NOT FIRST TIME
            b.currentCount.setText(b.currentCount.getText().toString() + tag);
        }

    }

}









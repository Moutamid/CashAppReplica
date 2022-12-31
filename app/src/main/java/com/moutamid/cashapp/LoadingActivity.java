package com.moutamid.cashapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        animate();

        new Handler().postDelayed(() -> {
            finish();
            startActivity(new Intent(LoadingActivity.this, LastScreenActivity.class));
        }, 8000);

    }

    private void animate() {

        ImageView imageView = findViewById(R.id.progressBar);
        imageView.animate().rotation(imageView.getRotation() + 50).start();

        new Handler().postDelayed(() -> {
            animate();

        }, 100);

    }

}
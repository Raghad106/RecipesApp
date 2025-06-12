package com.ucas.firebaseminiproject.ui;

import static com.ucas.firebaseminiproject.utilities.Constance.FIRST_RUN_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.IS_REMEMBERED_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.SHARED_PREFERENCES_NAME;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ucas.firebaseminiproject.R;
import com.ucas.firebaseminiproject.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {
    ActivitySplashBinding binding;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    boolean isRemembered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        boolean isFirstRun = sharedPreferences.getBoolean(FIRST_RUN_KEY, true);
        if (isFirstRun) {
            //clear SharedPreferences on first run
            editor.clear().apply();
            //set first run to false
            editor.putBoolean(FIRST_RUN_KEY, false);
            editor.apply();
        }

        isRemembered = sharedPreferences.getBoolean(IS_REMEMBERED_KEY, false);

        new Handler().postDelayed(() -> {
            Intent intent;
            if (!isRemembered) {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            } else {
//                if (studentId == -1) {
//                    intent = new Intent(SplashActivity.this, LoginActivity.class);
//                } else {
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
//                    intent.putExtra(STUDENT_ID_KEY, studentId);
//                    Log.d("student id", studentId+"");
               // }
            }
            startActivity(intent);
            finish();
        }, 1000);


    }
}
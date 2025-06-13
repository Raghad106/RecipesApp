package com.ucas.firebaseminiproject.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.ucas.firebaseminiproject.R;
import com.ucas.firebaseminiproject.data.viewmodels.AuthViewModel;
import com.ucas.firebaseminiproject.databinding.ActivityLoginBinding;
import com.ucas.firebaseminiproject.utilities.ViewsCustomListeners;

import java.util.ArrayList;
import java.util.List;
public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private AuthViewModel authViewModel;
    boolean isPasswordShown = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.tvRedirectRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        binding.passwordEyeIcon.setOnClickListener(v ->{
            isPasswordShown = ViewsCustomListeners.hideAndShowPassword(isPasswordShown, binding.etLoginPassword);
        });


        binding.btnLogin.setOnClickListener(view -> {
            authViewModel.saveRememberInfo(LoginActivity.this, binding.cbRememberMe.isChecked());
            List<EditText> editTextList = new ArrayList<>();
            editTextList.add(binding.etLoginEmail);
            editTextList.add(binding.etLoginPassword);

            boolean isEmpty = ViewsCustomListeners.sureAllEditTextsNotEmpty(editTextList, binding.tvErrorMessage);
            if (!isEmpty){
                authViewModel.loginUser(binding.etLoginEmail.getText().toString(), binding.etLoginPassword.getText().toString(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "login Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        // Go to home or login
                    } else {
                        Log.d("Login Error", task.getException().getMessage());
                        binding.tvErrorMessage.setText("User Not Exists");
                    }
                });
            }
        });
    }
}
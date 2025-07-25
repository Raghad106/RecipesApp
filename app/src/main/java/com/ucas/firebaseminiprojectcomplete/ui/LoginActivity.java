package com.ucas.firebaseminiprojectcomplete.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ucas.firebaseminiprojectcomplete.R;
import com.ucas.firebaseminiprojectcomplete.data.viewmodels.AuthViewModel;
import com.ucas.firebaseminiprojectcomplete.databinding.ActivityLoginBinding;
import com.ucas.firebaseminiprojectcomplete.utilities.ViewsCustomListeners;

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
            binding.btnLogin.setEnabled(false);
            binding.btnLogin.setBackgroundColor(Color.parseColor("#FFB379"));
            authViewModel.saveRememberInfo(LoginActivity.this, binding.cbRememberMe.isChecked());
            List<EditText> editTextList = new ArrayList<>();
            editTextList.add(binding.etLoginEmail);
            editTextList.add(binding.etLoginPassword);

            boolean isEmpty = ViewsCustomListeners.sureAllEditTextsNotEmpty(editTextList, binding.tvErrorMessage);
            if (!isEmpty){
                authViewModel.loginUser(binding.etLoginEmail.getText().toString(), binding.etLoginPassword.getText().toString(), task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "login Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // To enabled button if there's firebase's problems
                        binding.btnLogin.setEnabled(true);
                        binding.btnLogin.setBackgroundColor(Color.parseColor("#FF8600"));
                        String message = task.getException().getMessage();
                        if (message.contains("Email not verified")) {
                            binding.tvErrorMessage.setText("Please verify your email first.");
                        } else {
                            binding.tvErrorMessage.setText("Invalid credentials or user does not exist.");
                        }
                    }
                });
                // To enabled button if there's empty fields
            }else {
                binding.btnLogin.setEnabled(true);
                binding.btnLogin.setBackgroundColor(Color.parseColor("#FF8600"));
            }
        });
    }
}
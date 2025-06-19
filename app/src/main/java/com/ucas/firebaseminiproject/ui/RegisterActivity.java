package com.ucas.firebaseminiproject.ui;

import static com.ucas.firebaseminiproject.utilities.Constance.COUNTRY_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.EMAIL_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.NAME_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.PASSWORD_MAP_KEY;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
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
import com.ucas.firebaseminiproject.databinding.ActivityRegisterBinding;
import com.ucas.firebaseminiproject.utilities.ViewsCustomListeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;
    boolean isPasswordShown = false;
    boolean isConfirmPasswordShown = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.country_list,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCountry.setAdapter(adapter);

        binding.passwordEyeIcon.setOnClickListener(v ->{
            isPasswordShown = ViewsCustomListeners.hideAndShowPassword(isPasswordShown, binding.etPassword);
        });

        binding.passwordConfirmEyeIcon.setOnClickListener(v ->{
            isConfirmPasswordShown = ViewsCustomListeners.hideAndShowPassword(isConfirmPasswordShown, binding.etConfirmPassword);
        });

        binding.tvRedirectLogin.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

        binding.btnRegister.setOnClickListener(view -> {
            List<EditText> editTextList = new ArrayList<>();
            editTextList.add(binding.etEmail);
            editTextList.add(binding.etName);
            editTextList.add(binding.etPassword);
            editTextList.add(binding.etConfirmPassword);
            //sure that the edit texts list  is not empty
            boolean isEmpty = ViewsCustomListeners.sureAllEditTextsNotEmpty(editTextList, binding.tvErrorMessage);
            if (!isEmpty){
                String password = binding.etPassword.getText().toString();
                String confirmPassword = binding.etConfirmPassword.getText().toString();
                if (!password.equals(confirmPassword)) {
                    binding.tvErrorMessage.setText("Passwords do not match!");
                    return;
                }
                if (binding.etPassword.getText().toString().equals(binding.etConfirmPassword.getText().toString())){
                    Map<String, Object> map = new HashMap<>();
                    map.put(EMAIL_MAP_KEY, binding.etEmail.getText().toString());
                    map.put(NAME_MAP_KEY, binding.etName.getText().toString());
                    map.put(PASSWORD_MAP_KEY, binding.etPassword.getText().toString());
                    map.put(COUNTRY_MAP_KEY, binding.spinnerCountry.getSelectedItem().toString());

                    authViewModel.registerUser(map, task -> {
                        if (task.isSuccessful()) {
                            binding.btnRegister.setEnabled(false);
                            Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            // Go to home or login
                        } else {
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
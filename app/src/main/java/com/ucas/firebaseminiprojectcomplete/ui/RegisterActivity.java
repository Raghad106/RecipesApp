package com.ucas.firebaseminiprojectcomplete.ui;

import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.COUNTRY_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.EMAIL_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.NAME_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.PASSWORD_MAP_KEY;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ucas.firebaseminiprojectcomplete.R;
import com.ucas.firebaseminiprojectcomplete.data.viewmodels.AuthViewModel;
import com.ucas.firebaseminiprojectcomplete.databinding.ActivityRegisterBinding;
import com.ucas.firebaseminiprojectcomplete.utilities.ViewsCustomListeners;

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
            binding.btnRegister.setEnabled(false);
            binding.btnRegister.setBackgroundColor(Color.parseColor("#FFB379"));
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
                            binding.tvErrorMessage.setText("Please verify your email first, Check the spam");
                            authViewModel.signOut();
                            // Navigate to login after short delay to allow user to read the message
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish(); // prevent going back to register
                            }, 2000); // 2 seconds delay
                        } else {
                            binding.btnRegister.setEnabled(true);
                            binding.btnRegister.setBackgroundColor(Color.parseColor("#FF8600"));
                            Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            else {
                binding.btnRegister.setEnabled(true);
                binding.btnRegister.setBackgroundColor(Color.parseColor("#FF8600"));
            }
        });
    }
}
package com.ucas.firebaseminiproject.data.viewmodels;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.ucas.firebaseminiproject.data.repositories.AuthRepository;

import java.util.Map;

public class AuthViewModel extends ViewModel {
    private AuthRepository repository = new AuthRepository();

    public void registerUser(Map<String, Object> userData, OnCompleteListener<Void> listener) {
        repository.register(userData, listener);
    }
}

package com.ucas.firebaseminiproject.data.viewmodels;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.ucas.firebaseminiproject.data.repositories.AuthRepository;
import com.ucas.firebaseminiproject.utilities.OnFirebaseLoadedListener;

import java.util.Map;

public class AuthViewModel extends ViewModel {
    private AuthRepository repository = new AuthRepository();

    public void registerUser(Map<String, Object> userData, OnCompleteListener<Void> listener) {
        repository.register(userData, listener);
    }

    public void loginUser(String email, String password, OnCompleteListener<AuthResult> listener){
        repository.login(email, password, listener);
    }

    public void saveRememberInfo(Context context, Boolean isRememberMe){
        repository.saveRememberInfo(context, isRememberMe);
    }

    public boolean getRememberInfo(Context context){
        return repository.getRememberInfo(context);
    }

}

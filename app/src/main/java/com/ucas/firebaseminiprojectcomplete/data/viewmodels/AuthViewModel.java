package com.ucas.firebaseminiprojectcomplete.data.viewmodels;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.ucas.firebaseminiprojectcomplete.data.repositories.AuthRepository;

import java.util.Map;

public class AuthViewModel extends ViewModel {
    private AuthRepository repository = new AuthRepository();

    public void registerUser(Map<String, Object> userData, OnCompleteListener<Void> listener) {
        repository.register(userData, listener);
    }

    public void loginUser(String email, String password, OnCompleteListener<FirebaseUser> listener){
        repository.login(email, password, listener);
    }

    public void signOut(){
        repository.signOut();
    }

    public void saveRememberInfo(Context context, Boolean isRememberMe){
        repository.saveRememberInfo(context, isRememberMe);
    }

    public boolean getRememberInfo(Context context){
        return repository.getRememberInfo(context);
    }

}

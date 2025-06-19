package com.ucas.firebaseminiproject.data.viewmodels;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.ucas.firebaseminiproject.data.repositories.ProfileRepository;
import com.ucas.firebaseminiproject.utilities.OnFirebaseLoadedListener;

import java.util.Map;

public class ProfileViewModel extends ViewModel {
    ProfileRepository repository = new ProfileRepository();

    public void getRecipesByUserId(OnFirebaseLoadedListener.OnRecipesLoaded loaded){
        repository.getRecipesByUserId(loaded);
    }

    public void getCurrentUserInfo(OnFirebaseLoadedListener.OnUserInfoLoadedListener listener){
        repository.getCurrentUserInfo(listener);
    }

    public void getUserInfoById(String userId, OnFirebaseLoadedListener.OnUserInfoLoadedListener listener){
        repository.getUserInfoById(userId, listener);
    }
    public void updateUserInf(String userId, Map<String, String> userInfo, OnCompleteListener<Void> listener){
        repository.updateUserInf(userId, userInfo, listener);
    }
}

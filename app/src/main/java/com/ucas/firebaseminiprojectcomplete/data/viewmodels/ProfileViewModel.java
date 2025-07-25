package com.ucas.firebaseminiprojectcomplete.data.viewmodels;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.ucas.firebaseminiprojectcomplete.data.repositories.ProfileRepository;
import com.ucas.firebaseminiprojectcomplete.utilities.OnFirebaseLoadedListener;

import java.util.Map;

public class ProfileViewModel extends ViewModel {
    ProfileRepository repository = new ProfileRepository();

    public void getRecipesByUserId(String userId,OnFirebaseLoadedListener.OnRecipesLoaded loaded){
        repository.getRecipesByUserId(userId, loaded);
    }

    public void getCurrentUserInfo(OnFirebaseLoadedListener.OnUserInfoLoadedListener listener){
        repository.getCurrentUserInfo(listener);
    }

    public void getUserInfoById(String userId, OnFirebaseLoadedListener.OnUserInfoLoadedListener listener){
        repository.getUserInfoById(userId, listener);
    }
    public void updateUserInfo(String userId, Map<String, String> userInfo, OnCompleteListener<Void> listener){
        repository.updateUserInfo(userId, userInfo, listener);
    }

    public void toggleSaveRecipe(String recipeId, OnFirebaseLoadedListener.OnSaveRecipeLoadedListener listener){
        repository.toggleSaveRecipe(recipeId, listener);
    }

    public void checkIsSaved(String recipeId, OnFirebaseLoadedListener.OnSaveRecipeLoadedListener listener) {
        repository.checkIsSaved(recipeId, listener);
    }

    public void getSavedRecipes(OnFirebaseLoadedListener.OnRecipesLoaded listener){
        repository.getSavedRecipes(listener);
    }

    public void getTopCreators(int minCount, OnFirebaseLoadedListener.OnUsersInfoLoadedListener listener){
        repository.getTopCreators(minCount, listener);
    }
}

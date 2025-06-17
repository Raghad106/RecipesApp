package com.ucas.firebaseminiproject.utilities;

import com.ucas.firebaseminiproject.data.models.Recipe;

import java.util.List;
import java.util.Map;

public interface OnFirebaseLoadedListener {
    interface OnCategoriesLoaded{
        void onCategoriesLoaded(List<String> categories);
    }
    interface OnRecipeLoaded{
        void onRecipeLoaded(List<Recipe> recipes);
    }
    interface OnUserInfoLoadedListener{
        void onUserInfoLoaded(Map<String, String> userInfo);
    }
}

package com.ucas.firebaseminiprojectcomplete.utilities;

import com.ucas.firebaseminiprojectcomplete.data.models.Recipe;

import java.util.List;
import java.util.Map;

public interface OnFirebaseLoadedListener {
    interface OnCategoriesLoaded{
        void onCategoriesLoaded(List<String> categories);
    }
    interface OnRecipesLoaded {
        void onRecipeLoaded(List<Recipe> recipes);
    }
    interface OnRecipeLoaded {
        void onRecipeLoaded(Recipe recipe);
    }
    interface OnUserInfoLoadedListener{
        void onUserInfoLoaded(Map<String, String> userInfo);
    }
    interface OnUsersInfoLoadedListener{
        void onUsersInfoLoaded(List<Map<String, String>> userInfo);
    }
    interface OnLikeLoadedListener{
        void onLikeLoadedListener(boolean isLike, long likeCount);
    }
    interface OnSaveRecipeLoadedListener{
        void onSaveRecipeLoadedListener(boolean isSave);
    }
}

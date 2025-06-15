package com.ucas.firebaseminiproject.utilities;

import java.util.List;

public interface OnRecipeListener {
     boolean onLikeClicked(String recipeId, String userId);
     boolean onSaveClicked(String recipeId, String userId);

     void onNavigateFragment(String tag);

     public interface OnCategoryListener{
          void onCategoryClicked();
     }
}


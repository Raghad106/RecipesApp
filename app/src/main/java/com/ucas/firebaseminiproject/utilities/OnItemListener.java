package com.ucas.firebaseminiproject.utilities;

import com.ucas.firebaseminiproject.data.models.Recipe;

public interface OnItemListener {

     interface OnRecipeListener{
          boolean onLikeClicked(String recipeId, String userId);
          boolean onSaveClicked(String recipeId, String userId);
          void onLayoutClicked(String recipeId, String userId);
          void onVideoClicked(String videoLink);
     }

     interface OnFragmentListener{
          void onNavigateFragments(String tag);
          void onNavigateRecipeDetailsFragment(String recipeId, String publisherId);
          void onNavigateEditRecipeDetailsFragment(String tag, Recipe recipe);
     }

     public interface OnCategoryListener{
          void onCategoryClicked();
     }
}


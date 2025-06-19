package com.ucas.firebaseminiproject.utilities;

import com.ucas.firebaseminiproject.data.models.Recipe;

public interface OnItemListener {

     interface OnRecipeListener{
          void onLayoutClicked(String recipeId, String userId);
          void onVideoClicked(String videoLink);
     }

     interface OnFragmentListener{
          void onNavigateFragments(String tag);
          void onNavigateRecipeDetailsFragment(String recipeId, String publisherId);
          void onDeleteRecipe(String tag, String recipeId);
          void onNavigateEditRecipeDetailsFragment(String tag, Recipe recipe);
     }

     interface OnCategoryListener{
          void onCategoryClicked();
     }

     interface OnIntentListener {
          void onIntentListener(String tag);
     }
}


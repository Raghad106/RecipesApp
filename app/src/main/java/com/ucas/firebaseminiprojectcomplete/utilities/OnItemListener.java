package com.ucas.firebaseminiprojectcomplete.utilities;

import com.ucas.firebaseminiprojectcomplete.data.models.Recipe;

public interface OnItemListener {

     interface OnRecipeListener{
          void onLayoutClicked(String recipeId, String userId);
          void onUserClicked(String userId);
          void onVideoClicked(String videoLink);
     }

     interface OnFragmentListener{
          void onNavigateFragments(String tag);
          void onNavigateRecipeDetailsFragment(String recipeId, String publisherId);
          void onDeleteRecipe(String tag, Recipe recipe);
          void onNavigateToUserProfile(String tag, String userId);
          void onNavigateEditRecipeDetailsFragment(String tag, Recipe recipe);
     }

     interface OnCategoryListener{
          void onCategoryClicked();
     }

     interface OnUserListener{
          void onUserClicked(String userId);
     }

     interface OnIntentListener {
          void onIntentListener(String tag);
     }
}


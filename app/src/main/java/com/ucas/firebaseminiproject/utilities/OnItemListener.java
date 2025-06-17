package com.ucas.firebaseminiproject.utilities;

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
     }

     public interface OnCategoryListener{
          void onCategoryClicked();
     }
}


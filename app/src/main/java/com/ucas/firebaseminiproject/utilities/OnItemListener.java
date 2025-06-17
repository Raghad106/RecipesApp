package com.ucas.firebaseminiproject.utilities;

public interface OnItemListener {

     interface OnRecipeListener{
          boolean onLikeClicked(String recipeId, String userId);
          boolean onSaveClicked(String recipeId, String userId);
          boolean onVideoClicked(String videoLink);
     }

     interface OnFragmentListener{
          void onNavigateFragment(String tag);
     }

     public interface OnCategoryListener{
          void onCategoryClicked();
     }
}


package com.ucas.firebaseminiproject.utilities;

import com.ucas.firebaseminiproject.data.models.Recipe;

import java.util.List;

public interface OnFirebaseLoadedListener {
    void onCategoriesLoaded(List<String> categories);
    void onRecipeLoaded(List<Recipe> recipes);
}

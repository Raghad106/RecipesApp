package com.ucas.firebaseminiproject.data.viewmodels;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.ucas.firebaseminiproject.data.models.Recipe;
import com.ucas.firebaseminiproject.data.repositories.RecipeRepository;
import com.ucas.firebaseminiproject.utilities.OnFirebaseLoadedListener;

import java.util.List;
import java.util.Map;

public class RecipeViewModel extends ViewModel {

    RecipeRepository repository = new RecipeRepository();
    public void createCategory(Map<String, String> category, OnCompleteListener<Void> listener){
        repository.createCategory(category, listener);
    }
    public void getAllCategories(OnFirebaseLoadedListener.OnCategoriesLoaded listener){
        repository.getAllCategories(listener);
    }

    public void createRecipe(List<String> categories, Recipe recipe, OnCompleteListener<Void> listener){
        repository.createRecipe(categories, recipe, listener);
    }

    public void editRecipe(List<String> oldCategories, List<String> categories, Recipe recipe, OnCompleteListener<Void> listener){
        repository.editRecipe(oldCategories, categories, recipe, listener);
    }

    public void getAllRecipes(OnFirebaseLoadedListener.OnRecipesLoaded listener) {
        repository.getAllRecipes(listener);
    }

    public void getRecipesByCategoryName(String categoryId, OnFirebaseLoadedListener.OnRecipesLoaded listener){
        repository.getRecipesByCategoryName(categoryId, listener);
    }

    public void getRecipeByRecipeId(String recipeId, String userId, OnFirebaseLoadedListener.OnRecipeLoaded listener){
        repository.getRecipeByRecipeId(recipeId, userId, listener);
    }

    public void deleteRecipe(String recipeId, OnCompleteListener<Void> listener){
        repository.deleteRecipe(recipeId, listener);
    }

}

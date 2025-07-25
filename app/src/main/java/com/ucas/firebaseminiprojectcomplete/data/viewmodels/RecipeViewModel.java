package com.ucas.firebaseminiprojectcomplete.data.viewmodels;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.ucas.firebaseminiprojectcomplete.data.models.Recipe;
import com.ucas.firebaseminiprojectcomplete.data.repositories.RecipeRepository;
import com.ucas.firebaseminiprojectcomplete.utilities.OnFirebaseLoadedListener;

import java.util.List;
import java.util.Map;

public class RecipeViewModel extends ViewModel {

    RecipeRepository repository = new RecipeRepository();
    public void createCategory(Map<String, String> category, OnCompleteListener<Void> listener){
        repository.createCategory(category, listener);
    }
    public void getAllCategoriesPaginated(OnFirebaseLoadedListener.OnCategoriesLoaded listener, boolean isInitial){
        repository.getAllCategoriesPaginated(listener,isInitial);
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

    public void getAllRecipesPaginated(OnFirebaseLoadedListener.OnRecipesLoaded listener, boolean isInitial){
        repository.getAllRecipesPaginated(listener, isInitial);
    }

    public void getRecipesByCategoryName(String categoryId, OnFirebaseLoadedListener.OnRecipesLoaded listener){
        repository.getRecipesByCategoryName(categoryId, listener);
    }

    public void getRecipeByRecipeId(String recipeId, OnFirebaseLoadedListener.OnRecipeLoaded listener){
        repository.getRecipeByRecipeId(recipeId, listener);
    }

    public void deleteRecipe(Recipe recipe, OnCompleteListener<Void> listener){
        repository.deleteRecipe(recipe, listener);
    }

    public void toggleLike(String recipeId, String userId, OnFirebaseLoadedListener.OnLikeLoadedListener listener){
        repository.toggleLike(recipeId, userId, listener);
    }

    public void checkLikeStatus(String recipeId, String userId, OnFirebaseLoadedListener.OnLikeLoadedListener listener){
        repository.checkLikeStatus(recipeId, userId, listener);
    }

}

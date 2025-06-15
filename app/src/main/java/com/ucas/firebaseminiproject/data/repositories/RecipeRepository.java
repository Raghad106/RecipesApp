package com.ucas.firebaseminiproject.data.repositories;

import static com.ucas.firebaseminiproject.utilities.Constance.CATEGORY_COLLECTION;
import static com.ucas.firebaseminiproject.utilities.Constance.CATEGORY_NAME;
import static com.ucas.firebaseminiproject.utilities.Constance.RECIPE_COLLECTION;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ucas.firebaseminiproject.data.models.Recipe;
import com.ucas.firebaseminiproject.utilities.OnFirebaseLoadedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeRepository {
    FirebaseFirestore firestore =FirebaseFirestore.getInstance();

    public void createCategory(Map<String, String> category, OnCompleteListener<Void> listener){
        // I use the name as document Id to grant the users will not create same category
        firestore.collection(CATEGORY_COLLECTION).document(category.get(CATEGORY_NAME).toLowerCase()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                if (!task.getResult().exists())
                    firestore.collection(CATEGORY_COLLECTION).document(category.get(CATEGORY_NAME).toLowerCase()).set(category).addOnCompleteListener(listener);
            }
        });
    }
    public void getAllCategories(OnFirebaseLoadedListener listener){
        // IF I return it directly it will bake null because firebase work in worker thread so it take time, that way I use listeners
        // Implement the listener's interface in fragment
        List<String> categories = new ArrayList<>();
        firestore.collection(CATEGORY_COLLECTION).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (DocumentSnapshot doc: task.getResult()) {
                    categories.add(doc.getId().toLowerCase());
                }
                listener.onCategoriesLoaded(categories);
            }else
                listener.onCategoriesLoaded(new ArrayList<>());
        });
    }

    //If I call OnCompleteListener inside loop that's mean it will return the result according to loops time
    public void myCreateRecipe(List<String> categories,Recipe recipe, OnCompleteListener<Void> listener){
        // Limit to at most 3 categories
        List<String> limitedCategories = categories.size() > 3
                ? categories.subList(0, 3)
                : categories;

        // Collect all tasks into a list
        List<Task<Void>> tasks = new ArrayList<>();

        for (String documentId : limitedCategories) {
            Task<Void> task = firestore.collection(CATEGORY_COLLECTION)
                    .document(documentId.toUpperCase())
                    .collection(RECIPE_COLLECTION)
                    .document()
                    .set(recipe);
            tasks.add(task); // Add each task to the list
        }

        // Wait until all tasks complete before calling your listener
        Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
            listener.onComplete(Tasks.forResult(null)); // Triggers listener with Void
        });
    }

    public void createRecipe(List<String> categories, Recipe recipe, OnCompleteListener<Void> listener) {
        // Limit to at most 3 categories
        List<String> limitedCategories = categories.size() > 3
                ? categories.subList(0, 3)
                : categories;

        // Collect all write tasks
        List<Task<Void>> tasks = new ArrayList<>();

        for (String categoryId : limitedCategories) {
            Task<Void> task = firestore.collection(CATEGORY_COLLECTION)
                    .document(categoryId.toLowerCase()) // Convert to uppercase if needed
                    .collection(RECIPE_COLLECTION)
                    .document() // Generate new document ID
                    .set(recipe); // Save recipe

            tasks.add(task);
        }

        // Wait for all tasks to complete
        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(task -> {
                    boolean allSuccessful = true;
                    for (Task<?> t : tasks) {
                        if (!t.isSuccessful()) {
                            allSuccessful = false;
                            break;
                        }
                    }

                    if (allSuccessful) {
                        listener.onComplete(Tasks.forResult(null)); // Success
                    } else {
                        // You can also pass a custom exception if needed
                        listener.onComplete(Tasks.forException(new Exception("One or more tasks failed.")));
                    }
                });
    }


    public void getAllRecipes(OnFirebaseLoadedListener listener){
        List<Recipe> recipes = new ArrayList<>();
        firestore.collection(CATEGORY_COLLECTION).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                List<Task<QuerySnapshot>> tasks = new ArrayList<>();
                for (DocumentSnapshot doc: task.getResult()){
                    String categoryId = doc.getId();
                    tasks.add(firestore.collection(CATEGORY_COLLECTION).document(categoryId).collection(RECIPE_COLLECTION).get());
                }
                Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
                    for (Object resultObj : results) {
                        QuerySnapshot snapshot = (QuerySnapshot) resultObj;
                        for (DocumentSnapshot doc : snapshot.getDocuments()) {
                            Recipe recipe = doc.toObject(Recipe.class);
                            if (recipe != null) {
                                recipes.add(recipe);
                            }
                        }
                    }
                    listener.onRecipeLoaded(recipes);
                }).addOnFailureListener(e -> {
                    listener.onRecipeLoaded(new ArrayList<>()); // Return empty list on failure
                });

            } else {
                listener.onRecipeLoaded(new ArrayList<>()); // Return empty list on failure
            }
        });
    }

    public void getRecipesByCategoryName(String categoryId,OnFirebaseLoadedListener listener){
        List<Recipe> recipes = new ArrayList<>();
        firestore.collection(CATEGORY_COLLECTION).document(categoryId.toUpperCase()).collection(RECIPE_COLLECTION).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (Recipe recipe: task.getResult().toObjects(Recipe.class)){
                    recipes.add(recipe);
                }
                listener.onRecipeLoaded(recipes);
            }
            else {
                listener.onRecipeLoaded(new ArrayList<>());
            }
        });
    }
}

package com.ucas.firebaseminiprojectcomplete.data.repositories;

import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.CATEGORY_COLLECTION;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.CATEGORY_NAME;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.CREATE_AT;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.IMAGE_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.LIKES_COLLECTION;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.NAME_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.RECIPES_COUNT;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.RECIPE_COLLECTION;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.RECIPE_ID;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.USERS_COLLECTION;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.ucas.firebaseminiprojectcomplete.data.models.Recipe;
import com.ucas.firebaseminiprojectcomplete.utilities.OnFirebaseLoadedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeRepository {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private static final int PAGE_SIZE = 10;
    private DocumentSnapshot lastCategoryVisible = null;
    private boolean isLastCategoryPage = false;

    private DocumentSnapshot lastRecipeVisible = null;
    private boolean isLastRecipePage = false;

    // Add an new category to firebase
    public void createCategory(Map<String, String> category, OnCompleteListener<Void> listener) {
        String categoryName = category.get(CATEGORY_NAME).toLowerCase();

        firestore.collection(CATEGORY_COLLECTION).document(categoryName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().exists()) {
                    // Category doesn't exist, so create it
                    firestore.collection(CATEGORY_COLLECTION).document(categoryName).set(category)
                            .addOnCompleteListener(listener);
                } else {
                    // Category already exists — still trigger the listener with a "success"
                    listener.onComplete(Tasks.forResult(null));
                }
            } else {
                // Forward the failure
                listener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    public void getAllCategories(OnFirebaseLoadedListener.OnCategoriesLoaded listener) {
        // IF I return it directly it will bake null because firebase work in worker thread so it take time, that way I use listeners
        // Implement the listener's interface in fragment
        List<String> categories = new ArrayList<>();
        firestore.collection(CATEGORY_COLLECTION).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot doc : task.getResult()) {
                    List<String> recipeIds = (List<String>) doc.get(RECIPE_ID);

                    if (recipeIds != null && !recipeIds.isEmpty()) {
                        categories.add(doc.getId().toLowerCase());
                    }else {
                        // To delete empty category because
                        firestore.collection(CATEGORY_COLLECTION).document(doc.getId()).delete().addOnCompleteListener(task1 -> {
                            if (task.isSuccessful()){
                                Log.d("CategoryCleanup", "Deleted empty category: " + doc.getId());
                            }
                        });
                    }
                }
                listener.onCategoriesLoaded(categories);
            } else
                listener.onCategoriesLoaded(new ArrayList<>());
        });
    }

    public void getAllCategoriesPaginated(OnFirebaseLoadedListener.OnCategoriesLoaded listener, boolean isInitial) {
        if (isLastCategoryPage) {
            listener.onCategoriesLoaded(new ArrayList<>()); // No more data
            return;
        }

        Query query = firestore.collection(CATEGORY_COLLECTION)
               // .orderBy(FieldPath.documentId()) // Or any field you want to sort by
                .limit(PAGE_SIZE);

        if (!isInitial && lastCategoryVisible != null) {
            query = query.startAfter(lastCategoryVisible);
        }

        query.get().addOnCompleteListener(task -> {
            List<String> categories = new ArrayList<>();

            if (task.isSuccessful() && task.getResult() != null) {
                List<DocumentSnapshot> docs = task.getResult().getDocuments();

                if (docs.isEmpty()) {
                    isLastCategoryPage = true;
                    listener.onCategoriesLoaded(categories);
                    return;
                }

                for (DocumentSnapshot doc : docs) {
                    List<String> recipeIds = (List<String>) doc.get(RECIPE_ID);

                    if (recipeIds != null && !recipeIds.isEmpty()) {
                        categories.add(doc.getId().toLowerCase());
                        Log.d("CAT_NUMBER", String.valueOf(categories.size()));
                    } else {
                        firestore.collection(CATEGORY_COLLECTION).document(doc.getId())
                                .delete()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d("CategoryCleanup", "Deleted empty category: " + doc.getId());
                                    }
                                });
                    }
                }

                // Save last visible document for next pagination call
                lastCategoryVisible = docs.get(docs.size() - 1);
                if (docs.size() < PAGE_SIZE) {
                    isLastCategoryPage = true;
                }

                listener.onCategoriesLoaded(categories);
            } else {
                listener.onCategoriesLoaded(new ArrayList<>());
            }
        });
    }

    public void getAllRecipesPaginated(OnFirebaseLoadedListener.OnRecipesLoaded listener, boolean isInitial) {
        List<Recipe> recipes = new ArrayList<>();
        if (isLastRecipePage) {
            listener.onRecipeLoaded(new ArrayList<>()); // No more data
            return;
        }

        Query query = firestore.collection(RECIPE_COLLECTION)
                .orderBy(CREATE_AT, Query.Direction.DESCENDING) // Or any field you want to sort by
                .limit(PAGE_SIZE);

        if (!isInitial && lastRecipeVisible != null) {
            query = query.startAfter(lastRecipeVisible);
        }

        query.get().addOnCompleteListener(task -> {
            List<Recipe> tempRecipes = new ArrayList<>();
            List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
            if (task.isSuccessful() && task.getResult()!=null){
                List<DocumentSnapshot> docs = task.getResult().getDocuments();
                Log.d("RECIPES_NUMBER", String.valueOf(docs.size()));
                if (docs.isEmpty()){
                    isLastRecipePage = true;
                    listener.onRecipeLoaded(new ArrayList<>());
                    return;
                }

                for (DocumentSnapshot doc: docs){
                    Recipe recipe = doc.toObject(Recipe.class);
                    if (recipe!= null){
                        tempRecipes.add(recipe);
                        Task<DocumentSnapshot> userTask = firestore.collection(USERS_COLLECTION)
                                .document(recipe.getPublisherId())
                                .get();
                        tasks.add(userTask);
                    }
                }
                Tasks.whenAllSuccess(tasks).addOnSuccessListener(results ->{
                    for (int i = 0; i < results.size(); i++){
                        DocumentSnapshot userDoc = (DocumentSnapshot) results.get(i);
                        Recipe recipe = tempRecipes.get(i);
                        recipe.setPublisherName(userDoc.getString(NAME_MAP_KEY));
                        recipe.setPublisherImage(userDoc.getString(IMAGE_MAP_KEY)); // ← you wrote setPublisherName again
                        recipes.add(recipe);
                    }
                    lastRecipeVisible = docs.get(docs.size() - 1);
                    if (docs.size() < PAGE_SIZE) {
                        isLastRecipePage = true;
                    }
                    listener.onRecipeLoaded(recipes);

                }).addOnFailureListener(e -> {
                    listener.onRecipeLoaded(new ArrayList<>());
                });

            } else {
                listener.onRecipeLoaded(new ArrayList<>());
            }
        });

    }




    public void createRecipe(List<String> categories, Recipe recipe, OnCompleteListener<Void> listener) {
        // Limit categories to max 3
        List<String> limitedCategories = categories.size() > 3
                ? categories.subList(0, 3)
                : categories;

        // Set initial values
        recipe.setLikesCount(0);

        // Generate and set a unique recipe ID
        String recipeId = firestore.collection(RECIPE_COLLECTION).document().getId();
        recipe.setRecipeId(recipeId);

        List<Task<Void>> tasks = new ArrayList<>();

        // Save recipe in global collection
        Task<Void> globalTask = firestore.collection(RECIPE_COLLECTION)
                .document(recipeId)
                .set(recipe);
        tasks.add(globalTask);

        Map<String, Object> update = new HashMap<>();
        update.put(CREATE_AT, FieldValue.serverTimestamp());


        Task<Void> timeTask = firestore.collection(RECIPE_COLLECTION)
                .document(recipeId)
                .update(update);
        tasks.add(timeTask);

        // Save recipeId in each category's recipeIds array
        for (String categoryId : limitedCategories) {
            Map<String, Object> data = new HashMap<>();
            data.put(RECIPE_ID, FieldValue.arrayUnion(recipeId));

            Task<Void> categoryTask = firestore.collection(CATEGORY_COLLECTION)
                    .document(categoryId.toLowerCase())
                    .set(data, SetOptions.merge());  // Safe even if doc doesn't exist
            tasks.add(categoryTask);
        }

        // Wait for all tasks to complete
        Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
            boolean allSuccessful = true;
            for (Task<?> t : tasks) {
                if (!t.isSuccessful()) {
                    allSuccessful = false;
                    break;
                }
            }

            if (allSuccessful){
                firestore.collection(USERS_COLLECTION)
                        .document(recipe.getPublisherId())
                        .update(RECIPES_COUNT, FieldValue.increment(1));
                listener.onComplete(Tasks.forResult(null));
            } else {
                listener.onComplete(Tasks.forException(new Exception("One or more tasks failed.")));
            }
        });
    }

    public void editRecipe(List<String> oldCategories, List<String> categories, Recipe recipe, OnCompleteListener<Void> listener) {
        List<String> limitedCategories = categories.size() > 3
                ? categories.subList(0, 3)
                : categories;
        List<Task<Void>> tasks = new ArrayList<>();
        Task<Void> globalTask = firestore.collection(RECIPE_COLLECTION)
                .document(recipe.getRecipeId())
                .set(recipe, SetOptions.merge());
        tasks.add(globalTask);

        for (String categoryId : limitedCategories) {
            Map<String, Object> data = new HashMap<>();
            data.put(RECIPE_ID, FieldValue.arrayUnion(recipe.getRecipeId()));

            Task<Void> categoryTask = firestore.collection(CATEGORY_COLLECTION)
                    .document(categoryId.toLowerCase())
                    .set(data, SetOptions.merge());  // Safe even if doc doesn't exist
            tasks.add(categoryTask);
        }

        // Remove recipeId from removed categories
        for (String oldCat : oldCategories) {
            if (!limitedCategories.contains(oldCat)) {
                Map<String, Object> removeData = new HashMap<>();
                removeData.put(RECIPE_ID, FieldValue.arrayRemove(recipe.getRecipeId()));

                Task<Void> removeTask = firestore.collection(CATEGORY_COLLECTION)
                        .document(oldCat.toLowerCase())
                        .set(removeData, SetOptions.merge());
                tasks.add(removeTask);
            }
        }

        // Wait for all tasks to complete
        Tasks.whenAllComplete(tasks).addOnCompleteListener(task -> {
            boolean allSuccessful = true;
            for (Task<?> t : tasks) {
                if (!t.isSuccessful()) {
                    allSuccessful = false;
                    break;
                }
            }

            if (allSuccessful) {
                listener.onComplete(Tasks.forResult(null));
            } else {
                listener.onComplete(Tasks.forException(new Exception("One or more tasks failed.")));
            }
        });
    }

    public void getAllRecipes(OnFirebaseLoadedListener.OnRecipesLoaded listener) {
        List<Recipe> recipes = new ArrayList<>();

        firestore.collection(RECIPE_COLLECTION)
                .orderBy(CREATE_AT, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Task<DocumentSnapshot>> userTasks = new ArrayList<>();
                        List<Recipe> tempRecipes = new ArrayList<>();

                        for (DocumentSnapshot doc : task.getResult()) {
                            Recipe recipe = doc.toObject(Recipe.class);
                            if (recipe != null) {
                                tempRecipes.add(recipe);
                                Task<DocumentSnapshot> userTask = firestore.collection(USERS_COLLECTION)
                                        .document(recipe.getPublisherId())
                                        .get();
                                userTasks.add(userTask);
                            }
                        }

                        Tasks.whenAllSuccess(userTasks).addOnSuccessListener(results -> {
                            for (int i = 0; i < results.size(); i++) {
                                DocumentSnapshot userDoc = (DocumentSnapshot) results.get(i);
                                Recipe recipe = tempRecipes.get(i);
                                recipe.setPublisherName(userDoc.getString(NAME_MAP_KEY));
                                recipe.setPublisherImage(userDoc.getString(IMAGE_MAP_KEY)); // ← you wrote setPublisherName again
                                recipes.add(recipe);
                            }
                            listener.onRecipeLoaded(recipes);

                        }).addOnFailureListener(e -> {
                            listener.onRecipeLoaded(new ArrayList<>());
                        });

                    } else {
                        listener.onRecipeLoaded(new ArrayList<>());
                    }
                });
    }

    // Tab case
    public void getRecipesByCategoryName(String categoryId, OnFirebaseLoadedListener.OnRecipesLoaded listener){
        List<Recipe> recipes = new ArrayList<>();
        firestore.collection(CATEGORY_COLLECTION).document(categoryId.toLowerCase()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                List<String> recipeIds = (List<String>) task.getResult().get(RECIPE_ID);

                if (recipeIds == null || recipeIds.isEmpty()) {
                    listener.onRecipeLoaded(new ArrayList<>());
                    return;
                }
                List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                List<Recipe> tempRecipes = new ArrayList<>();
                for (String recipeId : recipeIds) {
                    tasks.add(firestore.collection(RECIPE_COLLECTION).document(recipeId).get());
                }
                Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
                    List<Task<DocumentSnapshot>> userTasks = new ArrayList<>();
                    for (Object result : results) {
                        DocumentSnapshot doc = (DocumentSnapshot) result;
                        Recipe recipe = doc.toObject(Recipe.class);
                        if (recipe != null) {
                            tempRecipes.add(recipe);

                            Task<DocumentSnapshot> oneUerTask = firestore.collection(USERS_COLLECTION).document(recipe.getPublisherId()).get();
                            userTasks.add(oneUerTask);
                        }
                    }
                    tempRecipes.sort((r1, r2) -> {
                        if (r1.getCreateAt() == null || r2.getCreateAt() == null) return 0;
                        return r2.getCreateAt().compareTo(r1.getCreateAt());
                    });
                    Tasks.whenAllSuccess(userTasks).addOnSuccessListener(userResults -> {
                        // Build a map from userId → user document
                        Map<String, DocumentSnapshot> userMap = new HashMap<>();
                        for (Object userObj : userResults) {
                            DocumentSnapshot userDoc = (DocumentSnapshot) userObj;
                            userMap.put(userDoc.getId(), userDoc);
                        }

                        for (Recipe recipe : tempRecipes) {
                            DocumentSnapshot userDoc = userMap.get(recipe.getPublisherId());
                            if (userDoc != null) {
                                recipe.setPublisherName(userDoc.getString(NAME_MAP_KEY));
                                recipe.setPublisherImage(userDoc.getString(IMAGE_MAP_KEY));
                            }
                            recipes.add(recipe);
                        }

                        listener.onRecipeLoaded(recipes);
                    });

                }).addOnFailureListener(e -> {
                    listener.onRecipeLoaded(new ArrayList<>());
                    Log.e("Firestore", "Error fetching recipes by category", e);
                });
            }
            else {
                listener.onRecipeLoaded(new ArrayList<>());
            }
        });
    }

    // Recipe's details
    public void getRecipeByRecipeId(String recipeId, OnFirebaseLoadedListener.OnRecipeLoaded listener){
        firestore.collection(RECIPE_COLLECTION).document(recipeId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Recipe recipe = task.getResult().toObject(Recipe.class);
                String publisherId = recipe.getPublisherId();
                firestore.collection(USERS_COLLECTION).document(publisherId).get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        if (recipe != null){
                            recipe.setPublisherName(task1.getResult().getString(NAME_MAP_KEY));
                            recipe.setPublisherImage(task1.getResult().getString(IMAGE_MAP_KEY));
                            listener.onRecipeLoaded(recipe);
                        }
                    }
                });
            }
        });
    }

    public void deleteRecipe(Recipe recipe, OnCompleteListener<Void> listener) {
        if (recipe.getRecipeId() == null || recipe.getRecipeId().isEmpty()) {
            return;
        }

        List<Task<Void>> updateTasks = new ArrayList<>();

        for (String category : recipe.getCategories()) {
            DocumentReference categoryRef = firestore.collection(CATEGORY_COLLECTION).document(category.toLowerCase());

            // Remove the recipe ID from the category document
            Task<Void> updateTask = categoryRef.update(RECIPE_ID, FieldValue.arrayRemove(recipe.getRecipeId()));
            updateTasks.add(updateTask);
        }

        // After removing from all categories, delete the recipe itself
        Tasks.whenAll(updateTasks).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firestore.collection(RECIPE_COLLECTION)
                        .document(recipe.getRecipeId())
                        .delete()
                        .addOnCompleteListener(task1 -> {
                            firestore.collection(USERS_COLLECTION)
                                    .document(recipe.getPublisherId())
                                    .update(RECIPES_COUNT, FieldValue.increment(-1));
                            listener.onComplete(task1);
                        });
            } else {
                listener.onComplete(Tasks.forException(new Exception("Failed to remove recipe from categories.")));
            }
        });
    }



    public void toggleLike(String recipeId, String userId, OnFirebaseLoadedListener.OnLikeLoadedListener listener) {
        DocumentReference likeDocRef = firestore
                .collection(RECIPE_COLLECTION)
                .document(recipeId)
                .collection(LIKES_COLLECTION)
                .document(userId);

        DocumentReference recipeRef = firestore
                .collection(RECIPE_COLLECTION)
                .document(recipeId);

        likeDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean isLiked;

                if (task.getResult().exists()) {
                    // User has already liked → remove like
                    likeDocRef.delete();
                    recipeRef.update("likesCount", FieldValue.increment(-1));
                    isLiked = false;
                } else {
                    // User has not liked → add like
                    Map<String, Object> data = new HashMap<>();
                    data.put("userId", userId);
                    //data.put("likedAt", FieldValue.serverTimestamp()); // optional

                    likeDocRef.set(data);
                    recipeRef.update("likesCount", FieldValue.increment(1));
                    isLiked = true;
                }

                // Fetch updated count
                recipeRef.get().addOnSuccessListener(recipeSnapshot -> {
                    long count = recipeSnapshot.contains("likesCount")
                            ? recipeSnapshot.getLong("likesCount")
                            : 0;
                    listener.onLikeLoadedListener(isLiked, count);
                });

            } else {
                listener.onLikeLoadedListener(false, -1); // error case
                Log.e("myToggleLike", "Failed to read like document: " + task.getException());
            }
        }).addOnFailureListener(e -> {
            listener.onLikeLoadedListener(false, -1);
            Log.e("myToggleLike", "Error toggling like: " + e.getMessage());
        });
    }

    public void checkLikeStatus(String recipeId, String userId, OnFirebaseLoadedListener.OnLikeLoadedListener listener) {
        DocumentReference recipeRef = firestore.collection(RECIPE_COLLECTION).document(recipeId);
        DocumentReference likeDocRef = recipeRef.collection(LIKES_COLLECTION).document(userId);

        Log.d("checkLikeStatus", "Checking like status for userId: " + userId + ", recipeId: " + recipeId);

        likeDocRef.get().addOnCompleteListener(task -> {
            boolean isLiked = task.isSuccessful() && task.getResult().exists();
            Log.d("checkLikeStatus", "Like doc exists: " + isLiked);

            recipeRef.get().addOnSuccessListener(snapshot -> {
                long count = snapshot.contains("likesCount") ? snapshot.getLong("likesCount") : 0;
                Log.d("checkLikeStatus", "likesCount = " + count);
                listener.onLikeLoadedListener(isLiked, count);
            }).addOnFailureListener(e -> {
                Log.e("checkLikeStatus", "Error getting recipe document", e);
                listener.onLikeLoadedListener(isLiked, 0);
            });
        });
    }

}


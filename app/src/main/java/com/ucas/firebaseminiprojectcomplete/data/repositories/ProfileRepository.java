package com.ucas.firebaseminiprojectcomplete.data.repositories;

import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.COUNTRY_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.EMAIL_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.ID_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.IMAGE_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.NAME_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.RECIPES_COUNT;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.RECIPE_COLLECTION;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.SAVED_RECIPES_COLLECTION;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.USERS_COLLECTION;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.ucas.firebaseminiprojectcomplete.data.models.Recipe;
import com.ucas.firebaseminiprojectcomplete.utilities.OnFirebaseLoadedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileRepository {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth =FirebaseAuth.getInstance();
    RecipeRepository recipeRepository = new RecipeRepository();

    // My recipes case
    public void getRecipesByUserId(String userId, OnFirebaseLoadedListener.OnRecipesLoaded loaded){
        if (userId != null && !userId.isEmpty()){
            this.getUserInfoById(userId, userInfo -> {
                firestore.collection(RECIPE_COLLECTION).whereEqualTo("publisherId", userId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<Recipe> userRecipes = task.getResult().toObjects(Recipe.class);
                            for (Recipe recipe: userRecipes){
                                recipe.setPublisherImage(userInfo.get(IMAGE_MAP_KEY));
                                recipe.setPublisherName(userInfo.get(NAME_MAP_KEY));
                            }
                            loaded.onRecipeLoaded(userRecipes);
                        } else {
                            loaded.onRecipeLoaded(Collections.emptyList()); // return empty list on failure
                        }
                    }
                });
            });
        }
    }

    public void getCurrentUserInfo(OnFirebaseLoadedListener.OnUserInfoLoadedListener listener) {
        FirebaseUser user = auth.getCurrentUser();
        Map<String, String> userInfo = new HashMap<>();

        if (user != null) {
            firestore.collection(USERS_COLLECTION).document(user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            userInfo.put(ID_MAP_KEY, user.getUid());
                            String name = task.getResult().getString(NAME_MAP_KEY);
                            String userImage = task.getResult().getString(IMAGE_MAP_KEY);
                            String email = task.getResult().getString(EMAIL_MAP_KEY);
                            String country = task.getResult().getString(COUNTRY_MAP_KEY);
                            userInfo.put(NAME_MAP_KEY, name != null ? name : "Unknown");
                            userInfo.put(IMAGE_MAP_KEY, userImage != null ? userImage : "");
                            userInfo.put(EMAIL_MAP_KEY, email != null ? email : "");
                            userInfo.put(COUNTRY_MAP_KEY, country != null ? country : "");
                            listener.onUserInfoLoaded(userInfo);
                        }
                    });
        }
    }

    public void getUserInfoById(String userId, OnFirebaseLoadedListener.OnUserInfoLoadedListener listener) {
        Map<String, String> userInfo = new HashMap<>();

        if (userId != null && !userId.isEmpty()) {
            firestore.collection(USERS_COLLECTION).document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            userInfo.put(ID_MAP_KEY, userId);
                            String name = task.getResult().getString(NAME_MAP_KEY);
                            String email = task.getResult().getString(EMAIL_MAP_KEY);
                            String country = task.getResult().getString(COUNTRY_MAP_KEY);
                            String userImage = task.getResult().getString(IMAGE_MAP_KEY);
                            userInfo.put(NAME_MAP_KEY, name != null ? name : "Unknown");
                            userInfo.put(EMAIL_MAP_KEY, email != null ? email : "");
                            userInfo.put(COUNTRY_MAP_KEY, country != null ? country : "");
                            userInfo.put(IMAGE_MAP_KEY, userImage != null ? userImage : "");
                            listener.onUserInfoLoaded(userInfo);
                        } else {
                            listener.onUserInfoLoaded(new HashMap<>()); // Return empty map on failure
                        }
                    });
        } else {
            listener.onUserInfoLoaded(new HashMap<>()); // Return empty map if userId is invalid
        }
    }

    public void updateUserInfo(String userId, Map<String, String> userInfo, OnCompleteListener<Void> listener){
        if (userInfo != null && !userInfo.isEmpty())
            firestore.collection(USERS_COLLECTION).document(userId).set(userInfo, SetOptions.merge()).addOnCompleteListener(listener);
    }

    public void toggleSaveRecipe(String recipeId, OnFirebaseLoadedListener.OnSaveRecipeLoadedListener listener) {
        getCurrentUserInfo(userInfo -> {
            DocumentReference savedRecipeDocument = firestore
                    .collection(USERS_COLLECTION)
                    .document(userInfo.get(ID_MAP_KEY))
                    .collection(SAVED_RECIPES_COLLECTION)
                    .document(recipeId);

            savedRecipeDocument.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (task.getResult().exists()) {
                        // Already saved → unsave it
                        savedRecipeDocument.delete().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                listener.onSaveRecipeLoadedListener(false);
                            }
                        });
                    } else {
                        // Not saved → save it
                        Map<String, Object> data = new HashMap<>();
                        data.put("recipeId", recipeId);
                        data.put("savedAt", FieldValue.serverTimestamp()); // optional
                        savedRecipeDocument.set(data).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                listener.onSaveRecipeLoadedListener(true);
                            }
                        });
                    }
                } else {
                    // Handle failure (optional)
                    listener.onSaveRecipeLoadedListener(false);
                }
            });
        });
    }

    public void checkIsSaved(String recipeId, OnFirebaseLoadedListener.OnSaveRecipeLoadedListener listener) {
        getCurrentUserInfo(userInfo -> {
            DocumentReference savedRecipeDocument = firestore
                    .collection(USERS_COLLECTION)
                    .document(userInfo.get(ID_MAP_KEY))
                    .collection(SAVED_RECIPES_COLLECTION)
                    .document(recipeId);

            savedRecipeDocument.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (task.getResult().exists()) {
                        listener.onSaveRecipeLoadedListener(true);
                    } else {
                        listener.onSaveRecipeLoadedListener(false);
                    }
                } else {
                    // Handle failure (optional)
                    listener.onSaveRecipeLoadedListener(false);
                }
            });
        });
    }

    public void getSavedRecipes(OnFirebaseLoadedListener.OnRecipesLoaded listener) {
        getCurrentUserInfo(userInfo -> {
            List<Recipe> recipes = new ArrayList<>();
            String userId = userInfo.get(ID_MAP_KEY);

            if (userId == null) {
                listener.onRecipeLoaded(new ArrayList<>());
                return;
            }

            firestore.collection(USERS_COLLECTION)
                    .document(userId)
                    .collection(SAVED_RECIPES_COLLECTION)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful() || task.getResult().isEmpty()) {
                            listener.onRecipeLoaded(new ArrayList<>());
                            return;
                        }

                        List<DocumentSnapshot> savedDocs = task.getResult().getDocuments();
                        List<String> recipeIds = new ArrayList<>();
                        for (DocumentSnapshot doc : savedDocs) {
                            String recipeId = doc.getString("recipeId");
                            if (recipeId != null) recipeIds.add(recipeId);
                        }

                        if (recipeIds.isEmpty()) {
                            listener.onRecipeLoaded(new ArrayList<>());
                            return;
                        }

                        // Fetch all recipes one by one
                        for (String recipeId : recipeIds) {
                            recipeRepository.getRecipeByRecipeId(recipeId, recipe -> {
                                if (recipe != null) recipes.add(recipe);

                                // Once all recipes are loaded, return them
                                if (recipes.size() == recipeIds.size()) {
                                    listener.onRecipeLoaded(recipes);
                                }
                            });
                        }
                    });
        });
    }

    public void getTopCreators(int minCount, OnFirebaseLoadedListener.OnUsersInfoLoadedListener listener) {
        firestore.collection(USERS_COLLECTION)
                .whereGreaterThanOrEqualTo(RECIPES_COUNT, minCount)
                .orderBy(RECIPES_COUNT, Query.Direction.DESCENDING)
                .limit(10) // LIMIT TO 3 USERS ONLY
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Map<String, String>> topUsers = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                            Map<String, String> user = new HashMap<>();
                            user.put(ID_MAP_KEY, doc.getId());
                            user.put(NAME_MAP_KEY, doc.getString(NAME_MAP_KEY));
                            user.put(IMAGE_MAP_KEY, doc.getString(IMAGE_MAP_KEY));
                            user.put(RECIPES_COUNT, String.valueOf(doc.getLong(RECIPES_COUNT)));
                            topUsers.add(user);
                        }
                        listener.onUsersInfoLoaded(topUsers);
                    } else {
                        listener.onUsersInfoLoaded(new ArrayList<>());
                    }
                });
    }

}

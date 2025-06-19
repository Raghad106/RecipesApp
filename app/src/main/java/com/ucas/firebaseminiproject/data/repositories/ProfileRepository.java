package com.ucas.firebaseminiproject.data.repositories;

import static com.ucas.firebaseminiproject.utilities.Constance.COUNTRY_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.EMAIL_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.ID_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.IMAGE_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.NAME_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.RECIPE_COLLECTION;
import static com.ucas.firebaseminiproject.utilities.Constance.USERS_COLLECTION;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.ucas.firebaseminiproject.data.models.Recipe;
import com.ucas.firebaseminiproject.utilities.OnFirebaseLoadedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileRepository {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    FirebaseAuth auth =FirebaseAuth.getInstance();

    public void getRecipesByUserId(OnFirebaseLoadedListener.OnRecipesLoaded loaded){
        this.getCurrentUserInfo(userInfo -> {
            if (!userInfo.isEmpty())
                firestore.collection(RECIPE_COLLECTION).whereEqualTo("publisherId", userInfo.get(ID_MAP_KEY)).get().addOnCompleteListener(task -> {
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
                            String userImage = task.getResult().getString(IMAGE_MAP_KEY);
                            userInfo.put(NAME_MAP_KEY, name != null ? name : "Unknown");
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

    public void updateUserInf(String userId, Map<String, String> userInfo, OnCompleteListener<Void> listener){
        if (userInfo != null && !userInfo.isEmpty())
            firestore.collection(USERS_COLLECTION).document(userId).set(userInfo, SetOptions.merge()).addOnCompleteListener(listener);
    }
}

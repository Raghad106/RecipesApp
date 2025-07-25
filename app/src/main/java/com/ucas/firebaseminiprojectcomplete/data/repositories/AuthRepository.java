package com.ucas.firebaseminiprojectcomplete.data.repositories;

import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.EMAIL_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.IS_REMEMBERED_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.PASSWORD_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.SHARED_PREFERENCES_NAME;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.USERS_COLLECTION;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AuthRepository {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    // for adding a new user
    public void register(Map<String, Object> userData, OnCompleteListener<Void> listener) {
        String email = (String) userData.get(EMAIL_MAP_KEY);
        String password = (String) userData.get(PASSWORD_MAP_KEY);
        if (email == null || password == null){
            listener.onComplete(Tasks.forException(new IllegalArgumentException("Email or password is missing")));
            return;
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                listener.onComplete(Tasks.forException(task.getException()));
                return;
            }

            String uid = auth.getUid();
            if (uid == null) {
                listener.onComplete(Tasks.forException(new Exception("User ID is null")));
                return;
            }

            // for saving user's private information
            userData.remove(PASSWORD_MAP_KEY);
            // Save user data to Firestore
            firestore.collection(USERS_COLLECTION).document(uid).set(userData).addOnCompleteListener(storeTask -> {
                if (!storeTask.isSuccessful()) {
                    listener.onComplete(Tasks.forException(storeTask.getException()));
                    return;
                }

                FirebaseUser user = auth.getCurrentUser();
                if (user != null && !user.isEmailVerified()) {
                    user.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                        if (verifyTask.isSuccessful()) {
                            auth.signOut(); // Prevent access until verified
                            listener.onComplete(Tasks.forResult(null)); // Success
                        } else {
                            // Failed to send verification email
                            listener.onComplete(Tasks.forException(verifyTask.getException()));
                        }
                    });
                } else {
                    // Shouldn't happen, but safe fallback
                    listener.onComplete(Tasks.forException(new Exception("User is null or already verified")));
                }
            });
        });

    }

    // for entering ti app
    public void login(String email, String password, OnCompleteListener<FirebaseUser> listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        listener.onComplete(Tasks.forException(task.getException()));
                        return;
                    }

                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null && user.isEmailVerified()) {
                        listener.onComplete(Tasks.forResult(user));
                    } else {
                        auth.signOut(); // Prevent unverified users from staying logged in
                        listener.onComplete(Tasks.forException(new Exception("Please verify your email before logging in.")));
                    }
                });
    }

    public void signOut(){
        auth.signOut();
    }

    // Remember me case (change value in login activity)
    public void saveRememberInfo(Context context, Boolean isRememberMe) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(IS_REMEMBERED_KEY, isRememberMe);
        editor.apply();
    }

    // Remember me case (get value in splash screen)
    public boolean getRememberInfo(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(IS_REMEMBERED_KEY, false);
    }


}

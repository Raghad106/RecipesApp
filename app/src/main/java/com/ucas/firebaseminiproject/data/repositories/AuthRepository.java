package com.ucas.firebaseminiproject.data.repositories;

import static com.ucas.firebaseminiproject.utilities.Constance.EMAIL_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.PASSWORD_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.USERS_COLLECTION;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AuthRepository {
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();


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

            userData.remove(PASSWORD_MAP_KEY);
            firestore.collection(USERS_COLLECTION).document(uid).set(userData).addOnCompleteListener(listener);
        });
    }

    public void login(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }
}

package com.ucas.firebaseminiproject.utilities;

import android.Manifest;
import android.os.Build;

public interface Constance {
    // Shared preferences
    String FIRST_RUN_KEY = "first_run";
    String IS_REMEMBERED_KEY = "is Remember";
    String SHARED_PREFERENCES_NAME ="remember me storage";

    // Collections Name
    String USERS_COLLECTION ="Users";
    String CATEGORY_COLLECTION ="Categories";
    String RECIPE_COLLECTION ="Recipes";

    // User Document's filed
    String EMAIL_MAP_KEY ="email";
    String PASSWORD_MAP_KEY ="password";
    String NAME_MAP_KEY ="name";
    String COUNTRY_MAP_KEY ="country";
    String LINK_MAP_KEY ="link";
    String IMAGE_MAP_KEY ="image";

    String CATEGORY_NAME = "Cat Name";

    // Fragment Tags
    String ADD_RECIPE_TAG = "new recipe";

    // Method Tags
    String ADD_CATEGORY_TAG = "add category";
    String REMOVE_CATEGORY_TAG = "remove category";

    String CHANNEL_ID = "lesson_channel";
    String STUDENT_ID_KEY ="student id";
    String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    String STORAGE_PERMISSION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? Manifest.permission.READ_MEDIA_IMAGES
            : Manifest.permission.READ_EXTERNAL_STORAGE;
    String POST_NOTIFICATIONS_PERMISSION = Manifest.permission.POST_NOTIFICATIONS;

}

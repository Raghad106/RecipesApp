package com.ucas.firebaseminiproject.utilities;

import android.Manifest;
import android.os.Build;

public interface Constance {
    String FIRST_RUN_KEY = "first_run";
    String IS_REMEMBERED_KEY = "is Remember";
    String SHARED_PREFERENCES_NAME ="remember me storage";
    String USERS_COLLECTION ="user collection";
    String EMAIL_MAP_KEY ="email";
    String PASSWORD_MAP_KEY ="password";
    String NAME_MAP_KEY ="name";
    String COUNTRY_MAP_KEY ="country";
    String LINK_MAP_KEY ="link";
    String IMAGE_MAP_KEY ="image";
    String CHANNEL_ID = "lesson_channel";
    String STUDENT_ID_KEY ="student id";
    String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    String STORAGE_PERMISSION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? Manifest.permission.READ_MEDIA_IMAGES
            : Manifest.permission.READ_EXTERNAL_STORAGE;
    String POST_NOTIFICATIONS_PERMISSION = Manifest.permission.POST_NOTIFICATIONS;

}

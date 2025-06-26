package com.ucas.firebaseminiproject.utilities;

public interface Constance {
    // Shared preferences
    String IS_REMEMBERED_KEY = "is Remember";
    String SHARED_PREFERENCES_NAME ="remember me storage";

    // Collections Name
    String USERS_COLLECTION ="Users";
    String CATEGORY_COLLECTION ="Categories";
    String RECIPE_COLLECTION ="Recipes";
    String LIKES_COLLECTION ="Likes";
    String SAVED_RECIPES_COLLECTION ="Saved Recipes";

    // User Document's filed
    String ID_MAP_KEY ="uid";
    String EMAIL_MAP_KEY ="email";
    String PASSWORD_MAP_KEY ="password";
    String NAME_MAP_KEY ="name";
    String COUNTRY_MAP_KEY ="country";
    String IMAGE_MAP_KEY ="image";

    String CATEGORY_NAME = "Cat Name";
    String RECIPE_ID = "Recipe Ids";
    String CREATE_AT = "createAt";
    String RECIPES_COUNT = "Recipes count";

    // Fragment Tags
    String ADD_RECIPE_TAG = "New Recipe Fragment";
    String EDIT_RECIPE_TAG = "Edit a Recipe Fragment";
    String HOME_TAG = "Home Fragment";
    String SAVED_RECIPE_TAG = "My recipe Fragment";
    String DIALOG_LOGOUT_TAG = "Dialog Logout Fragment";
    String DIALOG_RECIPE_TAG = "Dialog Recipe Fragment";
    String CURRENT_USER_TAG = "My Profile Fragment";
    String USER_TAG = "Users Profile Fragment";



}

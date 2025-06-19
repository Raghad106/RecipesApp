package com.ucas.firebaseminiproject.ui;

import static com.ucas.firebaseminiproject.utilities.Constance.ADD_RECIPE_TAG;
import static com.ucas.firebaseminiproject.utilities.Constance.DIALOG_LOGOUT_TAG;
import static com.ucas.firebaseminiproject.utilities.Constance.DIALOG_RECIPE_TAG;
import static com.ucas.firebaseminiproject.utilities.Constance.EDIT_RECIPE_TAG;
import static com.ucas.firebaseminiproject.utilities.Constance.HOME_TAG;
import static com.ucas.firebaseminiproject.utilities.Constance.MY_RECIPE_TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.ucas.firebaseminiproject.R;
import com.ucas.firebaseminiproject.data.models.Recipe;
import com.ucas.firebaseminiproject.databinding.ActivityMainBinding;
import com.ucas.firebaseminiproject.utilities.OnItemListener;

public class MainActivity extends AppCompatActivity implements OnItemListener.OnFragmentListener, OnItemListener.OnIntentListener {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //show Home fragment bu default
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentsContainer, new HomeFragment())
                .addToBackStack(null)
                .commit();

        binding.bottomNavigation.setOnItemSelectedListener( item ->{
            if (item.getItemId() == R.id.home) {
                navigateFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.account) {
                navigateFragment(new ProfileFragment());
            } else if (item.getItemId() == R.id.myRecipe) {
                navigateFragment(RecyclerViewFragment.newInstance(MY_RECIPE_TAG));
            }
            return true;
        });

    }

    private void navigateFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentsContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onNavigateFragments(String tag) {
        if (tag.equals(ADD_RECIPE_TAG))
            navigateFragment(AddAndEditRecipeFragment.newInstance(ADD_RECIPE_TAG));
        if (tag.equals(HOME_TAG))
            navigateFragment(new HomeFragment());
        if (tag.equals(DIALOG_LOGOUT_TAG)){
            DialogFragment dialog = ItemDialogFragment.newInstance("Are you sure, do you want logout", DIALOG_LOGOUT_TAG);
            dialog.show(getSupportFragmentManager(), DIALOG_LOGOUT_TAG);
        }
    }

    @Override
    public void onNavigateRecipeDetailsFragment(String recipeId, String publisherId) {
        if (recipeId != null && publisherId != null){
            navigateFragment(RecipeDetailsFragment.newInstance(recipeId, publisherId));
        }
    }

    @Override
    public void onDeleteRecipe(String tag, String recipeId) {
        if (tag.equals(DIALOG_RECIPE_TAG) && recipeId != null && !recipeId.isEmpty()){
            DialogFragment dialog = ItemDialogFragment.newInstance("Are you sure, do you want delete this recipe", DIALOG_RECIPE_TAG, recipeId);
            dialog.show(getSupportFragmentManager(), DIALOG_RECIPE_TAG);
        }
    }

    @Override
    public void onNavigateEditRecipeDetailsFragment(String tag, Recipe recipe) {
        if (tag.equals(EDIT_RECIPE_TAG))
            navigateFragment(AddAndEditRecipeFragment.newInstance(tag, recipe));
    }

    @Override
    public void onIntentListener(String tag) {
        if (tag.equals(DIALOG_LOGOUT_TAG)){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else if (tag.equals(DIALOG_RECIPE_TAG))
            navigateFragment(new HomeFragment());
    }

}
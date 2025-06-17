package com.ucas.firebaseminiproject.ui;

import static android.view.View.GONE;
import static com.ucas.firebaseminiproject.utilities.Constance.ADD_RECIPE_TAG;
import static com.ucas.firebaseminiproject.utilities.Constance.HOME_TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ucas.firebaseminiproject.R;
import com.ucas.firebaseminiproject.databinding.ActivityMainBinding;
import com.ucas.firebaseminiproject.utilities.OnItemListener;

public class MainActivity extends AppCompatActivity implements OnItemListener.OnFragmentListener {
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
            } else if (item.getItemId() == R.id.myRecipe) {
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
            navigateFragment(new AddRecipeFragment());
        if (tag.equals(HOME_TAG))
            navigateFragment(new HomeFragment());
    }

    @Override
    public void onNavigateRecipeDetailsFragment(String recipeId, String publisherId) {
        if (recipeId != null && publisherId != null){
            navigateFragment(RecipeDetailsFragment.newInstance(recipeId, publisherId));
        }
    }
}
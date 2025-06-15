package com.ucas.firebaseminiproject.ui;

import static com.ucas.firebaseminiproject.utilities.Constance.ADD_RECIPE_TAG;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.ucas.firebaseminiproject.R;
import com.ucas.firebaseminiproject.databinding.ActivityMainBinding;
import com.ucas.firebaseminiproject.utilities.OnRecipeListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnRecipeListener {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

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
    public boolean onLikeClicked(String recipeId, String userId) {
        return false;
    }

    @Override
    public boolean onSaveClicked(String recipeId, String userId) {
        return false;
    }



    @Override
    public void onNavigateFragment(String tag) {
        if (tag.equals(ADD_RECIPE_TAG))
            navigateFragment(new AddRecipeFragment());
    }
}
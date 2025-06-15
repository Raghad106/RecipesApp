package com.ucas.firebaseminiproject.ui;

import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

import static com.ucas.firebaseminiproject.utilities.Constance.CATEGORY_NAME;
import static com.ucas.firebaseminiproject.utilities.Constance.NAME_MAP_KEY;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ucas.firebaseminiproject.data.models.Recipe;
import com.ucas.firebaseminiproject.data.viewmodels.AuthViewModel;
import com.ucas.firebaseminiproject.data.viewmodels.RecipeViewModel;
import com.ucas.firebaseminiproject.databinding.FragmentAddRecipeBinding;
import com.ucas.firebaseminiproject.ui.adapters.CategoryAdapter;
import com.ucas.firebaseminiproject.utilities.OnFirebaseLoadedListener;
import com.ucas.firebaseminiproject.utilities.OnRecipeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRecipeFragment extends Fragment implements OnRecipeListener.OnCategoryListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    List<String> selectedCategories = new ArrayList<>();
    CategoryAdapter adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecipeViewModel recipeViewModel;
    private AuthViewModel authViewModel;
    private FragmentAddRecipeBinding binding;

    public AddRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddRecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddRecipeFragment newInstance(String param1, String param2) {
        AddRecipeFragment fragment = new AddRecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddRecipeBinding.inflate(inflater, container, false);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        Map<String, String> newCategoryMap = new HashMap<>();
        List<String> newCategoriesToCreate = new ArrayList<>();

        recipeViewModel.getAllCategories(new OnFirebaseLoadedListener() {
            @Override
            public void onCategoriesLoaded(List<String> categories) {
                adapter = new CategoryAdapter(categories, selectedCategories, AddRecipeFragment.this, true);
                declareRecyclerView(adapter, binding.rvCategories, true, 0);
            }
            @Override
            public void onRecipeLoaded(List<Recipe> recipes) {
                // Not needed here
            }
        });

        binding.btnAddCategory.setOnClickListener(v -> {
            String newCategory = binding.etCategory.getText().toString().trim();

            if (!newCategory.isEmpty() && selectedCategories.size() < 3 && !selectedCategories.contains(newCategory.toLowerCase())) {
                // Maybe I will delete it
                newCategoriesToCreate.add(newCategory.toLowerCase());// Save for later push
                selectedCategories.add(newCategory);

                CategoryAdapter adapter2 = new CategoryAdapter(selectedCategories, selectedCategories, this, false);
                declareRecyclerView(adapter2, binding.rvSelectedCategories, true, 0);

                binding.etCategory.setText("");
            } else {
                Toast.makeText(requireContext(), "You can only select up to 3 unique categories", LENGTH_SHORT).show();
            }
        });

        binding.btnAddIngredient.setOnClickListener(view -> {
            binding.tvContainerIngredients.setVisibility(VISIBLE);
            String newIngredient = binding.etIngredient.getText().toString();
            binding.tvContainerIngredients.setText(newIngredient);
            binding.etIngredient.setText("");
            binding.etIngredient.append(binding.tvContainerIngredients.getText().toString() +"\n");
        });

        binding.btnAddStep.setOnClickListener(view -> {
            binding.tvContainerSteps.setVisibility(VISIBLE);
            String newStep = binding.etStep.getText().toString();
            binding.tvContainerSteps.setText(newStep);
            binding.etStep.setText("");
            binding.etStep.append(binding.tvContainerSteps.getText().toString() + "\n");
        });

        binding.btnSubmit.setOnClickListener(view -> {
            if (!binding.tvContainerSteps.getText().toString().isEmpty() &&
                    !binding.tvContainerIngredients.getText().toString().isEmpty() &&
                    !binding.etTitle.getText().toString().isEmpty() &&
                    !binding.etVideoUrl.getText().toString().isEmpty()&&
                    !selectedCategories.isEmpty() && selectedCategories != null
            ){
                Recipe recipe = new Recipe();
                recipe.setCategories(selectedCategories);
                recipe.setTitle(binding.etTitle.getText().toString());
                recipe.setSteps(binding.tvContainerSteps.getText().toString());
                recipe.setIngredients(binding.tvContainerIngredients.getText().toString());
                recipe.setVideoUrl(binding.etVideoUrl.getText().toString());
                recipe.setPublisherName(authViewModel.getCurrentUserInfo().get(NAME_MAP_KEY));
                recipe.setPublisherImage(null);
                recipe.setImageUrl(null);
                recipe.setLikedBy(new ArrayList<>());
                recipe.setLikesCount(0);

                for (String category: selectedCategories) {
                    newCategoryMap.put(CATEGORY_NAME, category);
                }
                recipeViewModel.createCategory(newCategoryMap, task -> {
                    if (task.isSuccessful()){
                        recipeViewModel.createRecipe(selectedCategories, recipe, task1 -> {
                            if (task.isSuccessful())
                                Toast.makeText(requireContext(), "added done", LENGTH_SHORT).show();
                            else
                                Toast.makeText(requireContext(), "recipe not add", LENGTH_SHORT).show();
                        });
                    }
                    else
                        Toast.makeText(requireContext(), "category not add", LENGTH_SHORT).show();
                });
            }
            else
                Toast.makeText(requireContext(), "There is empty field", LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    void declareRecyclerView(RecyclerView.Adapter adapter, RecyclerView recyclerView, boolean isLinear, int numOfSpan){
        recyclerView.setAdapter(adapter);
        if (isLinear)
            recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        else
            recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(),numOfSpan));
    }

    @Override
    public void onCategoryClicked() {
        selectedCategories = adapter.getSelectedCategories();

        CategoryAdapter adapter2 = new CategoryAdapter(selectedCategories,selectedCategories, this, false);
        declareRecyclerView(adapter2, binding.rvSelectedCategories, true, 0);
        adapter.notifyDataSetChanged();

        Log.d("categories size", String.valueOf(selectedCategories.size()));
    }


}
package com.ucas.firebaseminiproject.ui;

import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.ucas.firebaseminiproject.data.uplodeImage.CloudinaryHelper.uploadImageToCloudinary;
import static com.ucas.firebaseminiproject.utilities.Constance.CATEGORY_NAME;
import static com.ucas.firebaseminiproject.utilities.Constance.HOME_TAG;
import static com.ucas.firebaseminiproject.utilities.Constance.ID_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.ViewsCustomListeners.declareRecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ucas.firebaseminiproject.data.models.Recipe;
import com.ucas.firebaseminiproject.data.uplodeImage.CloudinaryHelper;
import com.ucas.firebaseminiproject.data.viewmodels.AuthViewModel;
import com.ucas.firebaseminiproject.data.viewmodels.RecipeViewModel;
import com.ucas.firebaseminiproject.databinding.FragmentAddRecipeBinding;
import com.ucas.firebaseminiproject.ui.adapters.CategoryAdapter;
import com.ucas.firebaseminiproject.utilities.OnItemListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRecipeFragment extends Fragment implements OnItemListener.OnCategoryListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private List<String> selectedCategories = new ArrayList<>();
    private CategoryAdapter adapter;
    private RecipeViewModel recipeViewModel;
    private AuthViewModel authViewModel;
    private FragmentAddRecipeBinding binding;
    private Uri selectedImageUri;
    private OnItemListener.OnFragmentListener listener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    binding.ivRecipeImage.setImageURI(uri); // Preview selected image
                }
            });


    public AddRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnItemListener.OnFragmentListener) context;
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

        // I used map to add category
        Map<String, String> newCategoryMap = new HashMap<>();

        // View all Categories
        recipeViewModel.getAllCategories(categories -> {
            adapter = new CategoryAdapter(categories, selectedCategories, AddRecipeFragment.this, true);
            declareRecyclerView(requireActivity() ,adapter, binding.rvCategories, true);

        });

        // Add a new category to selected categories not to categories collection
        binding.btnAddCategory.setOnClickListener(v -> {
            String newCategory = binding.etCategory.getText().toString().trim();

            if (!newCategory.isEmpty() && selectedCategories.size() < 3 && !selectedCategories.contains(newCategory.toLowerCase())) {
                selectedCategories.add(newCategory);

                CategoryAdapter adapter2 = new CategoryAdapter(selectedCategories, selectedCategories, this, false);
                declareRecyclerView(requireActivity(),adapter2, binding.rvSelectedCategories, true);

                binding.etCategory.setText("");
            } else {
                Toast.makeText(requireContext(), "You can only select up to 3 unique categories", LENGTH_SHORT).show();
            }
        });

        // Add new Ingredient
        binding.btnAddIngredient.setOnClickListener(view -> {
            binding.tvContainerIngredients.setVisibility(VISIBLE);
            String newIngredient = binding.etIngredient.getText().toString();
            binding.tvContainerIngredients.setText(newIngredient);
            binding.etIngredient.setText("");
            binding.etIngredient.append(binding.tvContainerIngredients.getText().toString() +"\n");
        });

        // Add a new step
        binding.btnAddStep.setOnClickListener(view -> {
            binding.tvContainerSteps.setVisibility(VISIBLE);
            String newStep = binding.etStep.getText().toString();
            binding.tvContainerSteps.setText(newStep);
            binding.etStep.setText("");
            binding.etStep.append(binding.tvContainerSteps.getText().toString() + "\n");
        });

        // Add Image
        binding.ivRecipeImage.setOnClickListener(view -> {
            galleryLauncher.launch("image/*");
        });

        // Add new generated categories to categories collection in firebase and then
        // add a new recipe to global recipes collection and recipes collection in category document
        binding.btnSubmit.setOnClickListener(view -> {
            // Sure nothing empty or null
            if (!binding.tvContainerSteps.getText().toString().isEmpty() &&
                    !binding.tvContainerIngredients.getText().toString().isEmpty() &&
                    !binding.etTitle.getText().toString().isEmpty() &&
                    !binding.etVideoUrl.getText().toString().isEmpty()&&
                    !selectedCategories.isEmpty() && selectedCategories != null
            ){
                // To Prevent user from create more than one
                binding.btnSubmit.setEnabled(false);
                // I used pojo class to add recipe
                Recipe recipe = new Recipe();
                recipe.setCategories(selectedCategories);
                recipe.setTitle(binding.etTitle.getText().toString());
                recipe.setSteps(binding.tvContainerSteps.getText().toString());
                recipe.setIngredients(binding.tvContainerIngredients.getText().toString());
                recipe.setVideoUrl(binding.etVideoUrl.getText().toString());
                recipe.setImageUrl(null);
                recipe.setLikesCount(0);

                for (String category: selectedCategories) {
                    newCategoryMap.put(CATEGORY_NAME, category);
                }

                // Get publisher Info
                authViewModel.getCurrentUserInfo(userMap -> {
                    recipe.setPublisherId(userMap.get(ID_MAP_KEY));

                    if (selectedImageUri != null) {
                        uploadImageToCloudinary(requireContext(), selectedImageUri, new CloudinaryHelper.OnUploadCompleteListener() {
                            @Override
                            public void onSuccess(String imageUrl) {
                                recipe.setImageUrl(imageUrl);
                                saveRecipe(recipe, newCategoryMap); // proceed after image upload
                            }

                            @Override
                            public void onFailure(String error) {
                                Toast.makeText(requireContext(), "Image upload failed: " + error, LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        recipe.setImageUrl(null);
                        saveRecipe(recipe, newCategoryMap);
                    }
                });
            }
            else
                Toast.makeText(requireContext(), "There is empty field", LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }


    private void saveRecipe(Recipe recipe, Map<String, String> newCategoryMap) {
        // Create the new category first, then save the recipe
        Log.d("DEBUG", "Creating new category...");
        recipeViewModel.createCategory(newCategoryMap, categoryTask -> {
            if (categoryTask.isSuccessful()) {
                Log.d("DEBUG", "Category created successfully.");

                recipeViewModel.createRecipe(selectedCategories, recipe, recipeTask -> {
                    if (recipeTask.isSuccessful()) {
                        Log.d("DEBUG", "Recipe saved successfully.");
                        Toast.makeText(requireContext(), "Recipe added successfully", LENGTH_SHORT).show();
                        listener.onNavigateFragments(HOME_TAG);
                    } else {
                        Log.e("DEBUG", "Failed to save recipe after category creation.");
                        Toast.makeText(requireContext(), "Failed to add recipe", LENGTH_SHORT).show();
                    }
                });

            } else {
                Log.e("DEBUG", "Failed to create category.");
                Toast.makeText(requireContext(), "Failed to create category", LENGTH_SHORT).show();
            }
        });

    }



    @Override
    public void onCategoryClicked() {
        selectedCategories = adapter.getSelectedCategories();

        CategoryAdapter adapter2 = new CategoryAdapter(selectedCategories,selectedCategories, this, false);
        declareRecyclerView(requireActivity(), adapter2, binding.rvSelectedCategories, true);
        adapter.notifyDataSetChanged();

        Log.d("categories size", String.valueOf(selectedCategories.size()));
    }


}
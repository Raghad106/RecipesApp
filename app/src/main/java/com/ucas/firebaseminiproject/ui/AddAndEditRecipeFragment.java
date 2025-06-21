package com.ucas.firebaseminiproject.ui;

import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.ucas.firebaseminiproject.data.uplodeImage.CloudinaryHelper.uploadImageToCloudinary;
import static com.ucas.firebaseminiproject.utilities.Constance.ADD_RECIPE_TAG;
import static com.ucas.firebaseminiproject.utilities.Constance.CATEGORY_NAME;
import static com.ucas.firebaseminiproject.utilities.Constance.EDIT_RECIPE_TAG;
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

import com.squareup.picasso.Picasso;
import com.ucas.firebaseminiproject.data.models.Recipe;
import com.ucas.firebaseminiproject.data.uplodeImage.CloudinaryHelper;
import com.ucas.firebaseminiproject.data.viewmodels.AuthViewModel;
import com.ucas.firebaseminiproject.data.viewmodels.ProfileViewModel;
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
 * Use the {@link AddAndEditRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAndEditRecipeFragment extends Fragment implements OnItemListener.OnCategoryListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "param1";
    private static final String RECIPE = "param2";
    private List<String> selectedCategories = new ArrayList<>();
    private CategoryAdapter adapter;
    private RecipeViewModel recipeViewModel;
    private ProfileViewModel profileViewModel;
    private FragmentAddRecipeBinding binding;
    private Uri selectedImageUri;
    private OnItemListener.OnFragmentListener listener;
    //private List<String> oldCategories;

    // TODO: Rename and change types of parameters
    private String tag;
    private Recipe recipe;



    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    binding.ivRecipeImage.setImageURI(uri); // Preview selected image
                }
            });


    public AddAndEditRecipeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnItemListener.OnFragmentListener) context;
    }

    public static AddAndEditRecipeFragment newInstance(String tag) {
        AddAndEditRecipeFragment fragment = new AddAndEditRecipeFragment();
        Bundle args = new Bundle();
        args.putString(TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }
    public static AddAndEditRecipeFragment newInstance(String tag, Recipe recipe) {
        AddAndEditRecipeFragment fragment = new AddAndEditRecipeFragment();
        Bundle args = new Bundle();
        args.putString(TAG, tag);
        args.putParcelable(RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tag = getArguments().getString(TAG);
            recipe = getArguments().getParcelable(RECIPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddRecipeBinding.inflate(inflater, container, false);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        Log.d("ADDtTAg",tag.toString());
        if (tag != null){
            // I used map to add category
            Map<String, String> newCategoryMap = new HashMap<>();

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

            // Add a new photo
            binding.ivRecipeImage.setOnClickListener(view -> {
                galleryLauncher.launch("image/*");
            });

            if (tag.equals(ADD_RECIPE_TAG)){
                // View all Categories
                recipeViewModel.getAllCategories(categories -> {
                    adapter = new CategoryAdapter(categories, selectedCategories, AddAndEditRecipeFragment.this, true);
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

                // Add new generated categories to categories collection in firebase and then
                // add a new recipe to global recipes collection and recipes collection in category document
                binding.btnSubmit.setOnClickListener(view -> {
                    // Sure nothing empty or null
                    if (!binding.tvContainerSteps.getText().toString().isEmpty() &&
                            !binding.tvContainerIngredients.getText().toString().isEmpty() &&
                            !binding.etTitle.getText().toString().isEmpty() &&
                            !binding.etVideoUrl.getText().toString().isEmpty()&&
                            !selectedCategories.isEmpty() && selectedCategories != null &&
                            selectedImageUri != null
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
                        profileViewModel.getCurrentUserInfo(userMap -> {
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
                            }
                        });
                    }
                    else
                        Toast.makeText(requireContext(), "There is empty field", LENGTH_SHORT).show();
                });
            }

            else if (tag.equals(EDIT_RECIPE_TAG) && recipe != null) {
                if (!recipe.getImageUrl().isEmpty() && recipe.getImageUrl() != null)
                    Picasso.get().load(recipe.getImageUrl()).fit().centerCrop().into(binding.ivRecipeImage);
                final List<String> oldCategories = new ArrayList<>(recipe.getCategories());
                selectedCategories = recipe.getCategories();
                Log.d("recipesCt", "old"+oldCategories.size());
                // View all Categories
                recipeViewModel.getAllCategories(categories -> {
                    adapter = new CategoryAdapter(categories, recipe.getCategories(), AddAndEditRecipeFragment.this, true);
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

                binding.tvTitle.setText("Edit a Recipe information");
                binding.btnSubmit.setText("Apply Updates");
                binding.etTitle.setText(recipe.getTitle());
                binding.tvContainerSteps.setVisibility(VISIBLE);
                binding.tvContainerIngredients.setVisibility(VISIBLE);
                binding.tvContainerSteps.setText(recipe.getSteps());
                binding.etStep.setText(recipe.getSteps());
                binding.tvContainerIngredients.setText(recipe.getIngredients());
                binding.etIngredient.setText(recipe.getIngredients());
                binding.etVideoUrl.setText(recipe.getVideoUrl());

                CategoryAdapter adapter2 = new CategoryAdapter(recipe.getCategories(), recipe.getCategories(), this, false);
                declareRecyclerView(requireActivity(), adapter2, binding.rvSelectedCategories, true);

                binding.btnSubmit.setOnClickListener(view -> {
                    binding.btnSubmit.setEnabled(false);
                    CategoryAdapter adapter1 = (CategoryAdapter) binding.rvSelectedCategories.getAdapter();

                    String title = binding.etTitle.getText().toString().trim();
                    String steps = binding.tvContainerSteps.getText().toString().trim();
                    String ingredients = binding.tvContainerIngredients.getText().toString().trim();
                    String videoUrl = binding.etVideoUrl.getText().toString().trim();

                    if (!title.isEmpty() && !steps.isEmpty() && !ingredients.isEmpty() && !videoUrl.isEmpty()) {
                        recipe.setTitle(title);
                        recipe.setSteps(steps);
                        recipe.setIngredients(ingredients);
                        recipe.setVideoUrl(videoUrl);

                        if (adapter1 != null) {
                            List<String> newCategories = adapter1.getCategories();

                            if (selectedImageUri != null) {
                                // Upload new image
                                uploadImageToCloudinary(requireContext(), selectedImageUri, new CloudinaryHelper.OnUploadCompleteListener() {
                                    @Override
                                    public void onSuccess(String imageUrl) {
                                        recipe.setImageUrl(imageUrl);
                                        editRecipe(oldCategories, newCategories, recipe);
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        binding.btnSubmit.setEnabled(true); // re-enable button
                                        Toast.makeText(requireContext(), "Image upload failed: " + error, LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // No new image, just update the recipe
                                editRecipe(oldCategories, newCategories, recipe);
                            }
                        }
                    } else {
                        binding.btnSubmit.setEnabled(true);
                        Toast.makeText(requireContext(), "Please fill all fields", LENGTH_SHORT).show();
                    }
                });

            }

        }
        return binding.getRoot();
    }
    private void editRecipe(List<String> oldCategories, List<String> categories, Recipe recipe){
        Log.d("recipesCt", "old"+oldCategories.size());
        Log.d("recipesCt", "new"+categories.size());
        recipeViewModel.editRecipe(oldCategories, categories, recipe, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "Updated Successfully", LENGTH_SHORT).show();
                listener.onNavigateFragments(HOME_TAG);
            }
        });
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
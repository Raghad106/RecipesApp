package com.ucas.firebaseminiprojectcomplete.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.CURRENT_USER_TAG;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.DIALOG_RECIPE_TAG;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.EDIT_RECIPE_TAG;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.ID_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.USER_TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;
import com.ucas.firebaseminiprojectcomplete.data.viewmodels.ProfileViewModel;
import com.ucas.firebaseminiprojectcomplete.data.viewmodels.RecipeViewModel;
import com.ucas.firebaseminiprojectcomplete.databinding.FragmentRecipeDetailsBinding;
import com.ucas.firebaseminiprojectcomplete.utilities.OnItemListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecipeDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RECIPE_ID = "param1";
    private static final String ARG_PUBLISHER_ID = "param2";
    private RecipeViewModel recipeViewModel;
    private ProfileViewModel profileViewModel;
    private OnItemListener.OnFragmentListener listener;

    // TODO: Rename and change types of parameters
    private String recipeId;
    private String publisherId;


    public RecipeDetailsFragment() {
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
     * @param recipeId Parameter 1.
     * @param publisherId Parameter 2.
     * @return A new instance of fragment RecipeDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecipeDetailsFragment newInstance(String recipeId, String publisherId) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECIPE_ID, recipeId);
        args.putString(ARG_PUBLISHER_ID, publisherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeId = getArguments().getString(ARG_RECIPE_ID);
            publisherId = getArguments().getString(ARG_PUBLISHER_ID);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentRecipeDetailsBinding binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        binding.tvSteps.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        binding.tvIngredients.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });

        binding.tvIngredients.setMovementMethod(new ScrollingMovementMethod());
        binding.tvSteps.setMovementMethod(new ScrollingMovementMethod());


        if (recipeId != null && publisherId != null){
            recipeViewModel.getRecipeByRecipeId(recipeId, recipe -> {
                if (recipe != null){
                    profileViewModel.getCurrentUserInfo(userInfo -> {
                        if (recipe.getPublisherId().equals(userInfo.get(ID_MAP_KEY))){
                            binding.btnSave.setVisibility(GONE);
                            binding.btnEdit.setVisibility(VISIBLE);
                            binding.btnDelete.setVisibility(VISIBLE);
                        }else {
                            binding.btnSave.setVisibility(VISIBLE);
                            binding.btnEdit.setVisibility(GONE);
                            binding.btnDelete.setVisibility(GONE);
                        }
                        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty())
                            Picasso.get().load(recipe.getImageUrl()).fit().centerCrop().into(binding.ivRecipeImage);
                        if (recipe.getPublisherImage() != null && !recipe.getPublisherImage().isEmpty())
                            Picasso.get().load(recipe.getPublisherImage()).fit().centerCrop().into(binding.ivUserAvatar);

                        binding.tvTitle.setText(recipe.getTitle().toUpperCase());
                        binding.tvUserName.setText(recipe.getPublisherName());
                        binding.tvSteps.setText(recipe.getSteps());
                        binding.tvIngredients.setText(recipe.getIngredients());
                        if (recipe.getCategories().size() == 1){
                            binding.tvCategory1.setText(recipe.getCategories().get(0));
                        } else if (recipe.getCategories().size() == 2) {
                            binding.tvCategory1.setText(recipe.getCategories().get(0));
                            binding.tvCategory2.setText(recipe.getCategories().get(1));
                            binding.tvCategory2.setVisibility(VISIBLE);
                        } else if (recipe.getCategories().size() == 3) {
                            binding.tvCategory1.setText(recipe.getCategories().get(0));
                            binding.tvCategory2.setText(recipe.getCategories().get(1));
                            binding.tvCategory3.setText(recipe.getCategories().get(2));
                            binding.tvCategory2.setVisibility(VISIBLE);
                            binding.tvCategory3.setVisibility(VISIBLE);
                        }
                        binding.btnPlayVideo.setOnClickListener(view -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(recipe.getVideoUrl()));
                            startActivity(intent);
                        });

                        binding.btnEdit.setOnClickListener(view -> {
                            listener.onNavigateEditRecipeDetailsFragment(EDIT_RECIPE_TAG, recipe);
                        });

                        binding.btnDelete.setOnClickListener(view -> {
                            listener.onDeleteRecipe(DIALOG_RECIPE_TAG, recipe);
                        });

                        recipeViewModel.checkLikeStatus(recipeId, userInfo.get(ID_MAP_KEY), (isLike, likeCount) -> {
                            Log.d("LIKE", "Like icon"+isLike+likeCount);
                            if (isLike)
                                binding.ivLike.setColorFilter(Color.parseColor("#FB912C"));
                            else
                                binding.ivLike.setColorFilter(Color.parseColor("#606060"));

                            binding.tvLikesCount.setText(String.valueOf(likeCount));
                        });

                        binding.ivLike.setOnClickListener(view -> {
                            Log.d("LIKE", "Like icon clicked");
                            recipeViewModel.toggleLike(recipeId, userInfo.get(ID_MAP_KEY), (isLike, likeCount) -> {
                                if (isLike)
                                    binding.ivLike.setColorFilter(Color.parseColor("#FB912C"));
                                else
                                    binding.ivLike.setColorFilter(Color.parseColor("#606060"));

                                binding.tvLikesCount.setText(String.valueOf(likeCount));
                            });
                        });

                        profileViewModel.checkIsSaved(recipeId, isSave -> {
                            if (isSave)
                                binding.btnSave.setColorFilter(Color.parseColor("#FB912C"));
                            else
                                binding.btnSave.setColorFilter(Color.parseColor("#FFFFFF"));

                        });

                        binding.btnSave.setOnClickListener(view -> {
                            profileViewModel.toggleSaveRecipe(recipeId, isSave -> {
                                if (isSave)
                                    binding.btnSave.setColorFilter(Color.parseColor("#FB912C"));
                                else
                                    binding.btnSave.setColorFilter(Color.parseColor("#FFFFFF"));
                            });
                        });

                        binding.ivUserAvatar.setOnClickListener(view -> {
                            String  userId = recipe.getPublisherId();
                            if (userId != null && !userId.isEmpty()){
                                profileViewModel.getCurrentUserInfo(userInfo2 -> {
                                    if (userInfo2.get(ID_MAP_KEY).equals(userId))
                                        listener.onNavigateFragments(CURRENT_USER_TAG);
                                    else
                                        listener.onNavigateToUserProfile(USER_TAG, userId);
                                });
                            }
                        });
                    });

                }
            });
        }
        return binding.getRoot();
    }
}
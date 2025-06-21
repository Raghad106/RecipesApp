package com.ucas.firebaseminiproject.ui;

import static com.ucas.firebaseminiproject.utilities.Constance.CATEGORY_COLLECTION;
import static com.ucas.firebaseminiproject.utilities.Constance.CURRENT_USER_TAG;
import static com.ucas.firebaseminiproject.utilities.Constance.ID_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.SAVED_RECIPE_TAG;
import static com.ucas.firebaseminiproject.utilities.Constance.USER_TAG;
import static com.ucas.firebaseminiproject.utilities.ViewsCustomListeners.declareRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ucas.firebaseminiproject.data.models.Recipe;
import com.ucas.firebaseminiproject.data.viewmodels.ProfileViewModel;
import com.ucas.firebaseminiproject.data.viewmodels.RecipeViewModel;
import com.ucas.firebaseminiproject.databinding.FragmentRecyclerViewBinding;
import com.ucas.firebaseminiproject.ui.adapters.RecipeAdapter;
import com.ucas.firebaseminiproject.utilities.OnItemListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecyclerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecyclerViewFragment extends Fragment implements OnItemListener.OnRecipeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "param1";
    private static final String RECIPES_TYPE = "param2";
    private List<Recipe> originalRecipes = new ArrayList<>();
    private RecipeAdapter adapter;
    private RecipeViewModel recipeViewModel;
    private ProfileViewModel profileViewModel;
    private OnItemListener.OnFragmentListener listener;

    // TODO: Rename and change types of parameters
    private String tag;
    private String category;

    public RecyclerViewFragment() {
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
     * @return A new instance of fragment RecyclerViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecyclerViewFragment newInstance(String param1, String param2) {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(TAG, param1);
        args.putString(RECIPES_TYPE, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public static RecyclerViewFragment newInstance(String param1) {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(TAG, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tag = getArguments().getString(TAG);
            category = getArguments().getString(RECIPES_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentRecyclerViewBinding binding = FragmentRecyclerViewBinding.inflate(inflater, container, false);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        if (tag != null){
            if (tag.equals(CATEGORY_COLLECTION)){
                if (category != null){
                    if (category.equals("all")) {
                        recipeViewModel.getAllRecipes(recipes -> {
                            originalRecipes = recipes;
                            adapter = new RecipeAdapter(RecyclerViewFragment.this, recipes);
                            declareRecyclerView(requireActivity(), adapter, binding.rvItems, false);
                        });
                    }
                    else {
                        recipeViewModel.getRecipesByCategoryName(category.toLowerCase(), recipes -> {
                            originalRecipes = recipes;
                            adapter = new RecipeAdapter(RecyclerViewFragment.this, originalRecipes);
                            declareRecyclerView(requireActivity(), adapter, binding.rvItems, false);
                        });
                    }

                }

            }


            else if (tag.equals(SAVED_RECIPE_TAG)){
                profileViewModel.getSavedRecipes(recipes -> {
                    declareRecyclerView(requireContext(), new RecipeAdapter(RecyclerViewFragment.this, recipes), binding.rvItems, false);
                });
            }
        }
        return binding.getRoot();
    }

    @Override
    public void onLayoutClicked(String recipeId, String userId) {
        listener.onNavigateRecipeDetailsFragment(recipeId, userId);
    }

    @Override
    public void onUserClicked(String userId) {
        if (userId != null && !userId.isEmpty()){
            profileViewModel.getCurrentUserInfo(userInfo -> {
                if (userInfo.get(ID_MAP_KEY).equals(userId))
                    listener.onNavigateFragments(CURRENT_USER_TAG);
                else
                    listener.onNavigateToUserProfile(USER_TAG, userId);
            });
        }
    }

    @Override
    public void onVideoClicked(String videoLink) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(videoLink));
        startActivity(intent);
    }

    public void filterRecipes(String query) {
        if (adapter == null || originalRecipes == null) {
            return;
        }

        if (query == null || query.isEmpty()) {
            adapter.updateList(originalRecipes);
            return;
        }

        List<Recipe> filtered = new ArrayList<>();
        for (Recipe recipe : originalRecipes) {
            if (recipe.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(recipe);
            }
        }
        adapter.updateList(filtered);
    }


}
package com.ucas.firebaseminiprojectcomplete.ui;

import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.CATEGORY_COLLECTION;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.CURRENT_USER_TAG;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.ID_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.SAVED_RECIPE_TAG;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.USER_TAG;
import static com.ucas.firebaseminiprojectcomplete.utilities.ViewsCustomListeners.declareRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ucas.firebaseminiprojectcomplete.data.models.Recipe;
import com.ucas.firebaseminiprojectcomplete.data.viewmodels.ProfileViewModel;
import com.ucas.firebaseminiprojectcomplete.data.viewmodels.RecipeViewModel;
import com.ucas.firebaseminiprojectcomplete.databinding.FragmentRecyclerViewBinding;
import com.ucas.firebaseminiprojectcomplete.ui.adapters.RecipeAdapter;
import com.ucas.firebaseminiprojectcomplete.utilities.OnItemListener;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewFragment extends Fragment implements OnItemListener.OnRecipeListener {

    private static final String TAG = "param1";
    private static final String RECIPES_TYPE = "param2";

    private FragmentRecyclerViewBinding binding;
    private final List<Recipe> originalRecipes = new ArrayList<>();
    private RecipeAdapter adapter;
    private RecipeViewModel recipeViewModel;
    private ProfileViewModel profileViewModel;
    private OnItemListener.OnFragmentListener listener;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private final int visibleThreshold = 3;

    private String tag;
    private String category;

    public RecyclerViewFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnItemListener.OnFragmentListener) context;
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecyclerViewBinding.inflate(inflater, container, false);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        isLoading = false;
        isLastPage = false;

        if (tag != null) {
            if (tag.equals(CATEGORY_COLLECTION)) {
                if (category != null) {
                    if (category.equals("all")) {
                        if (!originalRecipes.isEmpty() && adapter != null) {
                            Log.d("RECYCLER_VIEW", "Reusing existing adapter.");
                            declareRecyclerView(requireActivity(), adapter, binding.rvItems, false);
                            attachScrollListener();
                        } else {
                            loadPaginatedRecipes();
                        }
                        }  else {
                        recipeViewModel.getRecipesByCategoryName(category.toLowerCase(), recipes -> {
                            originalRecipes.clear();
                            originalRecipes.addAll(recipes);
                            adapter = new RecipeAdapter(RecyclerViewFragment.this, originalRecipes);
                            declareRecyclerView(getActivity(), adapter, binding.rvItems, false);
                        });
                    }
                }
            } else if (tag.equals(SAVED_RECIPE_TAG)) {
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
        if (userId != null && !userId.isEmpty()) {
            profileViewModel.getCurrentUserInfo(userInfo -> {
                if (userInfo.get(ID_MAP_KEY).equals(userId)) {
                    listener.onNavigateFragments(CURRENT_USER_TAG);
                } else {
                    listener.onNavigateToUserProfile(USER_TAG, userId);
                }
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
        if (adapter == null || originalRecipes.isEmpty()) return;

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

    private void loadPaginatedRecipes() {
        Log.d("PAGINATION", "loadPaginatedRecipes called");

        if (isLoading || isLastPage) {
            Log.d("PAGINATION", "Already loading or reached last page");
            return;
        }

        isLoading = true;
        Log.d("PAGINATION", "Loading new recipes...");

        boolean isInitial = originalRecipes.isEmpty();
        Log.d("PAGINATION", "isInitial: " + isInitial);

        recipeViewModel.getAllRecipesPaginated(recipes -> {
            Log.d("ALL_RECIPES", "Received recipes size: " + recipes.size());

            if (recipes.isEmpty()) {
                isLastPage = true;
            }

            if (adapter == null) {
                Log.d("PAGINATION", "Adapter is null. Creating new adapter.");
                originalRecipes.addAll(recipes);
                adapter = new RecipeAdapter(RecyclerViewFragment.this, originalRecipes);
                declareRecyclerView(requireActivity(), adapter, binding.rvItems, false);
                attachScrollListener();
            } else {
                Log.d("PAGINATION", "Adapter exists. Appending data.");
                int oldSize = originalRecipes.size();
                originalRecipes.addAll(recipes);
                adapter.notifyItemRangeInserted(oldSize, recipes.size());
            }

            isLoading = false;
            Log.d("PAGINATION", "Loading complete.");
        }, isInitial);
    }

    private void attachScrollListener() {
        Log.d("PAGINATION", "ScrollListener attached");
        binding.rvItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager == null) return;

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                Log.d("SCROLL", "Total: " + totalItemCount + ", LastVisible: " + lastVisibleItem);

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    Log.d("SCROLL", "End reached. Triggering pagination...");
                    loadPaginatedRecipes();
                }
            }
        });
    }

}

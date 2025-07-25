package com.ucas.firebaseminiprojectcomplete.ui;

import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.ADD_RECIPE_TAG;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.CATEGORY_COLLECTION;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.USER_TAG;
import static com.ucas.firebaseminiprojectcomplete.utilities.ViewsCustomListeners.declareRecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayoutMediator;
import com.ucas.firebaseminiprojectcomplete.data.viewmodels.ProfileViewModel;
import com.ucas.firebaseminiprojectcomplete.data.viewmodels.RecipeViewModel;
import com.ucas.firebaseminiprojectcomplete.databinding.FragmentHomeBinding;
import com.ucas.firebaseminiprojectcomplete.ui.adapters.CategoryAdapter;
import com.ucas.firebaseminiprojectcomplete.ui.adapters.FragmentAdapter;
import com.ucas.firebaseminiprojectcomplete.ui.adapters.UserAdapter;
import com.ucas.firebaseminiprojectcomplete.utilities.OnItemListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnItemListener.OnUserListener,OnItemListener.OnCategoryListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentHomeBinding binding;
    private OnItemListener.OnFragmentListener listener;
    private RecipeViewModel recipeViewModel;
    private ProfileViewModel profileViewModel;
    private ArrayList<String> tabs = new ArrayList<>();
    private ArrayList<Fragment> fragments = new ArrayList<>();

    public HomeFragment() {
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
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        FragmentAdapter adapter = new FragmentAdapter(requireActivity());

        tabs.clear();
        fragments.clear();
        tabs.add("ALL");
        fragments.add(RecyclerViewFragment.newInstance(CATEGORY_COLLECTION, "all"));

        Log.d("GO_TO_HOME", requireContext().toString());

        profileViewModel.getTopCreators(3, userInfo -> {
            if (userInfo != null && !userInfo.isEmpty())
                declareRecyclerView(requireContext(), new UserAdapter(userInfo, HomeFragment.this), binding.rvUsers, true);

        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search(s);
                return false;
            }
        });

        binding.searchView.setOnCloseListener(() -> {
            search(""); // clear search
            return false;
        });

        recipeViewModel.getAllCategoriesPaginated(categories -> {
            tabs.clear();
            fragments.clear();
            tabs.add("ALL");
            fragments.add(RecyclerViewFragment.newInstance(CATEGORY_COLLECTION, "all"));

            for (String category: categories) {
                Log.d("CAT",category);
                tabs.add(category);
                fragments.add(RecyclerViewFragment.newInstance(CATEGORY_COLLECTION, category.toLowerCase()));
            }
            if (tabs.size() == fragments.size()) {
                adapter.addFragments(fragments);
                binding.vpRecipes.setAdapter(adapter);

                new TabLayoutMediator(binding.tlCategories, binding.vpRecipes, (tab, position) -> {
                    tab.setText(tabs.get(position));
                    Log.d("GO_TO_HOME", String.valueOf(tabs.size()));
                }).attach();
            }
        }, true);

        binding.vpRecipes.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) { // "ALL" tab is first
                    fragments.set(0, RecyclerViewFragment.newInstance(CATEGORY_COLLECTION, "all"));
                }
                // Existing pagination loading logic
                if (position == adapter.getItemCount() - 1) {
                    recipeViewModel.getAllCategoriesPaginated(newCategories -> {
                        List<Fragment> nextPageFragments = new ArrayList<>();
                        for (String category : newCategories) {
                            tabs.add(category);
                            nextPageFragments.add(RecyclerViewFragment.newInstance(CATEGORY_COLLECTION, category));
                        }
                        adapter.addFragments(nextPageFragments);
                    }, false);
                }
            }
        });



        binding.fab.setOnClickListener(view -> {
            listener.onNavigateFragments(ADD_RECIPE_TAG);
        });

        return binding.getRoot();
    }



    private void search(String query) {
        if (fragments != null && !fragments.isEmpty()){
            int position = binding.vpRecipes.getCurrentItem();
            Fragment fragment = fragments.get(position);

            if (fragment instanceof RecyclerViewFragment) {
                Log.d("Search Problem", query);
                ((RecyclerViewFragment) fragment).filterRecipes(query);
            }
        }
    }


    @Override
    public void onUserClicked(String userId) {
        if (userId != null && !userId.isEmpty())
            listener.onNavigateToUserProfile(USER_TAG, userId);
    }

    @Override
    public void onCategoryClicked() {

    }
}
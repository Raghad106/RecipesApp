package com.ucas.firebaseminiproject.ui;

import static com.ucas.firebaseminiproject.utilities.Constance.CATEGORY_COLLECTION;
import static com.ucas.firebaseminiproject.utilities.ViewsCustomListeners.declareRecyclerView;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.telephony.AvailableNetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ucas.firebaseminiproject.R;
import com.ucas.firebaseminiproject.data.models.Recipe;
import com.ucas.firebaseminiproject.data.viewmodels.AuthViewModel;
import com.ucas.firebaseminiproject.data.viewmodels.RecipeViewModel;
import com.ucas.firebaseminiproject.databinding.FragmentRecyclerViewBinding;
import com.ucas.firebaseminiproject.ui.adapters.RecipeAdapter;
import com.ucas.firebaseminiproject.utilities.OnItemListener;

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
    private RecipeViewModel recipeViewModel;
    private AuthViewModel authViewModel;

    // TODO: Rename and change types of parameters
    private String tag;
    private String category;

    public RecyclerViewFragment() {
        // Required empty public constructor
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
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        if (tag != null){
            if (category != null){
                if (category.equals("all")){
                    recipeViewModel.getAllRecipes( recipes -> {
                        Log.d("Problem", category+ recipes.size());
                        declareRecyclerView(requireActivity(), new RecipeAdapter(RecyclerViewFragment.this, recipes), binding.rvItems, false);
                    });
                }else {
                    recipeViewModel.getRecipesByCategoryName(category.toLowerCase(), recipes -> {
                        Log.d("Problem", category+ recipes.size());
                        declareRecyclerView(requireActivity(), new RecipeAdapter(RecyclerViewFragment.this, recipes), binding.rvItems, false);
                    });
                }
            }
        }
        return binding.getRoot();
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
    public boolean onVideoClicked(String videoLink) {
        return false;
    }

//    private void addDataToRecycler(List<Recipe> recipes){
//        authViewModel.getUserInfoById(recipe.getPublisherId(), userInfo -> {
//            declareRecyclerView(requireActivity(), new RecipeAdapter(RecyclerViewFragment.this, recipes, ), binding.rvItems, false);
//        });
//    }
}
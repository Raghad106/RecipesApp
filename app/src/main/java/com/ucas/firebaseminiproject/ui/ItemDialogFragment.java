package com.ucas.firebaseminiproject.ui;

import static android.widget.Toast.LENGTH_SHORT;
import static com.ucas.firebaseminiproject.utilities.Constance.DIALOG_LOGOUT_TAG;
import static com.ucas.firebaseminiproject.utilities.Constance.DIALOG_RECIPE_TAG;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.ucas.firebaseminiproject.R;
import com.ucas.firebaseminiproject.data.viewmodels.AuthViewModel;
import com.ucas.firebaseminiproject.data.viewmodels.RecipeViewModel;
import com.ucas.firebaseminiproject.utilities.OnItemListener;


public class ItemDialogFragment extends DialogFragment {
    //If I want customizing another dialog, I will do it here
    private static final String ARG_MESSAGE = "delete msg";
    private static final String ARG_TAG = "tag";
    private static final String ARG_RECIPE_ID = "recipe id";

    private String deleteMsg;
    private String tag;
    private String recipeId;
    private AuthViewModel authViewModel;
    private RecipeViewModel recipeViewModel;
    private OnItemListener.OnIntentListener listener;


    public ItemDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnItemListener.OnIntentListener) context;
    }

    public static ItemDialogFragment newInstance(String deleteMsg, String tag) {
        ItemDialogFragment fragment = new ItemDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, deleteMsg);
        args.putString(ARG_TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    public static ItemDialogFragment newInstance(String deleteMsg, String tag, String recipeId) {
        ItemDialogFragment fragment = new ItemDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, deleteMsg);
        args.putString(ARG_TAG, tag);
        args.putString(ARG_RECIPE_ID, recipeId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            deleteMsg = getArguments().getString(ARG_MESSAGE);
            tag = getArguments().getString(ARG_TAG);
            recipeId = getArguments().getString(ARG_RECIPE_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);
        recipeViewModel = new ViewModelProvider(requireActivity()).get(RecipeViewModel.class);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
        alertDialog.setTitle("Delete Dialog");
        alertDialog.setMessage(deleteMsg);
        alertDialog.setIcon(R.drawable.ic_delete);
        alertDialog.setPositiveButton("Yes", (dialogInterface, i) -> {
            if (tag != null){
                if (tag.equals(DIALOG_LOGOUT_TAG)) {
                    authViewModel.saveRememberInfo(requireContext(), false);
                    Toast.makeText(requireActivity(), "Logout successfully", LENGTH_SHORT).show();
                    listener.onIntentListener(DIALOG_LOGOUT_TAG);
                }
                else if (tag.equals(DIALOG_RECIPE_TAG) && recipeId != null && !recipeId.isEmpty()){
                    recipeViewModel.deleteRecipe(recipeId, task -> {
                        if (task.isSuccessful()) {
                            Context context = getContext(); // or getActivity()
                            if (context != null) {
                                Toast.makeText(context, "Deleted successfully", LENGTH_SHORT).show();
                            }
                            listener.onIntentListener(DIALOG_RECIPE_TAG);
                        }
                    });
                }
            }

        });
        alertDialog.setNegativeButton("No", (dialogInterface, i) -> {
        });
        return alertDialog.create();
    }


}
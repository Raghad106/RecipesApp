package com.ucas.firebaseminiproject.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.ucas.firebaseminiproject.data.uplodeImage.CloudinaryHelper.uploadImageToCloudinary;
import static com.ucas.firebaseminiproject.utilities.Constance.COUNTRY_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.DIALOG_LOGOUT_TAG;
import static com.ucas.firebaseminiproject.utilities.Constance.EMAIL_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.ID_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.IMAGE_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.NAME_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.ViewsCustomListeners.declareRecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.ucas.firebaseminiproject.R;
import com.ucas.firebaseminiproject.data.uplodeImage.CloudinaryHelper;
import com.ucas.firebaseminiproject.data.viewmodels.ProfileViewModel;
import com.ucas.firebaseminiproject.databinding.FragmentAddRecipeBinding;
import com.ucas.firebaseminiproject.databinding.FragmentProfileBinding;
import com.ucas.firebaseminiproject.ui.adapters.RecipeAdapter;
import com.ucas.firebaseminiproject.utilities.OnItemListener;
import com.ucas.firebaseminiproject.utilities.ViewsCustomListeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements OnItemListener.OnRecipeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private Uri selectedImageUri;
    private OnItemListener.OnFragmentListener listener;

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    binding.ivProfileImage.setImageURI(uri); // Preview selected image
                }
            });

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
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
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        profileViewModel.getCurrentUserInfo(userInfo -> {
            if (userInfo != null && !userInfo.isEmpty()) {
                binding.etUserEmail.setText(userInfo.get(EMAIL_MAP_KEY));
                binding.etUserName.setText(userInfo.get(NAME_MAP_KEY));
                binding.tvUseCountry.setText(userInfo.get(COUNTRY_MAP_KEY));
                if (userInfo.get(IMAGE_MAP_KEY) != null && !userInfo.get(IMAGE_MAP_KEY).isEmpty())
                    Picasso.get().load(userInfo.get(IMAGE_MAP_KEY)).fit().centerCrop().into(binding.ivProfileImage);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.country_list,
                        android.R.layout.simple_spinner_item
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerCountry.setAdapter(adapter);
                binding.ivEditProfile.setOnClickListener(view -> {
                    binding.ivAdd.setVisibility(VISIBLE);
                    binding.btnEdit.setVisibility(VISIBLE);
                    binding.ivEditProfile.setVisibility(GONE);
                    binding.spinnerCountry.setVisibility(VISIBLE);
                    binding.etUserEmail.setEnabled(true);
                    binding.etUserEmail.setFocusable(true);
                    binding.etUserEmail.setFocusableInTouchMode(true);
                    binding.etUserEmail.setCursorVisible(true);

                    binding.etUserName.setEnabled(true);
                    binding.etUserName.setFocusable(true);
                    binding.etUserName.setFocusableInTouchMode(true);
                    binding.etUserName.setCursorVisible(true);

                    String currentCountry = binding.tvUseCountry.getText().toString();
                    int spinnerPosition = adapter.getPosition(currentCountry);
                    binding.spinnerCountry.setSelection(spinnerPosition);

                    binding.ivAdd.setOnClickListener(image -> {
                        galleryLauncher.launch("image/*");
                    });
                });
//
                binding.btnEdit.setOnClickListener(view -> {
                    Map<String, String> updatedUserInfo = new HashMap<>();
                    binding.btnEdit.setEnabled(false);
                    String selectedCountry = binding.spinnerCountry.getSelectedItem().toString();
                    binding.tvUseCountry.setText(selectedCountry);
                    if (!selectedCountry.isEmpty() &&
                            !binding.etUserName.getText().toString().isEmpty() &&
                            !binding.etUserEmail.getText().toString().isEmpty()
                    ) {
                        binding.etUserEmail.getText().toString();
                        updatedUserInfo.put(NAME_MAP_KEY, binding.etUserName.getText().toString());
                        updatedUserInfo.put(COUNTRY_MAP_KEY, selectedCountry);
                        updatedUserInfo.put(EMAIL_MAP_KEY, binding.etUserEmail.getText().toString());
                        if (selectedImageUri != null) {
                            uploadImageToCloudinary(requireContext(), selectedImageUri, new CloudinaryHelper.OnUploadCompleteListener() {
                                @Override
                                public void onSuccess(String imageUrl) {
                                    updatedUserInfo.put(IMAGE_MAP_KEY, imageUrl);
                                    profileViewModel.updateUserInf(userInfo.get(ID_MAP_KEY), updatedUserInfo, task -> {
                                        binding.ivAdd.setVisibility(GONE);
                                        binding.btnEdit.setVisibility(GONE);
                                        binding.ivEditProfile.setVisibility(VISIBLE);
                                        binding.spinnerCountry.setVisibility(GONE);
                                        binding.etUserEmail.setEnabled(false);
                                        binding.etUserName.setEnabled(false);
                                        if (task.isSuccessful())
                                            Toast.makeText(requireContext(), "Apply changes successfully", LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(requireContext(), "Try again", LENGTH_SHORT).show();
                                    });
                                }
                                @Override
                                public void onFailure(String error) {
                                    //Toast.makeText(requireContext(), "Image upload failed: " + error, LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else {
                        Toast.makeText(requireContext(), "There are empty fields", LENGTH_SHORT).show();
                    }
                });
            }
        });

        profileViewModel.getRecipesByUserId(recipes -> {
            declareRecyclerView(requireContext(), new RecipeAdapter(ProfileFragment.this, recipes), binding.rvRecipes, false);
        });

        binding.ivLogout.setOnClickListener(view -> {
            listener.onNavigateFragments(DIALOG_LOGOUT_TAG);
        });
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
    public void onLayoutClicked(String recipeId, String userId) {

    }

    @Override
    public void onVideoClicked(String videoLink) {

    }
}
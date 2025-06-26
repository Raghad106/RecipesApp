package com.ucas.firebaseminiproject.ui;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.ucas.firebaseminiproject.data.uplodeImage.CloudinaryHelper.uploadImageToCloudinary;
import static com.ucas.firebaseminiproject.utilities.Constance.COUNTRY_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.CURRENT_USER_TAG;
import static com.ucas.firebaseminiproject.utilities.Constance.DIALOG_LOGOUT_TAG;
import static com.ucas.firebaseminiproject.utilities.Constance.EMAIL_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.ID_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.IMAGE_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.NAME_MAP_KEY;
import static com.ucas.firebaseminiproject.utilities.Constance.USER_TAG;
import static com.ucas.firebaseminiproject.utilities.ViewsCustomListeners.declareRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.squareup.picasso.Picasso;
import com.ucas.firebaseminiproject.R;
import com.ucas.firebaseminiproject.data.uplodeImage.CloudinaryHelper;
import com.ucas.firebaseminiproject.data.viewmodels.ProfileViewModel;
import com.ucas.firebaseminiproject.databinding.FragmentProfileBinding;
import com.ucas.firebaseminiproject.ui.adapters.RecipeAdapter;
import com.ucas.firebaseminiproject.utilities.OnItemListener;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements OnItemListener.OnRecipeListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "param1";
    private static final String USER_ID = "param2";
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
    private String tag;
    private String userId;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnItemListener.OnFragmentListener) context;
    }

    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String tag, String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(TAG, tag);
        args.putString(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }
    public static ProfileFragment newInstance(String tag) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(TAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tag = getArguments().getString(TAG);
            userId = getArguments().getString(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        if (tag.equals(CURRENT_USER_TAG)){
            profileViewModel.getCurrentUserInfo(userInfo -> {
                if (userInfo != null && !userInfo.isEmpty()) {

                    profileViewModel.getRecipesByUserId(userInfo.get(ID_MAP_KEY),recipes -> {
                        declareRecyclerView(requireContext(), new RecipeAdapter(ProfileFragment.this, recipes), binding.rvRecipes, false);
                    });


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
                                        applyUserUpdate(userInfo.get(ID_MAP_KEY), updatedUserInfo);
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        Toast.makeText(requireContext(), "Image upload failed: " + error, LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                applyUserUpdate(userInfo.get(ID_MAP_KEY), updatedUserInfo); // <-- call update even if no image
                            }
                        }
                        });
                }
            });
        }
        else if (tag.equals(USER_TAG) && userId != null && !userId.isEmpty()){
            binding.ivLogout.setVisibility(GONE);
            binding.ivEditProfile.setVisibility(GONE);
            profileViewModel.getUserInfoById(userId,userInfo -> {
                if (userInfo != null && !userInfo.isEmpty()) {

                    profileViewModel.getRecipesByUserId(userInfo.get(ID_MAP_KEY),recipes -> {
                        declareRecyclerView(requireContext(), new RecipeAdapter(ProfileFragment.this, recipes), binding.rvRecipes, false);
                    });
                    binding.tvMyRecipes.setText(userInfo.get(NAME_MAP_KEY)+"'s " + "Recipes");
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
                }
            });
        }

        binding.ivLogout.setOnClickListener(view -> {
            listener.onNavigateFragments(DIALOG_LOGOUT_TAG);
        });
        return binding.getRoot();
    }

    private void applyUserUpdate(String userId, Map<String, String> updatedUserInfo) {
        profileViewModel.updateUserInfo(userId, updatedUserInfo, task -> {
            if (task.isSuccessful()) {
                binding.ivAdd.setVisibility(GONE);
                binding.btnEdit.setVisibility(GONE);
                binding.ivEditProfile.setVisibility(VISIBLE);
                binding.spinnerCountry.setVisibility(GONE);
                binding.etUserEmail.setEnabled(false);
                binding.etUserName.setEnabled(false);
                Toast.makeText(requireContext(), "Changes applied successfully", LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Failed to apply changes", LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onLayoutClicked(String recipeId, String userId) {
        listener.onNavigateRecipeDetailsFragment(recipeId, userId);
    }

    @Override
    public void onUserClicked(String userId) {

    }

    @Override
    public void onVideoClicked(String videoLink) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(videoLink));
        startActivity(intent);
    }
}
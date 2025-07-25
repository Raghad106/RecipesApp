package com.ucas.firebaseminiprojectcomplete.ui.adapters;

import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.ID_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.IMAGE_MAP_KEY;
import static com.ucas.firebaseminiprojectcomplete.utilities.Constance.NAME_MAP_KEY;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ucas.firebaseminiprojectcomplete.databinding.ItemUserBinding;
import com.ucas.firebaseminiprojectcomplete.utilities.OnItemListener;

import java.util.List;
import java.util.Map;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserHolder> {
    List<Map<String, String>> userInfo;
    OnItemListener.OnUserListener listener;

    public UserAdapter(List<Map<String, String>> userInfo, OnItemListener.OnUserListener listener) {
        this.userInfo = userInfo;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemUserBinding binding = ItemUserBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new UserHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder userHolder, int i) {
        int pos = i;
        Map<String, String> info = userInfo.get(pos);
        userHolder.userName.setText(String.valueOf(info.get(NAME_MAP_KEY)));
        if (info.get(IMAGE_MAP_KEY) != null && !info.get(IMAGE_MAP_KEY).isEmpty())
            Picasso.get().load(info.get(IMAGE_MAP_KEY)).into(userHolder.userImage);

        userHolder.layout.setOnClickListener(view -> {
            listener.onUserClicked(info.get(ID_MAP_KEY));
        });

    }

    @Override
    public int getItemCount() {
        return userInfo.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {
        ImageView userImage;
        TextView userName;
        ConstraintLayout layout;
        public UserHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            userImage = binding.ivProfileImage;
            userName = binding.tvUserName;
            layout = binding.layout;
        }
    }
}

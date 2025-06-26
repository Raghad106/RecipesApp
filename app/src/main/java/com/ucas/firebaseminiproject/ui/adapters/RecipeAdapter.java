package com.ucas.firebaseminiproject.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.ucas.firebaseminiproject.data.models.Recipe;
import com.ucas.firebaseminiproject.databinding.ItemRecipeBinding;
import com.ucas.firebaseminiproject.utilities.OnItemListener;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeHolder> {
    List<Recipe> recipes;
    OnItemListener.OnRecipeListener listener;

    public RecipeAdapter(OnItemListener.OnRecipeListener listener, List<Recipe> recipes) {
        this.listener = listener;
        this.recipes = recipes;
    }

    public void updateList(List<Recipe> newList) {
        this.recipes = newList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public RecipeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemRecipeBinding binding = ItemRecipeBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new RecipeHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeHolder recipeHolder, int i) {
        int pos = i;
        Recipe recipe = recipes.get(pos);
        recipeHolder.username.setText(recipe.getPublisherName());
        recipeHolder.description.setText(recipe.getTitle().toUpperCase());
        recipeHolder.likesCounts.setText("Loved By "+recipe.getLikesCount());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String formatted = sdf.format(recipe.getCreateAt());
        recipeHolder.time.setText(formatted);

        if (recipe.getPublisherImage() != null && !recipe.getPublisherImage().isEmpty())
            Picasso.get().load(recipe.getPublisherImage()).into(recipeHolder.accountImage);

        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty())
            Picasso.get().load(recipe.getImageUrl()).into(recipeHolder.recipeImage);

        // Reset all category views
        recipeHolder.category1.setText("");
        recipeHolder.category2.setText("");
        recipeHolder.category3.setText("");
        recipeHolder.category2.setVisibility(ViewGroup.GONE);
        recipeHolder.category3.setVisibility(ViewGroup.GONE);

// Set category views based on size
        List<String> categories = recipe.getCategories();
        if (categories != null && !categories.isEmpty()) {
            if (categories.size() >= 1) {
                recipeHolder.category1.setText(categories.get(0));
            }
            if (categories.size() >= 2) {
                recipeHolder.category2.setText(categories.get(1));
                recipeHolder.category2.setVisibility(ViewGroup.VISIBLE);
            }
            if (categories.size() >= 3) {
                recipeHolder.category3.setText(categories.get(2));
                recipeHolder.category3.setVisibility(ViewGroup.VISIBLE);
            }
        }

        recipeHolder.username.setOnClickListener(view -> {
            listener.onUserClicked(recipe.getPublisherId());
        });
        recipeHolder.playIcon.setOnClickListener(view -> {
            listener.onVideoClicked(recipe.getVideoUrl());
        });

        recipeHolder.layout.setOnClickListener(view -> {
            listener.onLayoutClicked(recipe.getRecipeId(), recipe.getPublisherId());
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }


    public class RecipeHolder extends RecyclerView.ViewHolder {
        ImageButton playIcon;
        CardView layout;
        ImageView accountImage, recipeImage;
        TextView username, description, category1, category2, category3, likesCounts, time;
        public RecipeHolder(@NonNull ItemRecipeBinding binding) {
            super(binding.getRoot());
            playIcon = binding.btnPlayVideo;
            accountImage = binding.ivUserAvatar;
            recipeImage = binding.ivRecipeImage;
            username = binding.tvUserName;
            description = binding.tvDescription;
            category1 = binding.tvCategory1;
            category2 = binding.tvCategory2;
            category3 = binding.tvCategory3;
            likesCounts = binding.tvLikesCount;
            time = binding.tvDuration;
            layout = binding.layout;
        }
    }
}

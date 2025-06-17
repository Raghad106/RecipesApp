package com.ucas.firebaseminiproject.ui.adapters;

import android.graphics.Color;
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

import java.util.List;

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
        recipeHolder.likesCounts.setText(String.valueOf(recipe.getLikesCount()));
        if (recipe.getPublisherImage() != null && !recipe.getPublisherImage().isEmpty())
            Picasso.get().load(recipe.getPublisherImage()).into(recipeHolder.accountImage);

        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty())
            Picasso.get().load(recipe.getImageUrl()).into(recipeHolder.recipeImage);

        if (recipe.getCategories().size() == 1){
            recipeHolder.category1.setText(recipe.getCategories().get(0));
        } else if (recipe.getCategories().size() == 2) {
            recipeHolder.category1.setText(recipe.getCategories().get(0));
            recipeHolder.category2.setText(recipe.getCategories().get(1));
            recipeHolder.category2.setVisibility(ViewGroup.VISIBLE);
        } else if (recipe.getCategories().size() == 3) {
            recipeHolder.category1.setText(recipe.getCategories().get(0));
            recipeHolder.category2.setText(recipe.getCategories().get(1));
            recipeHolder.category3.setText(recipe.getCategories().get(2));
            recipeHolder.category2.setVisibility(ViewGroup.VISIBLE);
            recipeHolder.category3.setVisibility(ViewGroup.VISIBLE);
        }

        if (listener.onLikeClicked(recipe.getRecipeId(), recipe.getPublisherId()))
            recipeHolder.likeIcon.setColorFilter(Color.parseColor("#FB912C"));
        else
           // recipeHolder.likeIcon.setColorFilter(Color.parseColor("#8D8D8D"));

        if (listener.onSaveClicked(recipe.getRecipeId(), recipe.getPublisherId()))
            recipeHolder.saveIcon.setColorFilter(Color.parseColor("#FB912C"));
        //else
           // recipeHolder.saveIcon.setColorFilter(Color.parseColor("8D8D8DFF"));

        recipeHolder.likeIcon.setOnClickListener(view -> {
            listener.onLikeClicked(recipe.getRecipeId(), recipe.getPublisherId());
        });

        recipeHolder.saveIcon.setOnClickListener(view -> {
            listener.onSaveClicked(recipe.getRecipeId(), recipe.getPublisherId());
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
        ImageButton playIcon, saveIcon;
        CardView layout;
        ImageView likeIcon, accountImage, recipeImage;
        TextView username, description, category1, category2, category3, likesCounts;
        public RecipeHolder(@NonNull ItemRecipeBinding binding) {
            super(binding.getRoot());
            playIcon = binding.btnPlayVideo;
            saveIcon = binding.btnSave;
            likeIcon = binding.ivLike;
            accountImage = binding.ivUserAvatar;
            recipeImage = binding.ivRecipeImage;
            username = binding.tvUserName;
            description = binding.tvDescription;
            category1 = binding.tvCategory1;
            category2 = binding.tvCategory2;
            category3 = binding.tvCategory3;
            likesCounts = binding.tvLikesCount;
            layout = binding.layout;
        }
    }
}

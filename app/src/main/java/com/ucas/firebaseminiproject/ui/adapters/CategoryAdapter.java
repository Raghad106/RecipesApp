package com.ucas.firebaseminiproject.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ucas.firebaseminiproject.R;
import com.ucas.firebaseminiproject.databinding.ItemCategoryBinding;
import com.ucas.firebaseminiproject.utilities.OnItemListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {
    private List<String> categories;
    private List<String> selectedCategories = new ArrayList<>();
    private OnItemListener.OnCategoryListener listener;
    private boolean isSelectionMode; // true = first list (select), false = second list (remove)

    public CategoryAdapter(List<String> categories, List<String> selectedCategories, OnItemListener.OnCategoryListener listener, boolean isSelectionMode) {
        this.categories = categories;
        this.selectedCategories = selectedCategories;
        this.listener = listener;
        this.isSelectionMode = isSelectionMode;
    }

    public List<String> getSelectedCategories() {
        return selectedCategories;
    }

    public void setSelectedCategories(List<String> selectedCategories) {
        this.selectedCategories = selectedCategories;
    }





    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new CategoryHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryHolder categoryHolder, int i) {
        int pos = i;
        String category = categories.get(pos);
        boolean isSelected = selectedCategories.contains(category);
        categoryHolder.name.setText(category);
        categoryHolder.name.setBackgroundResource(
                isSelected ? R.drawable.bg_circle_orange_selected : R.drawable.bg_circle_orange
        );

        categoryHolder.itemView.setOnClickListener(v -> {
            // Two lists are not same
            if (isSelectionMode) {
                // Select category
                if (selectedCategories.contains(category)) {
                    selectedCategories.remove(category);
                } else if (selectedCategories.size() < 3) {
                    selectedCategories.add(category);
                } else {
                    Toast.makeText(categoryHolder.itemView.getContext(), "You can select up to 3 categories", Toast.LENGTH_SHORT).show();
                }
                notifyDataSetChanged();
                listener.onCategoryClicked();
                // Tow lists are same
            } else {
                // Remove from selected list (adapter 2 case)
                if (selectedCategories != null){
                    selectedCategories.remove(category);
                    categories.remove(category); // remove from adapter data
                    notifyDataSetChanged();
                    listener.onCategoryClicked(); // to refresh main list if needed
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryHolder extends RecyclerView.ViewHolder{
        TextView name;
        ConstraintLayout layout;
        public CategoryHolder(ItemCategoryBinding binding) {
            super(binding.getRoot());
            name = binding.tvCategory;
            layout = binding.categoryCard;
        }
    }
}

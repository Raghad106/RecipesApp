package com.ucas.firebaseminiprojectcomplete.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragmentList;

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.fragmentList = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position >= 0 && position < fragmentList.size()) {
            return fragmentList.get(position);
        } else {
            throw new IndexOutOfBoundsException("Invalid fragment index: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    /**
     * Add new fragments to the adapter and notify range inserted (for pagination).
     */
    public void addFragments(@NonNull List<Fragment> newFragments) {
        int oldSize = fragmentList.size();
        fragmentList.addAll(newFragments);
        notifyItemRangeInserted(oldSize, newFragments.size());
    }

    /**
     * Replace all fragments with a new list and notify full data set change.
     */
    public void setFragments(@NonNull List<Fragment> newFragments) {
        fragmentList.clear();
        fragmentList.addAll(newFragments);
        notifyDataSetChanged();
    }
}

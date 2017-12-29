package com.eugene.fithealth.search;


import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.eugene.fithealth.databinding.RecyclerFatSecretItemBinding;
import com.eugene.fithealth.model.Food;

import java.util.ArrayList;
import java.util.List;

public class FatSecretRecyclerAdapter extends RecyclerView.Adapter<FatSecretRecyclerAdapter.FatSecretViewHolder> {
    private List<Food> itemList = new ArrayList<>();

    void setItems(final List<Food> items) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return itemList.size();
            }

            @Override
            public int getNewListSize() {
                return items.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                Food newItem = items.get(newItemPosition);
                Food oldItem = itemList.get(oldItemPosition);
                return oldItem.getFoodId().equals(newItem.getFoodId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Food oldItem = itemList.get(oldItemPosition);
                Food newItem = items.get(newItemPosition);
                return oldItem.getFoodId().equals(newItem.getFoodId()) && newItem.getFoodDescription().equals(oldItem.getFoodDescription());
            }
        });
        this.itemList.clear();
        this.itemList.addAll(items);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public FatSecretViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FatSecretViewHolder(RecyclerFatSecretItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(FatSecretViewHolder holder, int position) {
        holder.bindItem(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class FatSecretViewHolder extends RecyclerView.ViewHolder {
        RecyclerFatSecretItemBinding binding;

        FatSecretViewHolder(RecyclerFatSecretItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindItem(Food food) {
            binding.setObject(food);
        }
    }
}

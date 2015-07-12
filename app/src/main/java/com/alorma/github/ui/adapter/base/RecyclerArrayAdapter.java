package com.alorma.github.ui.adapter.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Bernat on 12/07/2015.
 */
public abstract class RecyclerArrayAdapter<ItemType, ViewHolder extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<ViewHolder> {

    public List<ItemType> items;
    private boolean paging;
    private LayoutInflater inflater;

    public RecyclerArrayAdapter(LayoutInflater inflater) {
        this.inflater = inflater;
        items = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ItemType getItem(int position) {
        return items.get(position);
    }

    public void add(ItemType itemType) {
        items.add(itemType);
        notifyDataSetChanged();
    }

    public void addAll(Collection<ItemType> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void addAll(Collection<ItemType> items, boolean paging) {
        this.paging = paging;
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public void setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }
}

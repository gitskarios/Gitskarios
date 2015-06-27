package com.alorma.github.ui.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.bean.ProfileItem;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernat on 26/06/2015.
 */
public class ProfileItemsAdapter extends RecyclerView.Adapter<ProfileItemsAdapter.Holder> {

    private final LayoutInflater mInflater;
    private List<ProfileItem> items;
    private int avatarColor;

    public ProfileItemsAdapter(Context context) {
        items = new ArrayList<>();
        this.mInflater = LayoutInflater.from(context);
        avatarColor = context.getResources().getColor(R.color.accent);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(mInflater.inflate(R.layout.profil_item_row, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        ProfileItem profileItem = items.get(position);

        holder.icon.setImageDrawable(drawable(holder.icon.getContext(), profileItem.icon));
        holder.text.setText(profileItem.value);
        holder.intent(profileItem.intent);
    }

    private IconicsDrawable drawable(Context context, IIcon icon) {
        IconicsDrawable githubIconDrawable = new IconicsDrawable(context, icon);

        githubIconDrawable.sizeDp(30);
        githubIconDrawable.color(avatarColor);

        return githubIconDrawable;
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void remove(ProfileItem profileItem) {
        items.remove(profileItem);
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView text;

        public Holder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            text = (TextView) itemView.findViewById(R.id.text);
        }

        public void intent(final Intent intent) {
            if (intent != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            v.getContext().startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                itemView.setOnClickListener(null);
                itemView.setClickable(false);
            }
        }
    }

    public void add(ProfileItem profileItem) {
        items.add(profileItem);
        notifyDataSetChanged();
    }

    public void addAll(List<ProfileItem> profileItems) {
        items.addAll(profileItems);
        notifyDataSetChanged();
    }

    public void setAvatarColor(int avatarColor) {
        this.avatarColor = avatarColor;
        notifyDataSetChanged();
    }

}

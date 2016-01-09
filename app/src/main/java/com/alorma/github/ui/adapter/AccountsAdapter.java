package com.alorma.github.ui.adapter;

import android.accounts.Account;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.AccountsHelper;
import com.alorma.github.R;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Bernat on 06/04/2015.
 */
public class AccountsAdapter extends RecyclerView.Adapter<AccountsAdapter.Holder> {

    private final LayoutInflater mInflater;
    private Context context;
    private Account[] accounts;

    public AccountsAdapter(Context context, Account[] accounts) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.accounts = accounts;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(mInflater.inflate(R.layout.material_drawer_item_profile, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Account account = accounts[position];

        String userAvatar = AccountsHelper.getUserAvatar(context, account);
        String userName = AccountsHelper.getUserName(context, account);
        String userMail = AccountsHelper.getUserMail(context, account);
        ImageLoader.getInstance().displayImage(userAvatar, holder.profileIcon);

        holder.name.setText(userName);
        holder.email.setText(userMail);
    }

    @Override
    public int getItemCount() {
        return accounts != null ? accounts.length : 0;
    }

    public class Holder extends RecyclerView.ViewHolder {
        private ImageView profileIcon;
        private TextView name;
        private TextView email;

        public Holder(View itemView) {
            super(itemView);
            this.profileIcon = (ImageView) itemView.findViewById(R.id.material_drawer_profileIcon);
            this.name = (TextView) itemView.findViewById(R.id.material_drawer_name);
            this.email = (TextView) itemView.findViewById(R.id.material_drawer_email);
        }
    }
}

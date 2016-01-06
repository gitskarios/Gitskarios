package com.alorma.github.ui.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

/**
 * Created by Bernat on 09/05/2015.
 */
public class GitskariosProfileDrawerItem extends ProfileDrawerItem {

  @Override
  public void bindView(RecyclerView.ViewHolder holder) {
    super.bindView(holder);

    TextView text = (TextView) holder.itemView.findViewById(com.mikepenz.materialdrawer.R.id.material_drawer_name);
    TextView mail = (TextView) holder.itemView.findViewById(com.mikepenz.materialdrawer.R.id.material_drawer_email);

    if (text != null && mail != null) {
      text.setVisibility(View.VISIBLE);
      mail.setVisibility(View.VISIBLE);

      this.getName().applyTo(text);
      this.getEmail().applyTo(mail);
    }
  }
}

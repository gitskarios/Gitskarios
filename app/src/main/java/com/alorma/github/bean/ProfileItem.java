package com.alorma.github.bean;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

/**
 * Created by Bernat on 26/06/2015.
 */
public class ProfileItem {
  public IIcon icon;
  public String value;
  public Intent intent;

  public ProfileItem(IIcon icon, String value, Intent intent) {
    this.icon = icon;
    this.value = value;
    this.intent = intent;
  }

  public View getView(Context context, ViewGroup parent) {
    View v = LayoutInflater.from(context).inflate(R.layout.row_user_resume, parent, false);

    ImageView i = (ImageView) v.findViewById(R.id.image);
    TextView t = (TextView) v.findViewById(R.id.text);

    Drawable drawable = new IconicsDrawable(context).icon(icon).sizeRes(R.dimen.gapLarge).paddingDp(4);
    i.setImageDrawable(drawable);

    t.setText(value);

    if (intent != null) {
      v.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (intent != null) {
            v.getContext().startActivity(intent);
          }
        }
      });
    }

    return v;
  }
}

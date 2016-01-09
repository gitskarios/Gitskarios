package com.alorma.github.bean;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alorma.github.R;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.view.IconicsImageView;

/**
 * Created by Bernat on 26/06/2015.
 */
public class ProfileItem {
    public int id;
    public IIcon icon;
    public String value;
    public Intent intent;
    @ColorInt
    public int color = Color.BLACK;
    private Callback callback;
    private View v;
    private IconicsImageView i;
    private TextView t;

    public ProfileItem(IIcon icon, String value, Intent intent) {
        this.icon = icon;
        this.value = value;
        this.intent = intent;
    }

    public void setId(int id) {
        this.id = id;
    }


    public View getView(Context context, ViewGroup parent) {
        v = LayoutInflater.from(context).inflate(R.layout.row_user_resume, parent, false);

        i = (IconicsImageView) v.findViewById(R.id.image);
        t = (TextView) v.findViewById(R.id.text);

        i.setIcon(icon);
        i.setPaddingDp(16);
        i.setColor(color);
        t.setText(value);

        if (intent != null || callback != null) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (intent != null) {
                        v.getContext().startActivity(intent);
                    } else if (callback != null) {
                        callback.onSelected(id);
                    }
                }
            });
        }

        return v;
    }

    public void updateColor(@ColorInt int color) {
        this.color = color;
        if (i != null) {
            i.setColor(color);
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onSelected(int id);
    }
}

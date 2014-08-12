package com.alorma.github.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BaseActivity;
import com.alorma.github.ui.utils.UniversalImageLoaderUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MainActivity extends BaseActivity {

    public static void startActivity(Activity context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageLoader.getInstance().init(UniversalImageLoaderUtils.getImageLoaderConfiguration(this));
    }
}

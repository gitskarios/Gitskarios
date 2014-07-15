package com.alorma.github.ui.fragment.navigation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.PaletteItem;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CircularImageView;
import android.widget.EnhancedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.user.RequestAutenticatedUserClient;
import com.alorma.github.sdk.services.user.RequestUserClient;
import com.alorma.github.ui.utils.PaletteUtils;
import com.joanzapata.android.iconify.Iconify;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 12/07/2014.
 */
public class MainNavigationFragment extends NavigationDrawerFragment implements AdapterView.OnItemClickListener, BaseClient.OnResultCallback<User>, Palette.PaletteAsyncListener {

    private ListView mDrawerListView;

    private int mCurrentSelectedPosition = 0;
    private String[] items;
    private CircularImageView circularImage;
    private View profileLy;
    private EnhancedTextView joinedText;
    private TextView nameText;

    public static MainNavigationFragment newInstance() {
        return new MainNavigationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_navigation_drawer, null, false);
        profileLy = v.findViewById(R.id.profileLy);
        mDrawerListView = (ListView) v.findViewById(R.id.list);
        circularImage = (CircularImageView) v.findViewById(R.id.circular);
        nameText = (TextView) v.findViewById(R.id.name);
        joinedText = (EnhancedTextView) v.findViewById(R.id.joined);
        mDrawerListView.setOnItemClickListener(this);

        items = new String[]{
                getString(R.string.navigation_gists),
                "JakeWharton"
        };

        ArrayAdapter adapter = new ArrayAdapter<String>(
                getThemedContext(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                items
        );
        mDrawerListView.setAdapter(adapter);

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        selectItem(1);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        RequestAutenticatedUserClient client = new RequestAutenticatedUserClient(activity);
        client.setOnResultCallback(this);
        client.execute();
    }

    @Override
    protected void setItemSelected(int position) {
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }

    @Override
    public void onResponseOk(User user, Response r) {
        if (user != null) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.loadImage(user.avatar_url, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    circularImage.setImageBitmap(loadedImage);
                    Palette.generateAsync(loadedImage, MainNavigationFragment.this);
                }
            });

            if (user.login != null) {
                nameText.setText(user.login);
            }

            if (user.created_at != null) {
                CharSequence format = DateFormat.format("MMM dd, yyyy", user.created_at);

                joinedText.setText(format);
                joinedText.setPrefixIcon(Iconify.IconValue.fa_clock_o);
            } else {
                joinedText.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onFail(RetrofitError error) {

    }

    @Override
    public void onGenerated(Palette palette) {
        if (palette != null) {
            PaletteItem item = PaletteUtils.getPaletteItem(palette);
            if (item != null){
                profileLy.setBackgroundColor(item.getRgb());
            }
        }
    }
}

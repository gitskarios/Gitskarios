package com.alorma.github.ui.fragment;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.PaletteItem;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alorma.github.GistsApplication;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.client.BaseClient;
import com.alorma.github.sdk.services.user.RequestUserClient;
import com.alorma.github.ui.fragment.navigation.NavigatedFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import retrofit.RetrofitError;

/**
 * Created by Bernat on 12/07/2014.
 */
public class ProfileFragment extends NavigatedFragment implements BaseClient.OnResultCallback<User>, Target, Palette.PaletteAsyncListener, ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {
    private static final String USERNAME = "USERNAME";
    private User user;
    private ImageView avatarImage;
    private TextView userText;
    private int rgbAbColor;
    private int i = 0;
    private String[] names = {"alorma", "octocats", "JakeWharton", "gabrielemariotti", "kix2902"};

    public static ProfileFragment newInstance(String username) {
        Bundle bundle = new Bundle();
        bundle.putString(USERNAME, username);
        ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.setArguments(bundle);
        return profileFragment;
    }

    @Override
    protected int getTitle() {
        return R.string.title_profile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String username = getArguments().getString(USERNAME);
            RequestUserClient requestUserClient = new RequestUserClient(getActivity(), names[i++]);
            requestUserClient.setOnResultCallback(this);
            requestUserClient.execute();
        } else {
            Toast.makeText(getActivity(), "No username provided", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.profile_fragment, null, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        avatarImage = (ImageView) view.findViewById(R.id.imageView);
        userText = (TextView) view.findViewById(R.id.user);

        updateData();
    }

    @Override
    public void onResponseOk(User user) {
        this.user = user;
        updateData();
    }

    private void updateData() {
        if (isAdded() && user != null) {
            Picasso.with(getActivity()).load(user.getAvatarUrl()).into(this);
            userText.setText(user.getLogin());
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        Palette.generateAsync(bitmap, this);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }

    @Override
    public void onGenerated(Palette palette) {
        setUpFromPalette(palette);
    }

    public void setUpFromPalette(Palette palette) {
        if (palette != null) {
            PaletteItem vibrantColor = palette.getVibrantColor();
            PaletteItem lightVibrantColor = palette.getLightVibrantColor();
            PaletteItem darkVibrantColor = palette.getDarkVibrantColor();
            PaletteItem mutedColor = palette.getMutedColor();
            PaletteItem lightMutedColor = palette.getLightMutedColor();
            PaletteItem darkMutedColor = palette.getDarkMutedColor();
            if (vibrantColor != null) {
                setUpFromPaletteItem(vibrantColor);
                Log.i("PALETTE", "vibrantColor");
            } else if (lightVibrantColor != null) {
                setUpFromPaletteItem(lightVibrantColor);
                Log.i("PALETTE", "lightVibrantColor");
            }else if (darkVibrantColor != null) {
                setUpFromPaletteItem(darkVibrantColor);
                Log.i("PALETTE", "darkVibrantColor");
            }else if (darkMutedColor != null) {
                setUpFromPaletteItem(darkMutedColor);
                Log.i("PALETTE", "darkMutedColor");
            }else if (lightMutedColor != null) {
                setUpFromPaletteItem(lightMutedColor);
                Log.i("PALETTE", "lightMutedColor");
            }else if (mutedColor != null) {
                setUpFromPaletteItem(mutedColor);
                Log.i("PALETTE", "mutedColor");
            }
        }
    }

    private void setUpFromPaletteItem(PaletteItem paletteItem) {
        if (paletteItem != null) {
            int rgb = paletteItem.getRgb();
            if (getActivity().getActionBar() != null) {
                animateChange(rgb);
            }
        }
    }

    private void animateChange(int rgb) {
        this.rgbAbColor = rgb;
        ValueAnimator animator = ValueAnimator.ofObject(new ArgbEvaluator(), GistsApplication.AB_COLOR, rgb);
        animator.addUpdateListener(this);
        animator.setDuration(300);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(this);
        animator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        ColorDrawable cd = new ColorDrawable((Integer) valueAnimator.getAnimatedValue());
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setBackgroundDrawable(cd);
        }
    }

    @Override
    public void onAnimationStart(Animator animator) {

    }

    @Override
    public void onAnimationEnd(Animator animator) {
        GistsApplication.AB_COLOR = rgbAbColor;

        if (i < names.length) {
            RequestUserClient requestUserClient = new RequestUserClient(getActivity(), names[i++]);
            requestUserClient.setOnResultCallback(this);
            requestUserClient.execute();
        }

        Picasso.with(getActivity()).load(user.getAvatarUrl()).into(avatarImage);
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }
}

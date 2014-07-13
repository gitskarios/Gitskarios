package com.alorma.github.ui.fragment;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.PaletteItem;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumericTitle;
import android.widget.TextView;
import android.widget.Toast;

import com.alorma.github.GistsApplication;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.user.BaseUsersClient;
import com.alorma.github.sdk.services.user.RequestAutenticatedUserClient;
import com.alorma.github.sdk.services.user.RequestUserClient;
import com.alorma.github.ui.fragment.navigation.NavigatedFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

public class ProfileFragment extends NavigatedFragment implements BaseClient.OnResultCallback<User>, Target, Palette.PaletteAsyncListener, ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener, View.OnClickListener {
    private static final String USERNAME = "USERNAME";
    private static final long DURATION = 300;
    private User user;
    private ImageView avatarImage;
    private TextView userText;
    private int rgbAbColor;
    private Bitmap avatartBitmap;
    private View content1;
    private TextView typeText;
    private NumericTitle num1Text;
    private NumericTitle num2Text;
    private NumericTitle num3Text;
    private NumericTitle num4Text;
    private LinearLayout content2;
    private TextView joinedText;
    private String username;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

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

        BaseUsersClient<User> requestClient;
        if (getArguments() != null) {
            username = getArguments().getString(USERNAME);
            requestClient = new RequestUserClient(getActivity(), username);
        } else {
            requestClient = new RequestAutenticatedUserClient(getActivity());
        }
        requestClient.setOnResultCallback(this);
        requestClient.execute();
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
        typeText = (TextView) view.findViewById(R.id.type);
        joinedText = (TextView) view.findViewById(R.id.joined);

        num1Text = (NumericTitle) view.findViewById(R.id.num1);
        num2Text = (NumericTitle) view.findViewById(R.id.num2);
        num3Text = (NumericTitle) view.findViewById(R.id.num3);
        num4Text = (NumericTitle) view.findViewById(R.id.num4);

        num1Text.setOnClickListener(this);
        num2Text.setOnClickListener(this);
        num3Text.setOnClickListener(this);
        num4Text.setOnClickListener(this);

        content1 = view.findViewById(R.id.content1);
        content2 = (LinearLayout) view.findViewById(R.id.content2);

        content1.setBackgroundColor(getResources().getColor(R.color.gray_github));
        content2.setBackgroundColor(getResources().getColor(R.color.gray_github));

        if (username == null) {
            Fragment fragment = UserPublicGistsFragment.newInstance();
            replaceContent(fragment);
        } else {
            Fragment fragment = UserPublicGistsFragment.newInstance(username);
            replaceContent(fragment);
        }
    }

    private void replaceContent(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.content3, fragment);
            ft.commit();
        }
    }

    @Override
    public void onResponseOk(User user, Response r) {
        this.user = user;
        updateData();
    }

    private void updateData() {
        if (isAdded() && user != null) {
            Picasso.with(getActivity()).load(user.avatar_url).into(this);
            if (getActivity().getActionBar() != null) {
                getActivity().getActionBar().setTitle(user.login);
            }
            userText.setText(user.name);
            typeText.setText(user.type.toString());

            CharSequence format = DateFormat.format("MMM dd, yyyy", user.created_at);

            joinedText.setText("Joined on " + format);

            updateNums();
        }
    }

    private void updateNums() {
        num1Text.setCustomNumber(user.public_repos);
        num1Text.setCustomText(R.string.public_repos);
        num2Text.setCustomNumber(user.public_gists);
        num2Text.setCustomText(R.string.public_gists);
        num3Text.setCustomNumber(user.followers);
        num3Text.setCustomText(R.string.followers);
        num4Text.setCustomNumber(user.following);
        num4Text.setCustomText(R.string.following);
    }

    @Override
    public void onFail(RetrofitError error) {
        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        Palette.generateAsync(bitmap, this);
        this.avatartBitmap = bitmap;

        PropertyValuesHolder ph = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(avatarImage, ph);
        animator.setDuration(DURATION);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ColorDrawable cd = new ColorDrawable(getResources().getColor(R.color.gray_github_dark));
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setBackgroundDrawable(cd);
            GistsApplication.AB_COLOR = getResources().getColor(R.color.gray_github_dark);
        }
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
            } else if (darkVibrantColor != null) {
                setUpFromPaletteItem(darkVibrantColor);
            } else if (lightVibrantColor != null) {
                setUpFromPaletteItem(lightVibrantColor);
                content1.setBackgroundColor(lightVibrantColor.getRgb());
            } else if (darkMutedColor != null) {
                setUpFromPaletteItem(darkMutedColor);
            } else if (lightMutedColor != null) {
                setUpFromPaletteItem(lightMutedColor);
                content1.setBackgroundColor(lightMutedColor.getRgb());
            } else if (mutedColor != null) {
                setUpFromPaletteItem(mutedColor);
            }

            /*if (lightMutedColor != null) {
                content1.setBackgroundColor(lightMutedColor.getRgb());
                content2.setBackgroundColor(lightMutedColor.getRgb());
            } else if (lightVibrantColor != null) {
                content1.setBackgroundColor(lightVibrantColor.getRgb());
                content2.setBackgroundColor(lightVibrantColor.getRgb());
            }*/

            aphaImage(avatartBitmap);
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
        animator.setDuration(DURATION);
        animator.setInterpolator(new LinearInterpolator());
        animator.addListener(this);
        animator.start();
    }

    private void aphaImage(Bitmap bitmap) {
        avatarImage.setAlpha(0f);
        avatarImage.setImageBitmap(bitmap);
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
    }

    @Override
    public void onAnimationCancel(Animator animator) {

    }

    @Override
    public void onAnimationRepeat(Animator animator) {

    }

    @Override
    public void onClick(View view) {
        Fragment fragment = null;
        switch (view.getId()) {
            case R.id.num1:

                break;
            case R.id.num2:
                fragment = UserPublicGistsFragment.newInstance(username);
                break;
            case R.id.num3:

                break;
            case R.id.num4:

                break;
        }

        replaceContent(fragment);
    }
}

package com.alorma.github.ui.fragment;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.PaletteItem;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.CircularImageView;
import android.widget.EnhancedTextView;
import android.widget.NumericTitle;

import com.alorma.github.GistsApplication;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.user.BaseUsersClient;
import com.alorma.github.sdk.services.user.RequestAutenticatedUserClient;
import com.alorma.github.sdk.services.user.RequestUserClient;
import com.alorma.github.ui.utils.PaletteUtils;
import com.joanzapata.android.iconify.Iconify;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProfileFragment extends Fragment implements BaseClient.OnResultCallback<User>, Palette.PaletteAsyncListener,
        ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener, View.OnClickListener {
    public static final String USERNAME = "USERNAME";
    private static final long DURATION = 300;
    private User user;
    private int rgbAbColor;
    private NumericTitle num1Text;
    private NumericTitle num2Text;
    private NumericTitle num3Text;
    private NumericTitle num4Text;
    private String username;
    private CircularImageView avatarImage;
    private EnhancedTextView mailText;
    private EnhancedTextView blogText;
    private EnhancedTextView joinedText;
    private PaletteItem usedPalette;

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

        avatarImage = (CircularImageView) view.findViewById(R.id.imageView);
        avatarImage.setBackgroundColor(getResources().getColor(R.color.gray_github));

        mailText = (EnhancedTextView) view.findViewById(R.id.mail);
        blogText = (EnhancedTextView) view.findViewById(R.id.blog);
        joinedText = (EnhancedTextView) view.findViewById(R.id.joined);

        mailText.setOnClickListener(this);
        blogText.setOnClickListener(this);

        num1Text = (NumericTitle) view.findViewById(R.id.num1);
        num2Text = (NumericTitle) view.findViewById(R.id.num2);
        num3Text = (NumericTitle) view.findViewById(R.id.num3);
        num4Text = (NumericTitle) view.findViewById(R.id.num4);

        num1Text.setOnClickListener(this);
        num2Text.setOnClickListener(this);
        num3Text.setOnClickListener(this);
        num4Text.setOnClickListener(this);

        num1Text.setSelected(true);
        if (username == null) {
            Fragment fragment = ReposFragment.newInstance();
            replaceContent(fragment);
        } else {
            Fragment fragment = ReposFragment.newInstance(username);
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
            if (getActivity().getActionBar() != null) {
                getActivity().getActionBar().setTitle(user.login);
            }

            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.loadImage(user.avatar_url, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    avatarImage.setImageBitmap(loadedImage);
                    Palette.generateAsync(loadedImage, ProfileFragment.this);
                }
            });

            if (user.name != null) {
                if (getActivity().getActionBar() != null) {
                    getActivity().getActionBar().setSubtitle(user.name);
                }
            }

            if (user.email != null) {
                mailText.setText(user.email);
                mailText.setPrefixIcon(Iconify.IconValue.fa_envelope_o);
            } else {
                mailText.setVisibility(View.GONE);
            }

            if (user.blog != null) {
                blogText.setText(user.blog);
                blogText.setPrefixIcon(Iconify.IconValue.fa_link);
            } else {
                blogText.setVisibility(View.GONE);
            }

            if (user.created_at != null) {
                CharSequence format = DateFormat.format("MMM dd, yyyy", user.created_at);

                joinedText.setText("Joined on " + format);
                joinedText.setPrefixIcon(Iconify.IconValue.fa_clock_o);
            } else {
                joinedText.setVisibility(View.GONE);
            }

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
    public void onGenerated(Palette palette) {
        setUpFromPalette(palette);
    }

    public void setUpFromPalette(Palette palette) {
        if (palette != null) {
            PaletteItem item = PaletteUtils.getPaletteItem(palette);

            if (item != null) {
                setUpFromPaletteItem(item);
            }
        }
    }

    private void setUpFromPaletteItem(PaletteItem paletteItem) {
        this.usedPalette = paletteItem;
        int rgb = paletteItem.getRgb();
        if (getActivity().getActionBar() != null) {
            animateChange(rgb);
        }

        avatarImage.setBorderColor(rgb);
        selectButton(null);

        mailText.setPrefixColor(rgb);
        blogText.setPrefixColor(rgb);
        joinedText.setPrefixColor(rgb);
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
                fragment = ReposFragment.newInstance(username);
                selectButton(num1Text);
                break;
            case R.id.num2:
                fragment = GistsFragment.newInstance(username);
                selectButton(num2Text);
                break;
            case R.id.num3:
                fragment = FollowersFragment.newInstance(username);
                selectButton(num3Text);
                break;
            case R.id.num4:
                fragment = FollowingFragment.newInstance(username);
                selectButton(num4Text);
                break;
            case R.id.mail:
                if (user.email != null) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + user.email));
                    startActivity(Intent.createChooser(intent, "Send Email..."));
                }
                break;
            case R.id.blog:
                if (user.blog != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(user.blog), "text/html");
                    startActivity(Intent.createChooser(intent, "Open with..."));
                }
                break;
        }

        replaceContent(fragment);
    }

    private void selectButton(NumericTitle numText) {
        List<NumericTitle> numericTitles = new ArrayList<NumericTitle>();
        numericTitles.add(num1Text);
        numericTitles.add(num2Text);
        numericTitles.add(num3Text);
        numericTitles.add(num4Text);

        for (NumericTitle numericTitle : numericTitles) {
            if (numText != null) {
                numericTitle.setSelected(numericTitle == numText);
            }

            if (usedPalette != null) {
                numericTitle.setRgb(usedPalette.getRgb());
            }
        }
    }
}

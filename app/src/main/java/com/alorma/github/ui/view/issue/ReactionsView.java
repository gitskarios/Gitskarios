package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.GithubCommentReactions;
import com.alorma.github.sdk.bean.dto.response.GithubReaction;

import java.util.ArrayList;
import java.util.List;
public class ReactionsView extends LinearLayout {


    private TextView[] reactions;

    public ReactionsView(Context context) {
        super(context);
        init();
    }

    public ReactionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReactionsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ReactionsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.reactions_layout, this);
        setOrientation(HORIZONTAL);

        TextView reaction1 = (TextView) findViewById(R.id.reaction1);
        TextView reaction2 = (TextView) findViewById(R.id.reaction2);
        TextView reaction3  = (TextView) findViewById(R.id.reaction3);
        TextView reaction4 = (TextView) findViewById(R.id.reaction4);
        TextView reaction5 = (TextView) findViewById(R.id.reaction5);
        TextView reaction6 = (TextView) findViewById(R.id.reaction6);

        reactions = new TextView[]{reaction1, reaction2, reaction3
                , reaction4, reaction5, reaction6};
    }

    public void setReactions(GithubCommentReactions reactions) {
        if (reactions != null) {
            List<GithubReaction> reactionList = new ArrayList<>();

            if (reactions.getPlusOne() > 0) {
                reactionList.add(new GithubReaction("plusOne", reactions.getPlusOne()));
            }
            if (reactions.getMinusOne() > 0) {
                reactionList.add(new GithubReaction("minusOne", reactions.getMinusOne()));
            }
            if (reactions.getConfused() > 0) {
                reactionList.add(new GithubReaction("confused", reactions.getConfused()));
            }
            if (reactions.getLaugh() > 0) {
                reactionList.add(new GithubReaction("laugh", reactions.getLaugh()));
            }
            if (reactions.getHooray() > 0) {
                reactionList.add(new GithubReaction("hooray", reactions.getHooray()));
            }
            if (reactions.getHeart() > 0) {
                reactionList.add(new GithubReaction("heart", reactions.getHeart()));
            }

            int min = Math.min(this.reactions.length, reactionList.size());
            for (int i = 0; i < min; i++) {
                TextView reaction = this.reactions[i];

                GithubReaction githubReaction = reactionList.get(i);

                int icon = getIconForReaction(githubReaction);

                String text = String.valueOf(githubReaction.getValue());
                if (icon != 0) {
                    text = getEmijoByUnicode(icon) + " " + text;
                }

                reaction.setText(text);
            }
        }
    }

    public String getEmijoByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    private int getIconForReaction(GithubReaction githubReaction) {
        if (githubReaction.getContent().equals("plusOne")) {
            return 0x1F44D;
        } else if (githubReaction.getContent().equals("minusOne")) {
            return 0x1F44E;
        } else if (githubReaction.getContent().equals("confused")) {
            return 0x1F615;
        } else if (githubReaction.getContent().equals("laugh")) {
            return 0x1F604;
        } else if (githubReaction.getContent().equals("hooray")) {
            return 0x1F389;
        } else if (githubReaction.getContent().equals("heart")) {
            return 0x2764;
        }
        return 0;
    }
}

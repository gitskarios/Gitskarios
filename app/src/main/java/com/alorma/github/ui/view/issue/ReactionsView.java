package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alorma.github.R;
import core.GithubCommentReactions;
import core.GithubReaction;
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
    TextView reaction3 = (TextView) findViewById(R.id.reaction3);
    TextView reaction4 = (TextView) findViewById(R.id.reaction4);
    TextView reaction5 = (TextView) findViewById(R.id.reaction5);
    TextView reaction6 = (TextView) findViewById(R.id.reaction6);

    reactions = new TextView[] {
        reaction1, reaction2, reaction3, reaction4, reaction5, reaction6
    };
  }

  public void setReactions(GithubCommentReactions reactions) {
    if (reactions != null && reactions.getTotalCount() > 0) {
      setVisibility(View.VISIBLE);
      for (TextView reaction : this.reactions) {
        reaction.setVisibility(View.INVISIBLE);
      }
      List<GithubReaction> reactionList = reactions.getReactions();
      int min = Math.min(this.reactions.length, reactionList.size());
      for (int i = 0; i < min; i++) {
        TextView reaction = this.reactions[i];
        reaction.setVisibility(View.VISIBLE);
        reaction.setText(reactionList.get(i).toString());
      }
    } else {
      setVisibility(View.GONE);
    }
  }
}

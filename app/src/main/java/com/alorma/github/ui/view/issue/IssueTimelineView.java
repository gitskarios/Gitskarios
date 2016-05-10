package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.sdk.bean.issue.IssueStoryEvent;
import com.alorma.github.sdk.bean.issue.Rename;
import com.alorma.github.sdk.core.ShaUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialize.color.Material;
import com.mikepenz.octicons_typeface_library.Octicons;
import tk.zielony.naturaldateformat.NaturalDateFormat;
import tk.zielony.naturaldateformat.RelativeDateFormat;

public class IssueTimelineView extends TextView {

  private static final int ICON_ROUNDED_CORNER_DP = 16;
  private static final int ICON_SIZE = 30;
  private static final int ICON_PADDING = 6;

  public IssueTimelineView(Context context) {
    super(context);
    init();
  }

  public IssueTimelineView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public IssueTimelineView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public IssueTimelineView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    int padding = getResources().getDimensionPixelOffset(R.dimen.materialize_baseline_grid);
    setPadding(padding * 2, padding, padding, padding);
    setCompoundDrawablePadding(padding);
    setGravity(Gravity.CENTER_VERTICAL);
  }

  public void setIssueEvent(IssueStoryEvent issueEvent) {
    long milis = System.currentTimeMillis();
    //applyGenericIssueStory(issueEvent);

    setText("");

    String actor = issueEvent.user().login;

    String eventType = issueEvent.event.event;
    if (eventType.equals("assigned")) {
      setIcon(Octicons.Icon.oct_person);
      if (actor.equalsIgnoreCase(issueEvent.event.assignee.login)) {
        setText(getResources().getString(R.string.issue_self_assigned, actor));
      } else {
        setText(getResources().getString(R.string.issue_assigned, actor,
            issueEvent.event.assignee.login));
      }
    } else if (eventType.equals("unassigned")) {
      setIcon(Octicons.Icon.oct_person);
      if (actor.equalsIgnoreCase(issueEvent.event.assignee.login)) {
        setText(getResources().getString(R.string.issue_self_unassigned, actor));
      } else {
        setText(getResources().getString(R.string.issue_unassigned, actor,
            issueEvent.event.assignee.login));
      }
    } else if (eventType.equals("milestoned")) {
      setIcon(Octicons.Icon.oct_milestone);
      String text = getResources().getString(R.string.issue_milestoned, actor,
          issueEvent.event.milestone.title);
      setText(text);
    } else if (eventType.equals("demilestoned")) {
      setIcon(Octicons.Icon.oct_milestone);
      String text = getResources().getString(R.string.issue_demilestoned, actor,
          issueEvent.event.milestone.title);
      setText(text);
    } else if (eventType.equals("merged") || eventType.equals("referenced")) {
      String text = null;
      String commitId = issueEvent.event.commit_id;
      String commitContent;
      if (!TextUtils.isEmpty(commitId)) {
        commitContent = ShaUtils.shortSha(commitId);
      } else {
        commitContent = "********";
      }
      if (eventType.equals("merged")) {
        IconicsDrawable drawable = new IconicsDrawable(getContext());
        drawable.backgroundColorRes(R.color.pullrequest_state_merged_dark);
        drawable.color(Color.WHITE);
        drawable.roundedCornersDp(ICON_ROUNDED_CORNER_DP);
        drawable.sizeDp(ICON_SIZE);
        drawable.paddingDp(ICON_PADDING);
        setCompoundDrawables(drawable.icon(Octicons.Icon.oct_git_merge), null, null, null);
        text = getResources().getString(R.string.issue_merged, commitContent);
      } else if (eventType.equals("referenced")) {
        setIcon(Octicons.Icon.oct_git_commit);
        text = getResources().getString(R.string.issue_referenced, commitContent);
      }
      if (text != null) {
        setText(String.format("%s %s", actor, text));
      }
    } else if (eventType.equals("renamed")) {
      setIcon(Octicons.Icon.oct_pencil);
      Rename rename = issueEvent.event.rename;
      String from = getResources().getString(R.string.issue_renamed_from, rename.from);
      String to = getResources().getString(R.string.issue_renamed_to, rename.to);
      setText(Html.fromHtml(actor + " " + from + "<br/>" + to));
    } else {
      setIcon(Octicons.Icon.oct_octoface);
      String text = issueEvent.event.actor.login + " " + eventType + " ";
      setText(text);
    }

    setText(String.format("%s %s", getText(), getTime(issueEvent.created_at)));

    Log.i("PR_time_event", eventType + ": " + (System.currentTimeMillis() - milis) + "ms");
  }

  private void setIcon(IIcon icon) {
    IconicsDrawable drawable = new IconicsDrawable(getContext());
    drawable.backgroundColor(Material.Grey._300.getAsColor());
    drawable.color(Material.Grey._700.getAsColor());
    drawable.roundedCornersDp(ICON_ROUNDED_CORNER_DP);
    drawable.sizeDp(ICON_SIZE);
    drawable.paddingDp(ICON_PADDING);
    setCompoundDrawables(drawable.icon(icon), null, null, null);
  }

  private String getTime(long time) {
    RelativeDateFormat relFormat = new RelativeDateFormat(getContext(), NaturalDateFormat.DATE);
    return relFormat.format(time);
  }
}

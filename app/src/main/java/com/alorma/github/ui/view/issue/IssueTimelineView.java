package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.issue.IssueLabel;
import com.alorma.github.sdk.bean.issue.IssueStoryEvent;
import com.alorma.github.ui.view.timeline.TimelineType;
import com.alorma.github.ui.view.timeline.TimelineView;
import com.alorma.github.utils.TimeUtils;

/**
 * Created by Bernat on 10/04/2015.
 */
public class IssueTimelineView extends LinearLayout {
    private IssueStoryEvent issueEvent;

    private TextView textView;
    private TimelineView timeline;

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
        inflate(getContext(), R.layout.issue_detail_issue_timeline_view, this);
        timeline = (TimelineView) findViewById(R.id.timeline);
        textView = (TextView) findViewById(R.id.text);
    }

    public void setLastItem(boolean lastItem) {
        if (lastItem) {
            timeline.setTimelineType(TimelineType.END);
        } else {
            timeline.setTimelineType(TimelineType.MIDDLE);
        }
    }

    public void setIssueEvent(IssueStoryEvent issueEvent) {
        this.issueEvent = issueEvent;

        timeline.setColorRes(R.color.gray_github_medium);

        String time = TimeUtils.getTimeString(getContext(), issueEvent.event.created_at);

        textView.setText("");

        String eventType = issueEvent.event.event;
        if (eventType.equals("closed") || eventType.equals("reopened")) {
            if (eventType.equals("closed")) {
                timeline.setColorRes(R.color.issue_state_close);
            } else if (eventType.equals("reopened")) {
                timeline.setColorRes(R.color.issue_state_open);
            }

            String text = issueEvent.event.actor.login + " " + eventType + " " + time;
            textView.setText(text);

        } else if (eventType.equals("labeled") || eventType.equals("labeled")) {
            String text = null;

            IssueLabel label = issueEvent.event.label;
            String labelName = " " + label.name + " ";
            if (eventType.equals("labeled")) {
                text = getResources().getString(R.string.issue_labeled, labelName, time);
            } else if (eventType.equals("reopened")) {
                text = getResources().getString(R.string.issue_unlabeled, labelName, time);
            }

            if (text != null) {
                int startLabel = text.indexOf(labelName);
                if (startLabel > -1) {
                    BackgroundColorSpan fspan = new BackgroundColorSpan(Color.parseColor("#" + label.color));
                    SpannableString spannableString = new SpannableString(text);
                    spannableString.setSpan(fspan, text.indexOf(labelName), text.indexOf(labelName) + labelName.length(), 0);

                    textView.setText(spannableString);
                }
            }
        } else if (eventType.equals("assigned") || eventType.equals("unassigned")) {
            String text = null;
            String user = "<b>" + issueEvent.event.assignee.login + "</b>";
            if (eventType.equals("assigned")) {
                text = getResources().getString(R.string.issue_assigned, user, time);
            } else if (eventType.equals("unassigned")) {
                text = getResources().getString(R.string.issue_unassigned, user, time);
            }
            if (text != null) {
                textView.setText(Html.fromHtml(text));
            }
        }  else if (eventType.equals("milestoned") || eventType.equals("demilestoned")) {
            String text = null;
            String milestone = "<b>" + issueEvent.event.milestone.title + "</b>";
            if (eventType.equals("milestoned")) {
                text = getResources().getString(R.string.issue_milestoned, milestone, time);
            } else if (eventType.equals("demilestoned")) {
                text = getResources().getString(R.string.issue_demilestoned, milestone, time);
            }
            if (text != null) {
                textView.setText(Html.fromHtml(text));
            }
        }   else if (eventType.equals("merged") || eventType.equals("referenced")) {
            String text = null;
            String commitId = issueEvent.event.commit_id;
            String milestone = "<b>" + commitId.substring(0, 8) + "</b>";
            if (eventType.equals("merged")) {
                text = getResources().getString(R.string.issue_merged, milestone, time);
            } else if (eventType.equals("referenced")) {
                text = getResources().getString(R.string.issue_referenced, milestone, time);
            }
            if (text != null) {
                textView.setText(Html.fromHtml(text));
            }
        } else {
            String text = issueEvent.event.actor.login + " " + eventType + " " + time;
            textView.setText(text);
        }
    }
}

package com.alorma.github.ui.view.issue;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.ui.view.LabelView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wefika.flowlayout.FlowLayout;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.Seconds;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Bernat on 08/04/2015.
 */
public class IssueDetailView extends LinearLayout {

    private TextView title;
    private TextView body;
    private ViewGroup labelsLayout;
    private WebView bodyHtml;
    private Issue issue;
    private ImageView profileIcon;
    private TextView profileName;
    private TextView profileEmail;

    public IssueDetailView(Context context) {
        super(context);
        init();
    }

    public IssueDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IssueDetailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IssueDetailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.issue_detail_issue_view, this);
        setOrientation(VERTICAL);
        title = (TextView) findViewById(R.id.textTitle);
        body = (TextView) findViewById(R.id.textBody);
        bodyHtml = (WebView) findViewById(R.id.webBody);
        labelsLayout = (ViewGroup) findViewById(R.id.labelsLayout);
        View authorView = findViewById(R.id.author);
        profileIcon = (ImageView) authorView.findViewById(R.id.profileIcon);
        profileName = (TextView) authorView.findViewById(R.id.name);
        profileEmail = (TextView) authorView.findViewById(R.id.email);
    }

    public void setIssue(Issue issue) {
        if (this.issue == null) {
            this.issue = issue;
            title.setText(issue.title);

            if (issue.user != null) {
                profileName.setText(issue.user.login);
                profileEmail.setText(getTimeString(issue.created_at));
                ImageLoader instance = ImageLoader.getInstance();
                instance.displayImage(issue.user.avatar_url, profileIcon);
            }

            if (issue.body_html != null) {
                bodyHtml.loadData(issue.body_html, "text/html", "UTF-8");
                bodyHtml.setBackgroundColor(Color.TRANSPARENT);
                bodyHtml.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
                bodyHtml.setVisibility(View.VISIBLE);
                body.setVisibility(View.GONE);
            } else if (issue.body != null) {
                body.setText(issue.body);
                body.setVisibility(View.VISIBLE);
                bodyHtml.setVisibility(View.GONE);
            } else {
                body.setVisibility(View.GONE);
                bodyHtml.setVisibility(View.GONE);
            }

            if (issue.labels != null) {
                labelsLayout.setVisibility(View.VISIBLE);
                for (Label label : issue.labels) {
                    LabelView labelView = new LabelView(getContext());
                    labelView.setLabel(label);
                    labelsLayout.addView(labelView);

                    if (labelView.getLayoutParams() != null && labelView.getLayoutParams() instanceof FlowLayout.LayoutParams) {
                        int margin = getResources().getDimensionPixelOffset(R.dimen.gapSmall);
                        FlowLayout.LayoutParams layoutParams = (FlowLayout.LayoutParams) labelView.getLayoutParams();
                        layoutParams.height = FlowLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.width = FlowLayout.LayoutParams.WRAP_CONTENT;
                        layoutParams.setMargins(margin, margin, margin, margin);
                        labelView.setLayoutParams(layoutParams);
                    }
                }
            } else {
                labelsLayout.setVisibility(View.GONE);
            }
        }
    }

    private String getTimeString(String date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        DateTime dt = formatter.parseDateTime(date);
        DateTime dtNow = DateTime.now().withZone(DateTimeZone.UTC);

        Years years = Years.yearsBetween(dt.withTimeAtStartOfDay(), dtNow.withTimeAtStartOfDay());
        int text = R.string.time_ago_at_years;
        int time = years.getYears();

        if (time == 0) {
            Months months = Months.monthsBetween(dt.withTimeAtStartOfDay(), dtNow.withTimeAtStartOfDay());
            text = R.string.time_ago_at_months;
            time = months.getMonths();

            if (time == 0) {

                Days days = Days.daysBetween(dt.withTimeAtStartOfDay(), dtNow.withTimeAtStartOfDay());
                text = R.string.time_ago_at_days;
                time = days.getDays();

                if (time == 0) {
                    Hours hours = Hours.hoursBetween(dt.toLocalDateTime(), dtNow.toLocalDateTime());
                    time = hours.getHours();
                    text = R.string.time_ago_at_hours;

                    if (time == 0) {
                        Minutes minutes = Minutes.minutesBetween(dt.toLocalDateTime(), dtNow.toLocalDateTime());
                        time = minutes.getMinutes();
                        text = R.string.time_ago_at_minutes;
                        if (time == 0) {
                            Seconds seconds = Seconds.secondsBetween(dt.toLocalDateTime(), dtNow.toLocalDateTime());
                            time = seconds.getSeconds();
                            text = R.string.time_ago_at_seconds;
                        }
                    }
                }
            }
        }

        return getContext().getResources().getString(text, time);
    }
}

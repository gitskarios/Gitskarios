package com.alorma.github.ui.adapter.commit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Commit;
import com.alorma.github.sdk.bean.dto.response.User;
import com.alorma.github.ui.adapter.LazyAdapter;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.utils.AttributesUtils;
import com.alorma.github.utils.TextUtils;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.nostra13.universalimageloader.core.ImageLoader;

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

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Bernat on 07/09/2014.
 */
public class CommitsAdapter extends RecyclerArrayAdapter<Commit, CommitsAdapter.ViewHolder> /*implements StickyListHeadersAdapter*/ {

    private boolean shortMessage;

    public CommitsAdapter(LayoutInflater inflater, boolean shortMessage) {
        super(inflater);
        this.shortMessage = shortMessage;
    }

    @Override
    public CommitsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(R.layout.commit_row, parent, false));
    }

    @Override
    public void onBindViewHolder(CommitsAdapter.ViewHolder holder, int position) {
        Commit commit = getItem(position);

        User author = commit.author;

        if (author == null) {
            author = commit.commit.author;
        }

        if (author == null) {
            author = commit.commit.committer;
        }

        if (author != null) {
            if (author.avatar_url != null) {
                ImageLoader.getInstance().displayImage(author.avatar_url, holder.avatar);
            } else if (author.email != null) {
                try {
                    MessageDigest digest = MessageDigest.getInstance("MD5");
                    digest.update(author.email.getBytes());
                    byte messageDigest[] = digest.digest();
                    StringBuffer hexString = new StringBuffer();
                    for (int i = 0; i < messageDigest.length; i++)
                        hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
                    String hash = hexString.toString();
                    ImageLoader.getInstance().displayImage("http://www.gravatar.com/avatar/" + hash, holder.avatar);
                } catch (NoSuchAlgorithmException e) {
                    IconicsDrawable iconDrawable = new IconicsDrawable(holder.itemView.getContext(), Octicons.Icon.oct_octoface);
                    iconDrawable.color(AttributesUtils.getSecondaryTextColor(holder.itemView.getContext()));
                    iconDrawable.sizeDp(36);
                    iconDrawable.setAlpha(128);
                    holder.avatar.setImageDrawable(iconDrawable);
                }

            } else {
                IconicsDrawable iconDrawable = new IconicsDrawable(holder.itemView.getContext(), Octicons.Icon.oct_octoface);
                iconDrawable.color(AttributesUtils.getSecondaryTextColor(holder.itemView.getContext()));
                iconDrawable.sizeDp(36);
                iconDrawable.setAlpha(128);
                holder.avatar.setImageDrawable(iconDrawable);
            }

            if (author.login != null) {
                holder.user.setText(author.login);
            } else if (author.name != null) {
                holder.user.setText(author.name);
            } else if (author.email != null) {
                holder.user.setText(author.email);
            }
        }

        String message = commit.shortMessage();
        if (commit.commit != null && commit.commit.shortMessage() != null) {
            message = commit.commit.shortMessage();
        }

        if (shortMessage) {
            try {
                holder.title.setText(TextUtils.splitLines(message, 2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            holder.title.setText(message);
        }

        if (commit.sha != null) {
            holder.sha.setText(commit.shortSha());
        }

        holder.textNums.setText("");

        if (commit.stats != null) {
            String textCommitsStr = null;
            if (commit.stats.additions > 0 && commit.stats.deletions > 0) {
                textCommitsStr = holder.itemView.getContext().getString(R.string.commit_file_add_del, commit.stats.additions, commit.stats.deletions);
                holder.textNums.setVisibility(View.VISIBLE);
            } else if (commit.stats.additions > 0) {
                textCommitsStr = holder.itemView.getContext().getString(R.string.commit_file_add, commit.stats.additions);
                holder.textNums.setVisibility(View.VISIBLE);
            } else if (commit.stats.deletions > 0) {
                textCommitsStr = holder.itemView.getContext().getString(R.string.commit_file_del, commit.stats.deletions);
                holder.textNums.setVisibility(View.VISIBLE);
            } else {
                holder.textNums.setVisibility(View.GONE);
            }

            if (textCommitsStr != null) {
                holder.textNums.setText(Html.fromHtml(textCommitsStr));
            }
        } else {
            holder.textNums.setVisibility(View.GONE);
        }

        if (commit.files != null && commit.files.size() > 0) {
            holder.numFiles.setVisibility(View.VISIBLE);
            holder.numFiles.setText(holder.itemView.getContext().getString(R.string.num_of_files, commit.files.size()));
        } else {
            holder.numFiles.setVisibility(View.GONE);
        }
    }

    private String getTimeString(Context context, String name, String date) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        DateTime dt = formatter.parseDateTime(date);
        DateTime dtNow = DateTime.now().withZone(DateTimeZone.UTC);

        Years years = Years.yearsBetween(dt.withTimeAtStartOfDay(), dtNow.withTimeAtStartOfDay());
        int text = R.string.commit_authored_at_years;
        int time = years.getYears();

        if (time == 0) {
            Months months = Months.monthsBetween(dt.withTimeAtStartOfDay(), dtNow.withTimeAtStartOfDay());
            text = R.string.commit_authored_at_months;
            time = months.getMonths();

            if (time == 0) {

                Days days = Days.daysBetween(dt.withTimeAtStartOfDay(), dtNow.withTimeAtStartOfDay());
                text = R.string.commit_authored_at_days;
                time = days.getDays();

                if (time == 0) {
                    Hours hours = Hours.hoursBetween(dt.toLocalDateTime(), dtNow.toLocalDateTime());
                    time = hours.getHours();
                    text = R.string.commit_authored_at_hours;

                    if (time == 0) {
                        Minutes minutes = Minutes.minutesBetween(dt.toLocalDateTime(), dtNow.toLocalDateTime());
                        time = minutes.getMinutes();
                        text = R.string.commit_authored_at_minutes;

                        if (time == 0) {
                            Seconds seconds = Seconds.secondsBetween(dt.toLocalDateTime(), dtNow.toLocalDateTime());
                            time = seconds.getSeconds();
                            if (time > 5) {
                                text = R.string.commit_authored_at_seconds;
                            } else {
                                text = R.string.commit_authored_at_now;
                            }
                        }
                    }
                }
            }
        }

        return context.getResources().getString(text, name, time);
    }

/*
    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        View v = inflate(R.layout.commit_row_header, viewGroup, false);
        TextView tv = (TextView) v.findViewById(android.R.id.text1);

        Commit commit = getItem(i);

        if (commit.commit != null && commit.commit.author != null && commit.commit.author.date != null) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            DateTime dt = formatter.parseDateTime(commit.commit.author.date);

            String text = dt.toString("dd MMM yyyy");

            tv.setText(text);
        }
        return tv;
    }

    @Override
    public long getHeaderId(int i) {
        return getItem(i).days;
    }
*/

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView user;
        private final TextView sha;
        private final TextView textNums;
        private final TextView numFiles;
        private final ImageView avatar;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            user = (TextView) itemView.findViewById(R.id.user);
            sha = (TextView) itemView.findViewById(R.id.sha);
            textNums = (TextView) itemView.findViewById(R.id.textNums);
            numFiles = (TextView) itemView.findViewById(R.id.numFiles);
            avatar = (ImageView) itemView.findViewById(R.id.avatarAuthor);

        }
    }
}

package com.alorma.github.ui.adapter.commit;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.alorma.github.R;
import com.alorma.github.emoji.EmojiBitmapLoader;
import com.alorma.github.ui.adapter.base.RecyclerArrayAdapter;
import com.alorma.github.ui.view.UserAvatarView;
import com.alorma.github.utils.TextUtils;
import com.mikepenz.octicons_typeface_library.Octicons;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import core.User;
import core.repositories.Commit;
import java.io.IOException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class CommitsAdapter extends RecyclerArrayAdapter<Commit, CommitsAdapter.ViewHolder>
    implements StickyRecyclerHeadersAdapter<CommitsAdapter.HeaderViewHolder> {

  private boolean shortMessage;
  private CommitsAdapterListener commitsAdapterListener;
  private EmojiBitmapLoader emojiBitmapLoader;

  public CommitsAdapter(LayoutInflater inflater, boolean shortMessage) {
    super(inflater);
    this.shortMessage = shortMessage;
    emojiBitmapLoader = new EmojiBitmapLoader();
  }

  @Override
  public CommitsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ViewHolder(getInflater().inflate(R.layout.row_commit, parent, false));
  }

  @Override
  public void onBindViewHolder(CommitsAdapter.ViewHolder holder, Commit commit) {
    bindUser(holder, commit);
    bindMessage(holder, commit);
    bindSha(holder, commit);
    bindFiles(holder, commit);
    bindVerification(holder, commit);
    bindComments(holder, commit);
  }

  private void bindComments(ViewHolder holder, Commit commit) {
    TextUtils.applyNumToTextView(holder.comments_count, Octicons.Icon.oct_comment_discussion, commit.comment_count);
  }

  private void bindVerification(ViewHolder holder, Commit commit) {
    boolean verifiedCommit = commit.isCommitVerified();
    holder.verifiedCommit.setVisibility(verifiedCommit ? View.VISIBLE : View.GONE);
  }

  private void bindFiles(ViewHolder holder, Commit commit) {
    if (commit.files != null && commit.files.size() > 0) {
      holder.numFiles.setVisibility(View.VISIBLE);
      holder.numFiles.setText(holder.itemView.getContext().getString(R.string.num_of_files, commit.files.size()));
    } else {
      holder.numFiles.setVisibility(View.GONE);
    }
  }

  private void bindSha(ViewHolder holder, Commit commit) {
    if (commit.sha != null) {
      holder.sha.setText(commit.shortSha());
    }
  }

  private void bindMessage(ViewHolder holder, Commit commit) {
    String message = commit.shortMessage();
    if (commit.commit != null && commit.commit.getMessage() != null) {
      message = commit.commit.getMessage();
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
    emojiBitmapLoader.parseTextView(holder.title);
  }

  private void bindUser(ViewHolder holder, Commit commit) {
    User author = getUser(commit);
    if (author != null) {
      holder.avatar.setUser(author);

      if (author.getLogin() != null) {
        holder.user.setText(author.getLogin());
      } else if (author.getName() != null) {
        holder.user.setText(author.getName());
      } else if (author.getEmail() != null) {
        holder.user.setText(author.getEmail());
      }
    }
  }

  private User getUser(Commit commit) {
    User author = commit.author;

    if (author == null) {
      author = commit.commit.author;
    }

    if (author == null) {
      author = commit.commit.committer;
    }
    return author;
  }

  @Override
  public long getHeaderId(int i) {
    return getItem(i).days;
  }

  @Override
  public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup viewGroup) {
    return new HeaderViewHolder(getInflater().inflate(R.layout.row_header_commit, viewGroup, false));
  }

  @Override
  public void onBindHeaderViewHolder(HeaderViewHolder headerViewHolder, int i) {
    Commit commit = getItem(i);

    if (commit.commit != null && commit.commit.author != null && commit.commit.author.date != null) {
      DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
      DateTime dt = formatter.parseDateTime(commit.commit.author.date);

      String text = dt.toString("dd MMM yyyy");

      headerViewHolder.tv.setText(text);
    }
  }

  public void setCommitsAdapterListener(CommitsAdapterListener commitsAdapterListener) {
    this.commitsAdapterListener = commitsAdapterListener;
  }

  public interface CommitsAdapterListener {
    void onCommitClick(Commit commit);

    boolean onCommitLongClick(Commit commit);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    private final TextView title;
    private final TextView user;
    private final TextView sha;
    private final TextView numFiles;
    private final TextView comments_count;
    private final UserAvatarView avatar;
    private final ImageView verifiedCommit;

    public ViewHolder(final View itemView) {
      super(itemView);

      title = (TextView) itemView.findViewById(R.id.title);
      user = (TextView) itemView.findViewById(R.id.user);
      sha = (TextView) itemView.findViewById(R.id.sha);
      numFiles = (TextView) itemView.findViewById(R.id.numFiles);
      comments_count = (TextView) itemView.findViewById(R.id.comments_count);
      avatar = (UserAvatarView) itemView.findViewById(R.id.avatarAuthor);
      verifiedCommit = (ImageView) itemView.findViewById(R.id.verifiedCommit);

      itemView.setOnClickListener(v -> {
        if (commitsAdapterListener != null) {
          Commit commit = getItem(getAdapterPosition());
          commitsAdapterListener.onCommitClick(commit);
        }
      });

      itemView.setOnLongClickListener(v -> {
        if (commitsAdapterListener != null) {
          Commit commit = getItem(getAdapterPosition());
          return commitsAdapterListener.onCommitLongClick(commit);
        }
        return true;
      });
    }

    public void copy(String text) {
      ClipboardManager clipboard = (ClipboardManager) itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
      ClipData clip = ClipData.newPlainText("Gitskarios", text);
      clipboard.setPrimaryClip(clip);
    }
  }

  public class HeaderViewHolder extends RecyclerView.ViewHolder {

    private final TextView tv;

    public HeaderViewHolder(View itemView) {
      super(itemView);
      tv = (TextView) itemView.findViewById(android.R.id.text1);
    }
  }
}

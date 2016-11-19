package com.alorma.github.ui.fragment.events;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.alorma.github.IntentsManager;
import com.alorma.github.R;
import com.alorma.github.ui.adapter.commit.CommitsAdapter;
import com.alorma.github.ui.fragment.BaseBottomSheetDialogFragment;
import core.repositories.Commit;
import java.util.ArrayList;
import java.util.List;

public class PushedCommitsBottomSheetDialog extends BaseBottomSheetDialogFragment {
  private static final String COMMITS = "COMMITS";

  public static PushedCommitsBottomSheetDialog newInstance(List<Commit> commits) {
    PushedCommitsBottomSheetDialog fragment = new PushedCommitsBottomSheetDialog();

    Bundle args = new Bundle();
    args.putParcelableArrayList(COMMITS, new ArrayList<>(commits));
    fragment.setArguments(args);

    return fragment;
  }

  @BindView(R.id.recycler) RecyclerView recyclerView;
  @BindView(R.id.toolbar) Toolbar toolbar;

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);

    return inflater.inflate(R.layout.commits_push_event, null, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.bind(this, view);

    toolbar.setNavigationIcon(R.drawable.ic_close_black_24dp);
    toolbar.setNavigationOnClickListener(navigationIcon -> dismiss());
    toolbar.setTitle(R.string.event_select_commit);

    if (getArguments() == null) {
      dismiss();
    } else {
      ArrayList<Commit> commits = getArguments().getParcelableArrayList(COMMITS);

      CommitsAdapter adapter = new CommitsAdapter(LayoutInflater.from(getActivity()), true);
      adapter.addAll(commits);
      adapter.setCallback(item -> startActivity(new IntentsManager(getActivity()).checkUri(Uri.parse(item.url))));
      recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
      recyclerView.setAdapter(adapter
      );
    }
  }
}

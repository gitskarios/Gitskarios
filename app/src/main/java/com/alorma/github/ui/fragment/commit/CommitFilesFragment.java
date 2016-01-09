package com.alorma.github.ui.fragment.commit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.CommitFile;
import com.alorma.github.sdk.bean.info.CommitInfo;
import com.alorma.github.ui.adapter.commit.CommitFilesAdapter;
import com.alorma.github.ui.fragment.base.BaseFragment;

import java.util.List;

/**
 * Created by Bernat on 22/12/2014.
 */
public class CommitFilesFragment extends BaseFragment {

    public static final String INFO = "INFO";
    private RecyclerView recyclerView;
    private UpdateReceiver updateReceiver;
    private CommitInfo info;
    private CommitFilesAdapter.OnFileRequestListener onFileRequestListener;

    public static CommitFilesFragment newInstance(CommitInfo info) {
        CommitFilesFragment f = new CommitFilesFragment();
        Bundle b = new Bundle();
        b.putParcelable(INFO, info);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.files_fragment, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            info = (CommitInfo) getArguments().getParcelable(INFO);

            recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            getContent();
        }
    }

    private void getContent() {

    }

    public void setFiles(List<CommitFile> files) {
        if (getActivity() != null) {
            CommitFilesAdapter adapter = new CommitFilesAdapter(LayoutInflater.from(getActivity()));
            adapter.addAll(files);
            adapter.setOnFileRequestListener(onFileRequestListener);
            recyclerView.setAdapter(adapter);
        }
    }

    public void reload() {
        getContent();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateReceiver = new UpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(updateReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(updateReceiver);
    }

    public void setOnFileRequestListener(CommitFilesAdapter.OnFileRequestListener onFileRequestListener) {
        this.onFileRequestListener = onFileRequestListener;
    }

    public class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (isOnline(context)) {
                reload();
            }
        }

        public boolean isOnline(Context context) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfoMob = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo netInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return (netInfoMob != null && netInfoMob.isConnectedOrConnecting()) || (netInfoWifi != null && netInfoWifi.isConnectedOrConnecting());
        }
    }
}

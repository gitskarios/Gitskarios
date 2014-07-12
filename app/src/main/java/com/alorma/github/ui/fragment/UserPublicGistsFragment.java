package com.alorma.github.ui.fragment;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.alorma.github.sdk.bean.dto.response.Gist;
import com.alorma.github.sdk.bean.dto.response.ListGists;
import com.alorma.github.sdk.client.BaseClient;
import com.alorma.github.sdk.client.UserGistsClient;
import com.alorma.github.ui.adapter.UserGistsAdapter;

import java.util.ArrayList;

import retrofit.RetrofitError;

public class UserPublicGistsFragment extends ListFragment implements BaseClient.OnResultCallback<ListGists> {

    private UserGistsAdapter userGistsAdapter;

    public static UserPublicGistsFragment newInstance() {
        return new UserPublicGistsFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpList();

        UserGistsClient client = new UserGistsClient(getActivity());
        client.setOnResultCallback(this);
        client.execute();
    }

    private void setUpList() {
        userGistsAdapter = new UserGistsAdapter(getActivity(), new ArrayList<Gist>());
        setListAdapter(userGistsAdapter);
    }

    @Override
    public void onResponseOk(ListGists gists) {
        if (getActivity() != null) {
            userGistsAdapter.clear();
            userGistsAdapter.addAll(gists);
        }
    }

    @Override
    public void onFail(RetrofitError error) {
        Toast.makeText(getActivity(), "failed: " + error, Toast.LENGTH_SHORT).show();
    }
}

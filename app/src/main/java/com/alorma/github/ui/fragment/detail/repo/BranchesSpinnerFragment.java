package com.alorma.github.ui.fragment.detail.repo;

import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.Branch;
import com.alorma.github.sdk.bean.dto.response.ListBranches;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.repo.GetRepoBranchesClient;
import com.alorma.github.ui.adapter.detail.repo.BranchesSpinnerAdapter;

import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 09/08/2014.
 */
public class BranchesSpinnerFragment extends Fragment implements BaseClient.OnResultCallback<ListBranches>,
        AdapterView.OnItemSelectedListener {

    private TextView text;
    private boolean useDivider;
    private Spinner spinner;
    private View divider;
    private OnBranchesListener onBranchesListener;
    private BranchesSpinnerAdapter adapter;
    private String defaultBranch;

    public static BranchesSpinnerFragment newInstance(String owner, String repo, String defaultBranch) {
        Bundle args = new Bundle();
        args.putString("OWNER", owner);
        args.putString("REPO", repo);
        args.putString("DEFAULT", defaultBranch);

        BranchesSpinnerFragment f = new BranchesSpinnerFragment();
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.small_pager_spinner, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String owner = getArguments().getString("OWNER");
        String repo = getArguments().getString("REPO");
        defaultBranch = getArguments().getString("DEFAULT");

        spinner = (Spinner) view.findViewById(R.id.spinner);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spinner.setPopupBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.accentDark)));
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner.setOnItemSelectedListener(BranchesSpinnerFragment.this);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        text = (TextView) view.findViewById(R.id.text);
        divider = view.findViewById(R.id.divider);
        divider.setVisibility(useDivider ? View.VISIBLE : View.INVISIBLE);

        GetRepoBranchesClient branchesClient = new GetRepoBranchesClient(getActivity(), owner, repo);
        branchesClient.setOnResultCallback(this);
        branchesClient.execute();

        text.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
    }

    @Override
    public void onResponseOk(ListBranches branches, Response r) {
        if (branches != null && branches.size() > 0) {

            ListBranches newBranchesList = new ListBranches(branches.size());

            for (Branch branch : branches) {
                if (defaultBranch != null && defaultBranch.equals(branch.name)) {
                    newBranchesList.add(branch);
                }
            }

            for (Branch branch : branches) {
                if (defaultBranch != null && !defaultBranch.equals(branch.name)) {
                    newBranchesList.add(branch);
                }
            }

            text.setVisibility(View.GONE);
            spinner.setVisibility(View.VISIBLE);
            adapter = new BranchesSpinnerAdapter(getActivity(), newBranchesList);
            spinner.setAdapter(adapter);
            if (onBranchesListener != null) {
                onBranchesListener.onBranches(branches.size());
            }
        } else {
            if (onBranchesListener != null) {
                onBranchesListener.onNoBranches();
            }
        }
    }

    @Override
    public void onFail(RetrofitError error) {

    }

    public void setUseDivider(boolean useDivider) {
        this.useDivider = useDivider;

        if (divider != null) {
            divider.setVisibility(useDivider ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void setOnBranchesListener(OnBranchesListener onBranchesListener) {
        this.onBranchesListener = onBranchesListener;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (onBranchesListener != null && adapter != null) {
            Branch branch = adapter.getItem(position);
            onBranchesListener.onBranchSelected(branch);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnBranchesListener {
        void onNoBranches();

        void onBranches(int size);

        void onBranchSelected(Branch branch);
    }


}

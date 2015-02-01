package com.alorma.github.ui.fragment.base;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;

import com.alorma.github.sdk.bean.info.PaginationLink;
import com.alorma.github.sdk.bean.info.RelType;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.ui.ErrorHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

public abstract class PaginatedListFragment<K> extends LoadingListFragment implements BaseClient.OnResultCallback<K>, AbsListView.OnScrollListener {

	protected static final String USERNAME = "USERNAME";
	protected boolean paging;
	private PaginationLink bottomPaginationLink;
	protected boolean refreshing;

	@Override
	public void onScroll(AbsListView absListView, int first, int last, int total) {
		super.onScroll(absListView, first, last, total);
		if (total > 0 && first + last == total) {
			if (bottomPaginationLink != null && bottomPaginationLink.rel == RelType.next) {
				paging = true;
				executePaginatedRequest(bottomPaginationLink.page);
				bottomPaginationLink = null;
			}
		}
	}

	@Override
	public void onResponseOk(K k, Response r) {
		stopRefresh();
		if (getActivity() != null && isAdded()) {
			if (k != null && k instanceof List) {
				if (emptyLy != null && ((List) k).size() > 0) {
					emptyLy.setVisibility(View.GONE);
					getLinkData(r);
					onResponse(k, refreshing);
					paging = false;
					refreshing = false;
				} else {
					setEmpty();
				}
			} else {
				setEmpty();
			}
		}
	}

	@Override
	public void onFail(RetrofitError error) {
		stopRefresh();
		if (getActivity() != null) {
			if (getListAdapter() == null || getListAdapter().getCount() == 0) {
				setEmpty();
			}
			ErrorHandler.onRetrofitError(getActivity(), "Paginated list fragment", error);
		}
	}

	protected abstract void onResponse(K k, boolean refreshing);

	private void getLinkData(Response r) {
		List<Header> headers = r.getHeaders();
		Map<String, String> headersMap = new HashMap<String, String>(headers.size());
		for (Header header : headers) {
			headersMap.put(header.getName(), header.getValue());
		}

		String link = headersMap.get("Link");

		if (link != null) {
			String[] parts = link.split(",");
			try {
				bottomPaginationLink = new PaginationLink(parts[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onRefresh() {
		refreshing = true;
		executeRequest();
	}
}

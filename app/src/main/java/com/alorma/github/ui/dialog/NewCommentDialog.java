package com.alorma.github.ui.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.dto.response.IssueComment;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.services.client.BaseClient;
import com.alorma.github.sdk.services.issues.NewIssueCommentClient;
import com.alorma.github.ui.ErrorHandler;
import com.alorma.github.ui.activity.base.BaseDialogActivity;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 06/09/2014.
 */
public class NewCommentDialog extends BaseDialogActivity implements BaseClient.OnResultCallback<IssueComment> {

	private static final String ISSUE_INFO = "ISSUE_INFO";
	private EditText edit;
	private IssueInfo issueInfo;
	private SmoothProgressBar smoothBar;

	public static Intent launchIntent(Context context, IssueInfo issueInfo) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(ISSUE_INFO, issueInfo);

		Intent intent = new Intent(context, NewCommentDialog.class);

		intent.putExtras(bundle);
		return intent;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_issue_comment);

		if (getIntent().getExtras() != null) {
			issueInfo = getIntent().getExtras().getParcelable(ISSUE_INFO);

			if (issueInfo != null) {
				smoothBar = (SmoothProgressBar) findViewById(R.id.smoothBar);
				smoothBar.progressiveStop();
				edit = (EditText) findViewById(R.id.edit);
			}
		} else {
			finish();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.new_issue_comment, menu);

		MenuItem itemSend = menu.findItem(R.id.action_send);
		if (itemSend != null) {
			IconDrawable iconDrawable = new IconDrawable(this, Iconify.IconValue.fa_send);
			iconDrawable.color(Color.WHITE);
			iconDrawable.actionBarSize();
			itemSend.setIcon(iconDrawable);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.action_send) {
			if (smoothBar != null) {
				smoothBar.progressiveStart();
			}
			String body = edit.getText().toString();
			NewIssueCommentClient client = new NewIssueCommentClient(this, issueInfo, body);
			client.setOnResultCallback(this);
			client.execute();
		}

		return true;
	}

	@Override
	public void onResponseOk(IssueComment issueComment, Response r) {
		if (smoothBar != null) {
			smoothBar.progressiveStop();
		}
		setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onFail(RetrofitError error) {
		ErrorHandler.onRetrofitError(this, "NewCommentDialog", error);
	}
}

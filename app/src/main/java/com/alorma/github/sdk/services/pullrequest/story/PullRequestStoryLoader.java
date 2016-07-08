package com.alorma.github.sdk.services.pullrequest.story;

import android.util.Pair;

import com.alorma.github.sdk.bean.dto.response.GithubComment;
import com.alorma.github.sdk.bean.dto.response.Label;
import com.alorma.github.sdk.bean.dto.response.PullRequest;
import com.alorma.github.sdk.bean.dto.response.ReviewComment;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.issue.IssueEvent;
import com.alorma.github.sdk.bean.issue.IssueStoryComment;
import com.alorma.github.sdk.bean.issue.IssueStoryComparators;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.IssueStoryEvent;
import com.alorma.github.sdk.bean.issue.IssueStoryReviewComments;
import com.alorma.github.sdk.bean.issue.PullRequestStory;
import com.alorma.github.sdk.services.client.BaseInfiniteCallback;
import com.alorma.github.sdk.services.client.GithubClient;
import com.alorma.github.sdk.services.issues.story.GithubCommentReactionsIssueMapper;
import com.alorma.github.sdk.services.issues.story.GithubPRReactionsIssueMapper;
import com.alorma.github.sdk.services.issues.story.GithubReactionsIssueMapper;
import com.alorma.github.sdk.services.issues.story.IssueStoryService;
import com.alorma.github.sdk.services.pullrequest.PullRequestsService;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;
import rx.Observable;
import rx.functions.Func1;

public class PullRequestStoryLoader extends GithubClient<PullRequestStory> {

  private final IssueInfo issueInfo;
  private final String owner;
  private final String repo;
  private final int num;
  private final IssueStoryService issueStoryService;
  private final PullRequestsService pullRequestsService;
  private PullRequestStoryService pullRequestStoryService;

  public PullRequestStoryLoader(IssueInfo info) {
    super();
    this.issueInfo = info;
    this.owner = issueInfo.repoInfo.owner;
    this.repo = issueInfo.repoInfo.name;
    this.num = issueInfo.num;
    pullRequestStoryService = getRestAdapter().create(PullRequestStoryService.class);
    issueStoryService = getRestAdapter().create(IssueStoryService.class);
    pullRequestsService = getRestAdapter().create(PullRequestsService.class);
  }

  @Override
  protected Observable<PullRequestStory> getApiObservable(RestAdapter restAdapter) {
    return getPullrequestStory();
  }

  private Observable<PullRequestStory> getPullrequestStory() {
    return Observable.zip(getPullRequestObs(), getIssueDetailsObservable(),
        (pullRequest, details) -> {
          PullRequestStory pullRequestStory = new PullRequestStory();
          pullRequestStory.item = pullRequest;
          pullRequestStory.details = details;
          Collections.sort(pullRequestStory.details,
              IssueStoryComparators.ISSUE_STORY_DETAIL_COMPARATOR);
          return pullRequestStory;
        });
  }

  private Observable<PullRequest> getPullRequestObs() {
    Observable<PullRequest> pullRequestObservable =
        pullRequestStoryService.detailObs(owner, repo, num)
                .map(new GithubPRReactionsIssueMapper());

    return Observable.zip(pullRequestObservable, getLabelsObs(), (pullRequest, labels) -> {
      pullRequest.labels = labels;
      return pullRequest;
    });
  }

  private Observable<List<IssueStoryDetail>> getIssueDetailsObservable() {
    Observable<IssueStoryDetail> commentsDetailsObs = getCommentsDetailsObs();
    Observable<IssueStoryDetail> eventDetailsObs = getEventDetailsObs();
    Observable<IssueStoryDetail> reviewCommentsObs = getReviewCommentsDetailsObs();
    Observable<IssueStoryDetail> details =
        Observable.mergeDelayError(eventDetailsObs, reviewCommentsObs);
    return Observable.mergeDelayError(commentsDetailsObs, details)
        .toSortedList((issueStoryDetail, issueStoryDetail2) -> {
          return ((Long) issueStoryDetail.createdAt()).compareTo(issueStoryDetail2.createdAt());
        });
  }

  private Observable<List<GithubComment>> getCommentsObs() {
    return Observable.create(new BaseInfiniteCallback<List<GithubComment>>() {
      @Override
      public void execute() {
        issueStoryService.comments(issueInfo.repoInfo.owner, issueInfo.repoInfo.name, issueInfo.num,
            this);
      }

      @Override
      protected void executePaginated(int nextPage) {
        issueStoryService.comments(issueInfo.repoInfo.owner, issueInfo.repoInfo.name, issueInfo.num,
            nextPage, this);
      }
    });
  }

  private Observable<IssueStoryDetail> getCommentsDetailsObs() {
    return getCommentsObs().flatMap(githubComments -> Observable.from(githubComments)
            .map(new GithubCommentReactionsIssueMapper())
            .map((Func1<GithubComment, IssueStoryDetail>) githubComment -> {
          long time = getMilisFromDateClearDay(githubComment.created_at);
          IssueStoryComment detail = new IssueStoryComment(githubComment);
          detail.created_at = time;
          return detail;
        }));
  }

  private Observable<List<IssueEvent>> getEventsObs() {
    return Observable.create(new BaseInfiniteCallback<List<IssueEvent>>() {

      @Override
      public void execute() {
        issueStoryService.events(issueInfo.repoInfo.owner, issueInfo.repoInfo.name, issueInfo.num,
            this);
      }

      @Override
      protected void executePaginated(int nextPage) {
        issueStoryService.events(issueInfo.repoInfo.owner, issueInfo.repoInfo.name, issueInfo.num,
            nextPage, this);
      }
    });
  }

  private Observable<IssueStoryDetail> getEventDetailsObs() {
    return getEventsObs().flatMap(issueEvents -> Observable.from(issueEvents)
        .filter(issueEvent -> validEvent(issueEvent.event))
        .map((Func1<IssueEvent, IssueStoryDetail>) issueEvent -> {
          long time = getMilisFromDateClearDay(issueEvent.created_at);
          IssueStoryEvent detail = new IssueStoryEvent(issueEvent);
          detail.created_at = time;
          return detail;
        }));
  }

  private Observable<List<ReviewComment>> getReviewCommentsObs() {
    return Observable.create(new BaseInfiniteCallback<List<ReviewComment>>() {

      @Override
      public void execute() {
        pullRequestsService.reviewComments(issueInfo.repoInfo.owner, issueInfo.repoInfo.name,
            issueInfo.num, this);
      }

      @Override
      protected void executePaginated(int nextPage) {
        pullRequestsService.reviewComments(issueInfo.repoInfo.owner, issueInfo.repoInfo.name,
            issueInfo.num, nextPage, this);
      }
    });
  }

  private Observable<IssueStoryDetail> getReviewCommentsDetailsObs() {
    return getReviewCommentsObs().flatMap(reviewComments1 -> {

      Map<String, Pair<String, List<ReviewComment>>> comments = new HashMap<>();
      for (ReviewComment reviewComment : reviewComments1) {
        String key = reviewComment.path + reviewComment.position;
        if (comments.get(key) == null) {
          comments.put(key, new Pair<>(reviewComment.diff_hunk, new ArrayList<>()));
        }
        comments.get(key).second.add(reviewComment);
      }
      return Observable.from(comments.values());
    }).map(pair -> {
      ReviewComment reviewComment = pair.second.get(0);
      long time = getMilisFromDateClearDay(reviewComment.created_at);
      return new IssueStoryReviewComments(pair, time, reviewComment.user);
    });
  }

  private Observable<List<Label>> getLabelsObs() {
    return Observable.create(new BaseInfiniteCallback<List<Label>>() {
      @Override
      public void execute() {
        issueStoryService.labels(owner, repo, num, this);
      }

      @Override
      protected void executePaginated(int nextPage) {
        issueStoryService.labels(owner, repo, num, nextPage, this);
      }
    });
  }

  private long getMilisFromDateClearDay(String createdAt) {
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    DateTime dt = formatter.parseDateTime(createdAt);

    return dt.minuteOfDay().roundFloorCopy().getMillis();
  }

  private boolean validEvent(String event) {
    return !(event.equals("mentioned") ||
        event.equals("subscribed") ||
        event.equals("unsubscribed") ||
        event.equals("labeled") ||
        event.equals("unlabeled"));
  }

  @Override
  public String getAcceptHeader() {
    return "application/vnd.github.v3.full+json";
  }
}
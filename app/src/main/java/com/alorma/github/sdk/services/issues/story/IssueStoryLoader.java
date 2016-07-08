package com.alorma.github.sdk.services.issues.story;

import com.alorma.github.sdk.bean.dto.response.GithubComment;
import com.alorma.github.sdk.bean.dto.response.Issue;
import com.alorma.github.sdk.bean.info.IssueInfo;
import com.alorma.github.sdk.bean.issue.IssueEvent;
import com.alorma.github.sdk.bean.issue.IssueStory;
import com.alorma.github.sdk.bean.issue.IssueStoryComment;
import com.alorma.github.sdk.bean.issue.IssueStoryComparators;
import com.alorma.github.sdk.bean.issue.IssueStoryDetail;
import com.alorma.github.sdk.bean.issue.IssueStoryEvent;
import com.alorma.github.sdk.services.client.BaseInfiniteCallback;
import com.alorma.github.sdk.services.client.GithubClient;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.client.Response;
import rx.Observable;
import rx.functions.Func1;

public class IssueStoryLoader extends GithubClient<IssueStory> {

    private final IssueInfo issueInfo;
    private final String owner;
    private final String repo;
    private final int num;
    private IssueStoryService issueStoryService;

    public IssueStoryLoader(IssueInfo info) {
        super();
        this.issueInfo = info;
        this.owner = issueInfo.repoInfo.owner;
        this.repo = issueInfo.repoInfo.name;
        this.num = issueInfo.num;
        issueStoryService = getRestAdapter().create(IssueStoryService.class);
    }

    @Override
    protected Observable<IssueStory> getApiObservable(RestAdapter restAdapter) {
        return getIssueStory();
    }

    private Observable<IssueStory> getIssueStory() {
        return Observable.zip(getIssueObservable(), getIssueDetailsObservable(), (issue, details) -> {
            IssueStory issueStory = new IssueStory();
            issueStory.item = issue;
            issueStory.details = details;
            Collections.sort(issueStory.details,
                    IssueStoryComparators.ISSUE_STORY_DETAIL_COMPARATOR);
            return issueStory;
        });
    }

    private Observable<Issue> getIssueObservable() {
        return issueStoryService.detailObs(owner, repo, num)
                .map(new GithubReactionsIssueMapper());
    }

    private Observable<List<IssueStoryDetail>> getIssueDetailsObservable() {
        Observable<IssueStoryDetail> commentsDetailsObs = getCommentsDetailsObs();
        Observable<IssueStoryDetail> eventDetailsObs = getEventDetailsObs();
        return Observable.mergeDelayError(commentsDetailsObs, eventDetailsObs).toList();
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
                        this);
            }

            @Override
            public void success(List<GithubComment> githubComments, Response response) {
                super.success(githubComments, response);
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

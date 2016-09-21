package com.alorma.github.ui.tags;

import com.alorma.github.di.DaggerTestComponent;
import com.alorma.github.di.TestComponent;
import com.alorma.github.di.TestModule;
import com.alorma.github.di.tags.TagsTestModule;
import com.alorma.github.presenter.Presenter;
import com.alorma.github.presenter.repos.releases.tags.RepositoryTagsPresenter;
import com.alorma.github.sdk.core.repositories.releases.tags.Tag;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import javax.inject.Inject;

public class RepositoryTagsPresenterTest {

    @Mock Presenter.Callback<List<Tag>> callback;
    @Inject RepositoryTagsPresenter mRepositoryTagsPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // dagger 2
        TestComponent testComponent
                = DaggerTestComponent.builder().testModule(new TestModule()).build();
        testComponent.plus(new TagsTestModule())
                .inject(this);
    }
}

package org.edx.mobile.base;

import static org.mockito.Mockito.mock;

import androidx.annotation.NonNull;

import org.edx.mobile.base.BaseTestCase;
import org.edx.mobile.base.GenericSuperclassUtils;
import org.edx.mobile.base.HiltTestActivity;
import org.edx.mobile.view.Presenter;
import org.edx.mobile.view.PresenterFragment;
import org.robolectric.shadows.support.v4.SupportFragmentController;

public abstract class PresenterFragmentTest<FragmentT extends PresenterFragment<PresenterT, ViewT>, PresenterT extends Presenter<ViewT>, ViewT> extends BaseTestCase {

    protected FragmentT fragment;
    protected ViewT view;
    protected PresenterT presenter;

    protected void startFragment(@NonNull final FragmentT fragment) {
        this.presenter = mock(getPresenterType());
        fragment.setPresenter(presenter);
        SupportFragmentController.setupFragment(fragment, HiltTestActivity.class,
                android.R.id.content, null);
        this.fragment = fragment;
        this.view = fragment.getPresenterView();
    }

    @SuppressWarnings("unchecked")
    private Class<PresenterT> getPresenterType() {
        return (Class<PresenterT>) GenericSuperclassUtils.getTypeArguments(getClass(), PresenterFragmentTest.class)[1];
    }
}
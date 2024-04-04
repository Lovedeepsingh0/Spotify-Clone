package com.oscarliang.spotifyclone.ui.searchresult.artist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.oscarliang.spotifyclone.util.EspressoTestUtil.nestedScrollTo;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.oscarliang.spotifyclone.R;
import com.oscarliang.spotifyclone.domain.model.Artist;
import com.oscarliang.spotifyclone.util.RecyclerViewMatcher;
import com.oscarliang.spotifyclone.util.Resource;
import com.oscarliang.spotifyclone.util.TestUtil;
import com.oscarliang.spotifyclone.util.ViewModelUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ArtistSearchResultFragmentTest {

    private NavController mNavController;
    private ArtistSearchResultViewModel mViewModel;

    private final MutableLiveData<Resource<List<Artist>>> mArtists = new MutableLiveData<>();

    @Before
    public void init() {
        mNavController = mock(NavController.class);
        mViewModel = mock(ArtistSearchResultViewModel.class);
        when(mViewModel.getArtists()).thenReturn(mArtists);

        FragmentScenario<ArtistSearchResultFragment> scenario =
                FragmentScenario.launchInContainer(ArtistSearchResultFragment.class, null, new FragmentFactory() {
                    @NonNull
                    @Override
                    public Fragment instantiate(@NonNull ClassLoader classLoader, @NonNull String className) {
                        ArtistSearchResultFragment fragment = new ArtistSearchResultFragment();
                        fragment.mFactory = ViewModelUtil.createFor(mViewModel);
                        return fragment;
                    }
                });
        scenario.onFragment(fragment -> Navigation.setViewNavController(fragment.getView(), mNavController));
    }

    @Test
    public void testLoading() {
        mArtists.postValue(Resource.loading(null));
        onView(withId(R.id.shimmer_layout_search_result)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoaded() {
        mArtists.postValue(Resource.success(Arrays.asList(TestUtil.createArtist("foo", "bar"))));
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("bar"))));
        onView(withId(R.id.shimmer_layout_search_result)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testError() {
        mArtists.postValue(Resource.error("Failed to load", null));
        onView(withId(R.id.shimmer_layout_search_result)).check(matches(isDisplayed()));
        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText("Failed to load")));
    }

    @Test
    public void loadMore() {
        mArtists.postValue(Resource.success(TestUtil.createArtists(10, "foo", "bar")));
        onView(listMatcher().atPosition(9)).perform(nestedScrollTo());
        onView(listMatcher().atPosition(9)).check(matches(isDisplayed()));
        onView(withId(R.id.progressbar)).check(matches(isDisplayed()));
        verify(mViewModel).loadNextPage();
    }

    @Test
    public void navigateToArtist() {
        mArtists.postValue(Resource.success(Arrays.asList(TestUtil.createArtist("foo", "bar"))));
        onView(withText("bar")).perform(click());
        verify(mNavController).navigate(eq(R.id.action_to_artistFragment), any(Bundle.class));
    }

    private RecyclerViewMatcher listMatcher() {
        return new RecyclerViewMatcher(R.id.recycler_view_search_result);
    }

}

package consumentor.shopgun.aidroid.view;

import android.support.v4.view.ViewPager;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.viewpagerindicator.TitlePageIndicator;

import consumentor.shopgun.aidroid.view.BaseActivity;

/**
 * Created by Simon on 2014-01-17.
 */
public abstract class PagerActivity extends BaseActivity implements ViewPager.OnPageChangeListener  {

    protected ViewPager mPager;
    protected TitlePageIndicator mPagerIndicator;

    @Override
    protected void onResume() {
        super.onResume();

        if (mPagerIndicator != null){
            mPagerIndicator.setOnPageChangeListener(this);
        }
    }

    @Override
    public void onPageSelected(int i) {
        // May return null if EasyTracker has not yet been initialized with a property
// ID.
        Tracker easyTracker = EasyTracker.getInstance(this);

// This screen name value will remain set on the tracker and sent with
// hits until it is set to a new value or to null.
        easyTracker.set(Fields.SCREEN_NAME, this.getClass().getSimpleName() +" - " +mPager.getAdapter().getPageTitle(i).toString());

        easyTracker.send(MapBuilder
                .createAppView()
                .build()
        );

    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }
}

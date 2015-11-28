package consumentor.shopgun.aidroid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;

import consumentor.shopgun.aidroid.customview.CircleDiagram;
import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.model.AdviceInfo;

import java.util.ArrayList;

/**
 * Created by Simon on 2013-10-22.
 */
public abstract class OverviewFragment extends BaseFragment {

    protected OverviewFragmentListener mListener;
    protected CircleDiagram mCircleDiagram;
    private TopWeightedAdvicesFragment mTopWeightedAdvicesFragment;
    protected ImageLoader mImageLoader;
    protected View mContentView;

    public abstract interface OverviewFragmentListener {
        public void onShowAdvices(View view);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        assert activity instanceof OverviewFragmentListener;
        mListener = (OverviewFragmentListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return null;
    }

    protected void initAdviceOverviewFragment(ArrayList<? extends AdviceInfo> adviceInfos) {
        mCircleDiagram = (CircleDiagram) mContentView.findViewById(R.id.circleDiagram);
        mCircleDiagram.init(adviceInfos);
    }

    protected void initTopWeightedAdvicesFragment(ArrayList<? extends AdviceInfo> adviceInfos) {
        mTopWeightedAdvicesFragment = (TopWeightedAdvicesFragment) Fragment.instantiate(getActivity(), TopWeightedAdvicesFragment.class.getName());
        //mTopWeightedAdvicesFragment = (TopWeightedAdvicesFragment) getFragmentManager().findFragmentById(R.id.topWeightedAdvicesFragment);

        if (mTopWeightedAdvicesFragment != null){
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.topWeightedAdvicesFragmentContainer, mTopWeightedAdvicesFragment)
                    .commit();
            mTopWeightedAdvicesFragment.setAdvices(adviceInfos);
        }
    }
}

package consumentor.shopgun.aidroid.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.model.AdviceInfo;
import consumentor.shopgun.aidroid.customview.CircleDiagram;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Simon on 2013-10-21.
 */
public class WeightedAdvicesDiagramFragment extends Fragment {
    private static final String TAG = WeightedAdvicesDiagramFragment.class.getName();
    private View mView;
    private LayoutInflater mInflater;
    private List<? extends AdviceInfo> mAdvices;
    private CircleDiagram mCircle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mInflater = inflater;
        mView = mInflater.inflate(R.layout.fragment_weighted_advices_diagram, container, false);
        return mView;
    }

    public void setAdvices(ArrayList<? extends AdviceInfo> adviceInfos){

        mAdvices = adviceInfos;
        initCircleDiagram();

    }

    private boolean onlyWhiteAdvices(List<? extends AdviceInfo> mAdvices) {
        for (AdviceInfo ai : mAdvices){
            if (ai.semaphoreValue != -2){
                // found an advice with colored semaphore
                return false;
            }
        }
        return true;
    }

    private void initCircleDiagram() {
        mCircle = (CircleDiagram) mView.findViewById(R.id.circleDiagram);
        mCircle.setSectorTextEnabled(true);
        mCircle.init(mAdvices);
    }
}
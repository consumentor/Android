package consumentor.shopgun.aidroid.fragment;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import consumentor.shopgun.aidroid.util.AdviceRatingComparator;
import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.view.AdviceActivity;
import consumentor.shopgun.aidroid.model.AdviceInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Simon on 2013-10-21.
 */
public class TopWeightedAdvicesFragment extends Fragment {
    private static final String TAG = WeightedAdvicesDiagramFragment.class.getName();
    private LinearLayout mAdviceContainer;
    private View mView;
    private LayoutInflater mInflater;
    private List<? extends AdviceInfo> mAdvices;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mInflater = inflater;
        mView = mInflater.inflate(R.layout.fragment_top_weighted_advices, container, false);
        return mView;
    }


    public void setAdvices(ArrayList<? extends AdviceInfo> adviceInfos){

        mAdvices = adviceInfos;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdviceContainer = (LinearLayout) mView.findViewById(R.id.advicesContainer);
        mAdviceContainer.removeAllViews();

        Collections.sort(mAdvices, new AdviceRatingComparator());

        int i = 0;
        if (mAdvices != null && i < mAdvices.size() && mAdviceContainer.getChildCount() < 4) {
            do {
                final AdviceInfo currentAdviceInfo = mAdvices.get(i);

                //Skip white advices
                if (currentAdviceInfo.semaphoreValue == -2) {
                    i++;
                    continue;
                }
                View adviceListItem = mInflater.inflate(R.layout.overview_advice_item, null, false);

                // Open AdviceActivity on click
                adviceListItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent adviceIntent = new Intent(getActivity(), AdviceActivity.class);
                        adviceIntent.putExtra(AdviceActivity.EXTRA_ADVICE_ID, currentAdviceInfo.id);
                        startActivity(adviceIntent);
                    }
                });

                // Init advisor name
                TextView mentorName = (TextView) adviceListItem.findViewById(R.id.advisorName);
                mentorName.setText(currentAdviceInfo.mentorInfo.name);

                // Init abstract line
                TextView adviceDescription = (TextView) adviceListItem.findViewById(R.id.adviceAbstract);
                adviceDescription.setText(currentAdviceInfo.introduction);

                // Init semaphore/tag combination
                ImageView semaphoreIndicator = (ImageView) adviceListItem.findViewById(R.id.semaphoreIndicator);
                Drawable semaphore = null;
                int semaphoreColor = 0;
                switch (currentAdviceInfo.semaphoreValue) {
                    case -1:
                        semaphoreColor = getResources().getColor(R.color.shopgun_red);
                        semaphore = getRedSemaphoreForTag(currentAdviceInfo.tag);
                        break;
                    case 0:
                        semaphoreColor = getResources().getColor(R.color.shopgun_yellow_darker);
                        semaphore = getYellowSemaphoreForTag(currentAdviceInfo.tag);
                        break;
                    case 1:
                        semaphoreColor = getResources().getColor(R.color.shopgun_green);
                        semaphore = getGreenSemaphoreForTag(currentAdviceInfo.tag);
                        break;
                }
                mentorName.setTextColor(semaphoreColor);
                semaphoreIndicator.setImageDrawable(semaphore);

                mAdviceContainer.addView(adviceListItem);
                i++;
            } while (i < mAdvices.size() && mAdviceContainer.getChildCount() < 3);
        }
    }

    private Drawable getGreenSemaphoreForTag(String tag) {
        if (tag.equals(AdviceInfo.TXT_ENVIRONMENT)){
            return getResources().getDrawable(R.drawable.ic_environment_green);
        }
        if (tag.equals(AdviceInfo.TXT_HEALTH)){
            return getResources().getDrawable(R.drawable.ic_health_green);
        }
        if (tag.equals(AdviceInfo.TXT_ETHICS)){
            return getResources().getDrawable(R.drawable.ic_ethic_green);
        }
        return null;
    }

    private Drawable getYellowSemaphoreForTag(String tag) {
        if (tag.equals(AdviceInfo.TXT_ENVIRONMENT)){
            return getResources().getDrawable(R.drawable.ic_environmentl_yellow);
        }
        if (tag.equals(AdviceInfo.TXT_HEALTH)){
            return getResources().getDrawable(R.drawable.ic_health_yellow);
        }
        if (tag.equals(AdviceInfo.TXT_ETHICS)){
            return getResources().getDrawable(R.drawable.ic_ethic_yellow);
        }
        return null;
    }

    private Drawable getRedSemaphoreForTag(String tag) {
        if (tag.equals(AdviceInfo.TXT_ENVIRONMENT)){
            return getResources().getDrawable(R.drawable.ic_environmentl_red);
        }
        if (tag.equals(AdviceInfo.TXT_HEALTH)){
            return getResources().getDrawable(R.drawable.ic_health_red);
        }
        if (tag.equals(AdviceInfo.TXT_ETHICS)){
            return getResources().getDrawable(R.drawable.ic_ethic_red);
        }
        return null;
    }
}
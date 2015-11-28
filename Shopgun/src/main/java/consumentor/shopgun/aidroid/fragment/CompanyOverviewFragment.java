package consumentor.shopgun.aidroid.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.view.CompanyActivity;
import consumentor.shopgun.aidroid.model.Company;
import consumentor.shopgun.aidroid.util.Util;

/**
 * Created by Simon on 11.09.13.
 */
public class CompanyOverviewFragment extends OverviewFragment {

    private static final String TAG = CompanyOverviewFragment.class.getName();
    private Company mCompany;

    public interface CompanyOverviewFragmentListener extends OverviewFragmentListener {
        void onCompanyInfoButtonPressed(View view);
        void onShowCompanyBrands(View view);
        void onShowCSR(View view);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        assert activity instanceof CompanyOverviewFragmentListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompany = (Company) getArguments().getSerializable(CompanyActivity.EXTRA_COMPANY);

    }

    private void initOverviewHeader(View view) {

        final TextView companyName = (TextView) view.findViewById(R.id.itemName);
        companyName.setText(mCompany.name);

        final ImageView companyLogotype = (ImageView) view.findViewById(R.id.itemLogotype);

        if (mCompany.isMember){
            if (!Util.isNullOrEmpty(mCompany.logotypeUrl)){
                mImageLoader.loadImage(mCompany.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        companyLogotype.setImageBitmap(loadedImage);
                        companyName.setVisibility(View.GONE);
                    }
                });
            }
        }else {
            companyLogotype.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mContentView =  inflater.inflate(R.layout.fragment_company_overview, container, false);

        View overviewHeader = mContentView.findViewById(R.id.overviewHeader);
        initOverviewHeader(overviewHeader);
        initMemberElements();
        initAdviceOverviewFragment(mCompany.getAdvicesRecursively());
        initTopWeightedAdvicesFragment(mCompany.getAdvicesRecursively());

        return mContentView;
    }

    private void initMemberElements() {
        TextView ifCompanyMemberInfo = (TextView) mContentView.findViewById(R.id.ifCompanyMemberInfo);
        if (!mCompany.isMember){
            View csrButton = mContentView.findViewById(R.id.csrButton);
            csrButton.setBackgroundColor(getResources().getColor(R.color.shopgun_gray));
            csrButton.setClickable(false);
            csrButton.setVisibility(View.GONE);
            ifCompanyMemberInfo.setVisibility(View.GONE);
        }
    }

}

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
import consumentor.shopgun.aidroid.view.BrandActivity;
import consumentor.shopgun.aidroid.model.Brand;
import consumentor.shopgun.aidroid.util.Util;

/**
 * Created by Simon on 11.09.13.
 */
public class BrandOverviewFragment extends OverviewFragment {

    private static final String TAG = BrandOverviewFragment.class.getName();
    private Brand mBrand;


    public interface BrandOverviewFragmentListener extends OverviewFragment.OverviewFragmentListener {
        public void onBrandInfoButtonPressed(View view);
        public void onStartCompanyActivity(View view);
        public void onShowProducts(View view);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        assert activity instanceof BrandOverviewFragmentListener;
        mListener = (BrandOverviewFragmentListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBrand = (Brand) getArguments().getSerializable(BrandActivity.EXTRA_BRAND);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mContentView =  inflater.inflate(R.layout.fragment_brand_overview, container, false);

        //initBrandInfoButton();
        View overviewHeader = mContentView.findViewById(R.id.overviewHeader);
        initOverviewHeader(overviewHeader);
        initOwnerButton();
        initAdviceOverviewFragment(mBrand.getAdvicesRecursively());
        initTopWeightedAdvicesFragment(mBrand.getAdvicesRecursively());
        return mContentView;
    }

    private void initOwnerButton() {
        View ownerButton = mContentView.findViewById(R.id.ownerButton);
        TextView ifBrandMemberInfo = (TextView) mContentView.findViewById(R.id.ifBrandMemberInfo);
        if (!mBrand.isMember){
            ifBrandMemberInfo.setVisibility(View.GONE);
        }
        if (mBrand.company != null){
            final TextView ownerName = (TextView) ownerButton.findViewById(R.id.companyName);
            ownerName.setText(mBrand.company.name);
            final ImageView ownerLogotype = (ImageView) ownerButton.findViewById(R.id.companyLogotype);
            if (mBrand.isMember){
                if (!Util.isNullOrEmpty(mBrand.company.logotypeUrl)){
                    mImageLoader.loadImage(mBrand.company.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener(){
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            ownerLogotype.setImageBitmap(loadedImage);
                            ownerName.setVisibility(View.GONE);
                        }
                    });
                }
            }else {
                ownerLogotype.setVisibility(View.GONE);
            }
        }else {
            ownerButton.setVisibility(View.GONE);
        }
    }

//    private void initBrandInfoButton() {
//        View brandInfoButton = mContentView.findViewById(R.id.brandInfoButton);
//        final TextView brandName = (TextView) brandInfoButton.findViewById(R.id.brandName);
//        brandName.setText(mBrand.name);
//        TextView brandDescription = (TextView)brandInfoButton.findViewById(R.id.ifBrandMemberInfo);
//        final ImageView brandLogotype = (ImageView) brandInfoButton.findViewById(R.id.brandLogotype);
//        if (mBrand.isMember){
//            if (!Util.isNullOrEmpty(mBrand.logotypeUrl)){
//                mImageLoader.loadImage(mBrand.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener(){
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        super.onLoadingComplete(imageUri, view, loadedImage);
//                        brandLogotype.setImageBitmap(loadedImage);
//                        brandName.setVisibility(View.GONE);
//                    }
//                });
//            }
//        }else {
//            brandLogotype.setVisibility(View.GONE);
//        }
//    }

    private void initOverviewHeader(View view) {

        final TextView brandName = (TextView) view.findViewById(R.id.itemName);
        brandName.setText(mBrand.name);

        final ImageView brandLogotype = (ImageView) view.findViewById(R.id.itemLogotype);

        if (mBrand.isMember){
            if (!Util.isNullOrEmpty(mBrand.logotypeUrl)){
                mImageLoader.loadImage(mBrand.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        brandLogotype.setImageBitmap(loadedImage);
                        brandName.setVisibility(View.GONE);
                    }
                });
            }
        }else {
            brandLogotype.setVisibility(View.GONE);
        }
    }

}

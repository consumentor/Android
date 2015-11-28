package consumentor.shopgun.aidroid.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.view.BrandActivity;
import consumentor.shopgun.aidroid.model.Brand;
import consumentor.shopgun.aidroid.model.Company;
import consumentor.shopgun.aidroid.util.Util;

/**
 * Created by Simon on 11.09.13.
 */
public class BrandFactsFragment extends BaseFragment {

    private static final String TAG = CompanyInfoFragment.class.getName();
    private Brand mBrand;
    private View mOwnerButton;
    private View mView;
    private TextView mBrandAddressTextView;
    private TextView mBrandPhoneTextView;
    private TextView mBrandEmailTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBrand = (Brand) getArguments().getSerializable(BrandActivity.EXTRA_BRAND);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_brand_facts, container, false);

        initOwnerButton();

        initBrandLogotype();

        mBrandAddressTextView = (TextView) mView.findViewById(R.id.brandAddress);
        if (mBrand.company != null){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(Util.isNullOrEmpty(mBrand.company.postalCode) ? "" : mBrand.company.postalCode +" ");
            stringBuilder.append(Util.isNullOrEmpty(mBrand.company.city) ? "" : mBrand.company.city +"\n");
            stringBuilder.append(Util.isNullOrEmpty(mBrand.company.address) ? "" : mBrand.company.address +"\n");
            mBrandAddressTextView.setText(stringBuilder.toString());
    
            mBrandPhoneTextView = (TextView) mView.findViewById(R.id.brandPhone);
            if (!Util.isNullOrEmpty(mBrand.company.phone)){
                mBrandPhoneTextView.setText(mBrand.company.phone);
            }
            else{
                mBrandPhoneTextView.setVisibility(View.GONE);
            }
    
            mBrandEmailTextView = (TextView) mView.findViewById(R.id.brandEmail);
            if (!Util.isNullOrEmpty(mBrand.company.email)){
                mBrandEmailTextView.setText(mBrand.company.email);
            }
            else{
                mBrandEmailTextView.setVisibility(View.GONE);
            }
        }

        return mView;
    }

    private void initBrandLogotype() {
        final TextView brandName = (TextView) mView.findViewById(R.id.brandName);
        brandName.setText(mBrand.name);
        final ImageView brandLogotype = (ImageView) mView.findViewById(R.id.brandLogotype);
        if (mBrand.isMember){
            ImageLoader.getInstance().loadImage(mBrand.company.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    brandLogotype.setImageBitmap(loadedImage);
                    brandName.setVisibility(View.GONE);
                }
            });
        }else {
            brandLogotype.setVisibility(View.GONE);
        }
    }

    private void initOwnerButton() {
        mOwnerButton = mView.findViewById(R.id.ownerButton);

        if (mBrand != null && mBrand.company != null){
            Company company = mBrand.company;
            final ImageView companyLogotype = (ImageView) mOwnerButton.findViewById(R.id.companyLogotype);
            final TextView companyName = (TextView) mOwnerButton.findViewById(R.id.companyName);
            companyName.setText(mBrand.company.name);
            if (mBrand.company.isMember && !Util.isNullOrEmpty(mBrand.company.logotypeUrl)){
                ImageLoader.getInstance().loadImage(mBrand.company.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        companyLogotype.setImageBitmap(loadedImage);
                        companyName.setVisibility(View.GONE);
                    }
                });
            }else{
                companyLogotype.setVisibility(View.GONE);
            }
        }
        else {
            mOwnerButton.setVisibility(View.GONE);
        }
    }

    public interface BrandFactsFragmentListener {
        public void onStartCompanyActivity(View view);
    }
}

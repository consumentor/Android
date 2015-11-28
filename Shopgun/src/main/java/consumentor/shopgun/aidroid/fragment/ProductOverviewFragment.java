package consumentor.shopgun.aidroid.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import consumentor.shopgun.aidroid.customview.CircleDiagram;
import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.view.CertificationMarkActivity;
import consumentor.shopgun.aidroid.view.ProductActivity;
import consumentor.shopgun.aidroid.model.CertificationMark;
import consumentor.shopgun.aidroid.model.Product;
import consumentor.shopgun.aidroid.util.Util;

/**
 * Created by Simon on 29.08.13.
 */
public class ProductOverviewFragment extends OverviewFragment {

    private static final String TAG = ProductOverviewFragment.class.getName();
    private Product mProduct;

    private View mCertifiedAlternativesButton;
    private ImageView mCompanyLogotype;
    private TextView mProductOrigin;

    private ImageView mProductImageView;
    private TextView mTableOfContentTextView;

    private ImageLoader mImageLoader;
    private TextView mCompanyNameTextView;
    private ImageView mBrandLogotype;
    private TextView mBrandNameTextView;

    private ProductOverviewFragmentListener mListener;

    public interface ProductOverviewFragmentListener extends OverviewFragmentListener {
        public void onShowCertifiedAlternatives(View view);
        public void onShowProductFacts(View view);
        public void onStartCompanyActivity(View view);
        public void onShowProductInfo(View view);
        public void onStartBrandActivity(View view);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        assert activity instanceof ProductOverviewFragmentListener;
        mListener = (ProductOverviewFragmentListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProduct = (Product) getArguments().getSerializable(ProductActivity.EXTRA_PRODUCT);
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        try {
            mContentView =  inflater.inflate(R.layout.fragment_product_overview, container, false);
        } catch (InflateException e) {
            Log.d(TAG, e.getMessage());
        }

        View productMainInfoItem = mContentView.findViewById(R.id.productMainInfoItem);
        initProductMainInfoItem(productMainInfoItem);

        //initProductFactsButton(mContentView);

        //initCertificationMarks(mContentView);

        initAdviceOverviewFragment(mProduct.getAdvicesRecursively());

        initTopWeightedAdvicesFragment(mProduct.getAdvicesRecursively());

        View moreInfoArea = mContentView.findViewById(R.id.moreInformationArea);
        initCompanyAndBrandButtons(moreInfoArea);

        return mContentView;
    }

    private void initProductMainInfoItem(View view) {

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onShowProductFacts(v);
            }
        });

        View circleDiagramContainer = view.findViewById(R.id.circleDiagramContainer);
        circleDiagramContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OverviewFragmentListener)getActivity()).onShowAdvices(view);
            }
        });

        TextView productNameTV = (TextView) view.findViewById(R.id.productName);
        productNameTV.setText(mProduct.toString());

        if (mProduct.brand != null) {
            TextView brandNameTV = (TextView) view.findViewById(R.id.brandName);
            if (!Util.isNullOrEmpty(mProduct.quantity) && !Util.isNullOrEmpty(mProduct.quantityUnit)){
                brandNameTV.setText(String.format("%s, %s %s", mProduct.brand.name, mProduct.quantity, mProduct.quantityUnit));
            }else{
                brandNameTV.setText(mProduct.brand.name);
            }
        }

//        TextView origin = (TextView) view.findViewById(R.id.productOrigin);
//        if(!Util.isNullOrEmpty(mProduct.origin)){
//            origin.setText(mProduct.origin);
//        }

        final ImageView productImageView = (ImageView) view.findViewById(R.id.productImage);
        mImageLoader.loadImage(mProduct.imageMedium, new ImageSize(100, 100), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                productImageView.setImageBitmap(loadedImage);
            }
        });

//        CircleDiagram circleDiagram = (CircleDiagram) view.findViewById(R.id.circleDiagram);
//        circleDiagram.init(mProduct.getAdvicesRecursively());

        View lacksCertificationInfo = view.findViewById(R.id.lacksCertificationInfo);
        lacksCertificationInfo.setVisibility(View.GONE);
        mCertifiedAlternativesButton = mContentView.findViewById(R.id.certifiedAlternativesButton);
        mCertifiedAlternativesButton.setVisibility(View.GONE);
        if (!mProduct.certificationMarks.isEmpty()){
            initCertificationMarks(view);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

//    private void initProductFactsButton(View view) {
//        mProductOrigin = (TextView) view.findViewById(R.id.productOrigin);
//        if (!Util.isNullOrEmpty(mProduct.origin)){
//            mProductOrigin.setText(mProduct.origin);
//        }else {
//            View originInfo = mContentView.findViewById(R.id.originInfo);
//            originInfo.setVisibility(View.GONE);
//        }
//        mTableOfContentTextView = (TextView) view.findViewById(R.id.tableOfContent);
//        //mTableOfContentTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
//        if (!Util.isNullOrEmpty(mProduct.tableOfContents)){
//            mTableOfContentTextView.setText(mProduct.tableOfContents);
//            mTableOfContentTextView.setTypeface(null, Typeface.NORMAL);
//        }
//        mProductImageView = (ImageView) view.findViewById(R.id.productImage);
//        mImageLoader.loadImage(mProduct.imageLarge, new ImageSize(300, 300), new SimpleImageLoadingListener(){
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                mProductImageView.setImageBitmap(loadedImage);
//            }
//        });
//    }

    private void initCompanyAndBrandButtons(View view) {

        View producerButton = view.findViewById(R.id.producerButton);
        View productInfoButton = mContentView.findViewById(R.id.productInfoButton);
        mBrandLogotype = (ImageView) view.findViewById(R.id.brandLogotype);
        mCompanyLogotype = (ImageView) view.findViewById(R.id.companyLogotype);

        if(mProduct.brand != null){

            mBrandNameTextView = (TextView) view.findViewById(R.id.brandName);
            mBrandNameTextView.setText(mProduct.brand.name);
            if (mProduct.brand.isMember){
                mImageLoader.loadImage(mProduct.brand.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View mView, Bitmap loadedImage) {
                        mBrandLogotype.setImageBitmap(loadedImage);
                        mBrandNameTextView.setVisibility(View.GONE);
                    }
                });
            }else{
                mBrandLogotype.setVisibility(View.GONE);
                productInfoButton.setVisibility(View.GONE);
            }
            if(mProduct.brand.company != null){
                mCompanyNameTextView = (TextView) view.findViewById(R.id.companyName);
                mCompanyNameTextView.setText(mProduct.brand.company.name);
                if (!Util.isNullOrEmpty(mProduct.brand.company.logotypeUrl) && mProduct.brand.company.isMember){
                    mImageLoader.loadImage(mProduct.brand.company.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener(){
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            mCompanyLogotype.setImageBitmap(loadedImage);
                            mCompanyNameTextView.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    mCompanyLogotype.setVisibility(View.GONE);
                }
            }
            else {
                producerButton.setVisibility(View.GONE);
            }

        }else {
            mCompanyLogotype.setVisibility(View.GONE);
        }
    }

    private void initCertificationMarks(View view) {
        mCertifiedAlternativesButton = mContentView.findViewById(R.id.certifiedAlternativesButton);
        final LinearLayout certificationMarkContainer = (LinearLayout) view.findViewById(R.id.certificationMarkContainer);
        // clear any existing certification marks
        certificationMarkContainer.removeAllViews();
        if (mProduct.certificationMarks.size() > 0){
            for(final CertificationMark certificationMark : mProduct.certificationMarks){
                final ImageView certificationMarkButton = new ImageView(getActivity());
                certificationMarkButton.setBackgroundResource(android.R.drawable.list_selector_background);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                certificationMarkButton.setLayoutParams(layoutParams);
                certificationMarkButton.setPadding(10, 10, 10, 10);
                certificationMarkButton.setAdjustViewBounds(true);
                certificationMarkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), CertificationMarkActivity.class);
                        intent.putExtra(CertificationMarkActivity.EXTRA_CERTIFICATIONMARK_ID, certificationMark.id);
                        startActivity(intent);
                    }
                });
                mImageLoader.loadImage(certificationMark.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        certificationMarkButton.setImageBitmap(loadedImage);
                        certificationMarkContainer.addView(certificationMarkButton, 0);
                    }
                });

                mCertifiedAlternativesButton.setVisibility(View.GONE);
            }
        }else{
            View certificationMarkScrollView = view.findViewById(R.id.certificationMarkScrollView);
            certificationMarkScrollView.setVisibility(View.GONE);
            mCertifiedAlternativesButton.setVisibility(View.VISIBLE);
        }
    }
}

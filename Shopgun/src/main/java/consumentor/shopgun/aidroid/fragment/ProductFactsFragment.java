package consumentor.shopgun.aidroid.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import consumentor.shopgun.aidroid.model.CertificationMark;
import consumentor.shopgun.aidroid.view.CertificationMarkActivity;
import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.view.ProductActivity;
import consumentor.shopgun.aidroid.model.Brand;
import consumentor.shopgun.aidroid.model.Company;
import consumentor.shopgun.aidroid.model.Product;
import consumentor.shopgun.aidroid.util.Util;

/**
 * Created by Simon on 29.08.13.
 */
public class ProductFactsFragment extends BaseFragment {

    private Product mProduct;

    private View mProducerButton;
    private View mBrandButton;
    private TextView mOriginTextView;
    private TextView mIngredientsTextView;
    private TextView mNutritionTextView;
    private ImageLoader mImageLoader;
    private View mView;
    private View mCertifiedAlternativesButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProduct = (Product) getArguments().getSerializable(ProductActivity.EXTRA_PRODUCT);
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_product_facts, container, false);

        initProductNameAndBrand();

        initProductImage();

        initProducerButton();

        initBrandButton();

        initCertificationMarks();

        initOriginInfo();

        initIngredientInfo();

        initNutritionInfo();

        return mView;
    }

    private void initProductNameAndBrand() {
        TextView productNameTV = (TextView) mView.findViewById(R.id.productName);
        productNameTV.setText(mProduct.toString());

        if (mProduct.brand != null) {
            TextView brandNameTV = (TextView) mView.findViewById(R.id.brandName);
            if (!Util.isNullOrEmpty(mProduct.quantity) && !Util.isNullOrEmpty(mProduct.quantityUnit)){
                brandNameTV.setText(String.format("%s, %s %s", mProduct.brand.name, mProduct.quantity, mProduct.quantityUnit));
            }else{
                brandNameTV.setText(mProduct.brand.name);
            }
        }
    }

    private void initProductImage() {
        final ImageView productImage = (ImageView)mView.findViewById(R.id.productImage);
        mImageLoader.loadImage(mProduct.imageLarge, new ImageSize(300, 300), new SimpleImageLoadingListener(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                productImage.setImageBitmap(loadedImage);
            }
        });
    }

    private void initNutritionInfo() {
        View nutritionInfo = mView.findViewById(R.id.nutritionInfo);
        mNutritionTextView = (TextView) nutritionInfo.findViewById(R.id.productNutrition);
        if(!Util.isNullOrEmpty(mProduct.nutrition)){
            mNutritionTextView.setText(mProduct.nutrition);
        }
        else {
            nutritionInfo.setVisibility(View.GONE);
        }
    }

    private void initOriginInfo() {
        View originInfo = mView.findViewById(R.id.originInfo);
        mOriginTextView = (TextView) originInfo.findViewById(R.id.productOrigin);
        if (!Util.isNullOrEmpty(mProduct.origin)){
            mOriginTextView.setText(mProduct.origin);
        }
        else {
            originInfo.setVisibility(View.GONE);
        }
    }
    private void initCertificationMarks() {
        mCertifiedAlternativesButton = mView.findViewById(R.id.certifiedAlternativesButton);
        final LinearLayout certificationMarkContainer = (LinearLayout) mView.findViewById(R.id.certificationMarkContainer);
        // clear any existing certification marks
        certificationMarkContainer.removeAllViews();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, displayMetrics);
        if (mProduct.certificationMarks.size() > 0){
            for(final CertificationMark certificationMark : mProduct.certificationMarks){
                final ImageView certificationMarkButton = new ImageView(getActivity());

                certificationMarkButton.setBackgroundResource(R.drawable.selector_button);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                certificationMarkButton.setLayoutParams(layoutParams);
                certificationMarkButton.setPadding(padding, padding, padding, padding);
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
            View certificationMarkScrollView = mView.findViewById(R.id.certificationMarkScrollView);
            certificationMarkScrollView.setVisibility(View.GONE);
            mCertifiedAlternativesButton.setVisibility(View.VISIBLE);
        }
    }

    private void initIngredientInfo() {
        View ingredientInfo = mView.findViewById(R.id.ingredientInfo);
        mIngredientsTextView = (TextView) ingredientInfo.findViewById(R.id.productIngredients);
        if (!Util.isNullOrEmpty(mProduct.tableOfContents)){
            mIngredientsTextView.setTypeface(null, Typeface.NORMAL);
            mIngredientsTextView.setText(mProduct.tableOfContents);
        }
    }

    private void initBrandButton() {
        mBrandButton = mView.findViewById(R.id.brandButton);
        if(mProduct.brand != null){
            Brand brand = mProduct.brand;
            final TextView brandName = (TextView) mBrandButton.findViewById(R.id.brandName);
            brandName.setText(brand.name);

            if (brand.company != null && brand.company.isMember){
                final ImageView brandLogotype = (ImageView) mBrandButton.findViewById(R.id.brandLogotype);
                if (!Util.isNullOrEmpty(brand.logotypeUrl)){
                    mImageLoader.loadImage(brand.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener(){
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            brandLogotype.setImageBitmap(loadedImage);
                            brandName.setVisibility(View.GONE);
                        }
                    });
                }else{
                    brandLogotype.setVisibility(View.GONE);
                }
            }
        }
        else {
            mBrandButton.setVisibility(View.GONE);
        }
    }

    private void initProducerButton() {
        mProducerButton = mView.findViewById(R.id.producerButton);

        if (mProduct.brand != null && mProduct.brand.company != null){
            Company producer = mProduct.brand.company;
            final ImageView producerLogotype = (ImageView) mProducerButton.findViewById(R.id.companyLogotype);
            final TextView producerName = (TextView) mProducerButton.findViewById(R.id.companyName);
            producerName.setText(producer.name);
            if (producer.isMember && !Util.isNullOrEmpty(producer.logotypeUrl)){
                mImageLoader.loadImage(producer.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        producerLogotype.setImageBitmap(loadedImage);
                        producerName.setVisibility(View.GONE);
                    }
                });
            }else{
                producerLogotype.setVisibility(View.GONE);
            }
        }
        else {
            mProducerButton.setVisibility(View.GONE);
        }
    }
}

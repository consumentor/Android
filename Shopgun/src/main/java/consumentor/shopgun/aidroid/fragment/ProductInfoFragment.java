package consumentor.shopgun.aidroid.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.view.ProductActivity;
import consumentor.shopgun.aidroid.model.Product;
import consumentor.shopgun.aidroid.util.Util;


/**
 * Created by Simon on 29.08.13.
 */
public class ProductInfoFragment extends BaseFragment{

    private static final String TAG = ProductInfoFragment.class.getName();
    private Product mProduct;
    private TextView mProductDescriptionView;
    private View mProductHomepageButton;
    private WebView mProductVideoWebView;
    private ImageView mProductImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProduct = (Product) getArguments().getSerializable(ProductActivity.EXTRA_PRODUCT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_info, container, false);

        mProductImageView = (ImageView)view.findViewById(R.id.productImage);
        if (!Util.isNullOrEmpty(mProduct.imageLarge)){
            ImageLoader.getInstance().loadImage(mProduct.imageLarge, new ImageSize(300, 300), new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    mProductImageView.setImageBitmap(loadedImage);
                }
            });
        }
        else {
            mProductImageView.setVisibility(View.GONE);
        }

        initProductDescription(view);

        initProductVideo(view);


        return view;
    }

    private void initProductDescription(View view) {
        mProductDescriptionView = (TextView) view.findViewById(R.id.description);
        mProductHomepageButton = view.findViewById(R.id.productHomepageButton);
        View disclaimer = view.findViewById(R.id.productInfoDisclaimer);
        View noMemberInfo = view.findViewById(R.id.productProducerNoMemberInfo);
        if (mProduct.brand.isMember){
            noMemberInfo.setVisibility(View.GONE);
            if(!Util.isNullOrEmpty(mProduct.description)){
                mProductDescriptionView.setText(mProduct.description);
            }

            if(mProduct.productHomepage != null && !mProduct.productHomepage.isEmpty()){
                mProductHomepageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(mProduct.productHomepage));
                        startActivity(i);
                    }
                });
            }
            else {
                mProductHomepageButton.setVisibility(View.GONE);
            }
        }
        else {
            mProductDescriptionView.setVisibility(View.GONE);
            mProductHomepageButton.setVisibility(View.GONE);
            disclaimer.setVisibility(View.GONE);
        }


    }

    private void initProductVideo(View view) {
        mProductVideoWebView = (WebView) view.findViewById(R.id.productVideo);
        View productVideoContainer = view.findViewById(R.id.productVideoContainer);
        if (mProduct.brand.isMember){

            mProductVideoWebView.getSettings().setJavaScriptEnabled(true);
            mProductVideoWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
            mProductVideoWebView.setWebChromeClient(new WebChromeClient());
            String youtubeVideoId = Util.isNullOrEmpty(mProduct.youtubeVideoId) ? "er4W4ce03fY" : mProduct.youtubeVideoId;
            mProductVideoWebView.loadUrl("http://www.youtube.com/embed/" + youtubeVideoId + "?autoplay=1&vq=small");
        }
        else {
            productVideoContainer.setVisibility(View.GONE);
        }
    }
}

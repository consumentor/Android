package consumentor.shopgun.aidroid.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
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
import consumentor.shopgun.aidroid.view.BrandActivity;
import consumentor.shopgun.aidroid.model.Brand;
import consumentor.shopgun.aidroid.util.Util;

/**
 * Created by Simon on 11.09.13.
 */
public class BrandInfoFragment extends BaseFragment {

    private static final String TAG = CompanyInfoFragment.class.getName();
    private Brand mBrand;
    private TextView mDescriptionTextView;
    private TextView mBrandHomepageButton;
    private WebView mBrandVideoWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBrand = (Brand) getArguments().getSerializable(BrandActivity.EXTRA_BRAND);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brand_info, container, false);

        final TextView brandName = (TextView) view.findViewById(R.id.brandName);
        brandName.setText(mBrand.name);
        View brandNoMember = view.findViewById(R.id.brandNoMemberInfo);
        View brandInfoDisclaimer = view.findViewById(R.id.brandInfoDisclaimer);
        View content = view.findViewById(R.id.content);
        final ImageView brandLogotype = (ImageView) view.findViewById(R.id.brandLogotype);
        if (mBrand.isMember){
            brandNoMember.setVisibility(View.GONE);
            if (!Util.isNullOrEmpty(mBrand.logotypeUrl)){
                ImageLoader.getInstance().loadImage(mBrand.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        brandLogotype.setImageBitmap(loadedImage);
                        brandName.setVisibility(View.GONE);
                    }
                });
            }
        }
        else{
            content.setVisibility(View.GONE);
            brandInfoDisclaimer.setVisibility(View.GONE);
        }

        mDescriptionTextView = (TextView) view.findViewById(R.id.description);
        if (mBrand.isMember && !Util.isNullOrEmpty(mBrand.description)){
            mDescriptionTextView.setTypeface(null, Typeface.NORMAL);
            mDescriptionTextView.setText(Util.trim(Html.fromHtml(mBrand.description)));
        }

        mBrandHomepageButton = (TextView) view.findViewById(R.id.brandHomepageButton);
        if (mBrand.homepage != null && !mBrand.homepage.isEmpty()){
            mBrandHomepageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String homePageUrl = mBrand.homepage.startsWith("http://") ? mBrand.homepage : "http://" +mBrand.homepage;
                    Uri uri = Uri.parse(homePageUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }else{
            mBrandHomepageButton.setVisibility(View.GONE);
        }

        mBrandVideoWebView = (WebView) view.findViewById(R.id.brandVideo);
        if (mBrand.isMember && !Util.isNullOrEmpty(mBrand.youtubeVideoId)){
            mBrandVideoWebView.getSettings().setJavaScriptEnabled(true);
            mBrandVideoWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
            mBrandVideoWebView.setWebChromeClient(new WebChromeClient());
            mBrandVideoWebView.loadUrl("http://www.youtube.com/embed/" + mBrand.youtubeVideoId + "?autoplay=1&vq=small");
        }else{
            mBrandVideoWebView.setVisibility(View.GONE);
        }

        return view;
    }
}
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
import consumentor.shopgun.aidroid.view.CompanyActivity;
import consumentor.shopgun.aidroid.model.Company;
import consumentor.shopgun.aidroid.util.Util;

/**
 * Created by Simon on 11.09.13.
 */
public class CompanyInfoFragment  extends BaseFragment {

    private static final String TAG = CompanyInfoFragment.class.getName();
    private Company mCompany;
    private TextView mDescriptionTextView;
    private TextView mCompanyHomepageButton;
    private WebView mCompanyVideoWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCompany = (Company) getArguments().getSerializable(CompanyActivity.EXTRA_COMPANY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_info, container, false);
        View content = view.findViewById(R.id.content);
        View companyNoMemberInfo = view.findViewById(R.id.companyNoMemberInfo);
        final TextView companyName = (TextView) view.findViewById(R.id.companyName);
        companyName.setText(mCompany.name);
        final ImageView companyLogotype = (ImageView) view.findViewById(R.id.companyLogotype);
        View disclaimer = view.findViewById(R.id.companyInfoDisclaimer);
        if (mCompany.isMember){
            companyNoMemberInfo.setVisibility(View.GONE);
            if (!Util.isNullOrEmpty(mCompany.logotypeUrl)){
                ImageLoader.getInstance().loadImage(mCompany.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        companyLogotype.setImageBitmap(loadedImage);
                        companyName.setVisibility(View.GONE);
                    }
                });
            }
        }
        else {
            disclaimer.setVisibility(View.GONE);
            content.setVisibility(View.GONE);
        }

        mDescriptionTextView = (TextView) view.findViewById(R.id.description);
        if (mCompany.isMember && !Util.isNullOrEmpty(mCompany.description)){
            mDescriptionTextView.setTypeface(null, Typeface.NORMAL);
            mDescriptionTextView.setText(Util.trim(Html.fromHtml(mCompany.description)));
        }

        mCompanyHomepageButton = (TextView) view.findViewById(R.id.companyHomepageButton);
        if (mCompany.homepage != null && !mCompany.homepage.isEmpty()){
            mCompanyHomepageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String homePageUrl = mCompany.homepage.startsWith("http://") ? mCompany.homepage : "http://" +mCompany.homepage;
                    Uri uri = Uri.parse(homePageUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }else{
            mCompanyHomepageButton.setVisibility(View.GONE);
        }

        mCompanyVideoWebView = (WebView) view.findViewById(R.id.companyVideo);

        if (mCompany.isMember && !Util.isNullOrEmpty(mCompany.youtubeVideoId)){
            mCompanyVideoWebView.getSettings().setJavaScriptEnabled(true);
            mCompanyVideoWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
            mCompanyVideoWebView.setWebChromeClient(new WebChromeClient());
            mCompanyVideoWebView.loadUrl("http://www.youtube.com/embed/" + mCompany.youtubeVideoId + "?autoplay=1&vq=small");
        }else{
            mCompanyVideoWebView.setVisibility(View.GONE);
        }

        return view;
    }
}
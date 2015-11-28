package consumentor.shopgun.aidroid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import consumentor.shopgun.aidroid.view.R;

/**
 * Created by Simon on 2014-01-27.
 */
public class WebViewFragment extends BaseFragment {

    public static final String EXTRA_URL = "consumentor.shopgun.extra.url";

    private String mUrl;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUrl = getArguments().getString(EXTRA_URL);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        WebView webView = (WebView) view.findViewById(R.id.webView);
        webView.loadUrl(mUrl);
        return view;
    }
}

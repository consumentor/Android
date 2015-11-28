package consumentor.shopgun.aidroid.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.view.IngredientActivity;

import consumentor.shopgun.aidroid.model.Ingredient;

import consumentor.shopgun.aidroid.model.WikipediaArticle;
import consumentor.shopgun.aidroid.util.JsonDateDeserializer;
import consumentor.shopgun.aidroid.util.RESTLoader;

import java.util.Date;

/**
 * Created by Simon on 10.09.13.
 */
public class IngredientInfoFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<RESTLoader.RESTResponse> {

    private static final String TAG = ProductInfoFragment.class.getName();
    private Ingredient mIngredient;
    private TextView mArticleTextView;
    private WikipediaArticle mWikiArticle;
    private static final int LOADER_GET_WIKIARTICLE = 0x1;
    private WebView mWikipediaWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIngredient = (Ingredient) getArguments().getSerializable(IngredientActivity.EXTRA_INGREDIENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredient_info, container, false);
        mArticleTextView = (TextView) view.findViewById(R.id.description);
        mWikipediaWebView = (WebView) view.findViewById(R.id.wikipediaWebView);
        mWikipediaWebView.getSettings().setJavaScriptEnabled(true);
        mWikipediaWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        mWikipediaWebView.loadUrl("http://sv.wikipedia.org/wiki/" +mIngredient.name);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Uri loginUri = Uri.parse("http://sv.wikipedia.org/w/api.php?format=json&action=mobileview&sections=0&prop=text&page=" + mIngredient.name);

        //Todo: Only load when savedInstanceState == null
        Bundle args = new Bundle();
        Bundle params = new Bundle();
        args.putParcelable(RESTLoader.ARGS_URI, loginUri);
        args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.GET);
        args.putParcelable(RESTLoader.ARGS_PARAMS, params);
        // Initialize the Loader.
        //getLoaderManager().initLoader(LOADER_GET_WIKIARTICLE, args, this);
    }

    @Override
    public Loader<RESTLoader.RESTResponse> onCreateLoader(int id, Bundle args) {
        if (args != null && args.containsKey(RESTLoader.ARGS_URI) && args.containsKey(RESTLoader.ARGS_PARAMS)) {
            Uri action = args.getParcelable(RESTLoader.ARGS_URI);
            RESTLoader.HTTPVerb verb = args.containsKey(RESTLoader.ARGS_VERB) ? (RESTLoader.HTTPVerb) args.getSerializable(RESTLoader.ARGS_VERB) : RESTLoader.HTTPVerb.GET;
            Bundle params = args.getParcelable(RESTLoader.ARGS_PARAMS);

            return new RESTLoader(getActivity(), verb, action, params);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<RESTLoader.RESTResponse> loader, RESTLoader.RESTResponse data) {
        int    code = data.getCode();
        String json = data.getData();

        switch (loader.getId()){

            case LOADER_GET_WIKIARTICLE:{
                if (code == 200 && !json.equals("")) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
                    mWikiArticle = gson.fromJson(json, WikipediaArticle.class);
                    if (mWikiArticle.sections != null){
                        mArticleTextView.setText(Html.fromHtml(mWikiArticle.sections.sections.get(0).text));
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Failed to load Wikipedia article!", Toast.LENGTH_SHORT).show();
                }
                getLoaderManager().destroyLoader(LOADER_GET_WIKIARTICLE);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {

    }
}

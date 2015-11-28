package consumentor.shopgun.aidroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.view.CompanyActivity;
import consumentor.shopgun.aidroid.model.Company;
import consumentor.shopgun.aidroid.util.RESTLoader;

import java.util.List;

/**
 * Created by Simon on 2013-09-19.
 */
public class CompanyListFragment extends BaseListFragment implements
        LoaderManager.LoaderCallbacks<RESTLoader.RESTResponse> {

    private static final int LOADER_GET_COMPANIES = 0x1;
    private List<Company> mCompanies;
    private CompanyAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_list, container, false);

        Uri loginUri = Uri.parse(RESTLoader.BASE_URL +"/companies?isMember=true" );

        Bundle args = new Bundle();
        Bundle params = new Bundle();
        args.putParcelable(RESTLoader.ARGS_URI, loginUri);
        args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.GET);
        args.putParcelable(RESTLoader.ARGS_PARAMS, params);
        // Initialize the Loader.
        getLoaderManager().initLoader(LOADER_GET_COMPANIES, args, this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                int companyId = (int) mAdapter.getItemId(pos);

                Intent intent = new Intent(getActivity(), CompanyActivity.class);
                intent.putExtra(CompanyActivity.EXTRA_COMPANY_ID, companyId);
                startActivity(intent);
            }
        });
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

            case LOADER_GET_COMPANIES:{
                if (code == 200 && !json.equals("")) {
                    Gson gson = new Gson();
                    mCompanies = gson.fromJson(json, new TypeToken<List<Company>>(){}.getType());
                    mAdapter = new CompanyAdapter(getActivity(), mCompanies);
                    setListAdapter(mAdapter);

                }
                else {
                    Toast.makeText(getActivity(), "Failed to load books!", Toast.LENGTH_SHORT).show();
                }
                getLoaderManager().destroyLoader(LOADER_GET_COMPANIES);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {

    }


    private class CompanyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private Context mContext;
        private List<Company> mCompanies;

        public CompanyAdapter(Context context, List<Company> companies) {
            this.mContext  		  = context;
            this.mCompanies = companies;
            this.mInflater 		  = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        /**
         * This is where the magic happens. GetView creates inflates
         * our rows. This is the optimal way of doing this since we
         * allocate very little memory. The holder finds the views
         * in the xml and saves thoses pointers. We associate a holder
         * with every convertView and once all the views are created
         * the pointers are re-used. Gorgeous.
         */
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;

            // Should be used if we switch the layout of the advice_list_item to relative.
            // convertView = mInflater.inflate(R.layout.advice_list_item, parent, false);
            holder = new ViewHolder();
            if(convertView == null){
                convertView 			 = mInflater.inflate(R.layout.company_list_item, null);
                holder.companyName = (TextView) convertView.findViewById(R.id.companyName);
                holder.companyLogotype = (ImageView) convertView.findViewById(R.id.companyLogotype);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.companyName.setText(mCompanies.get(position).name);
            final ViewHolder finalHolder = holder;
            ImageLoader.getInstance().loadImage(mCompanies.get(position).logotypeUrl,new ImageSize(100, 100), new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    finalHolder.companyLogotype.setImageBitmap(loadedImage);
                }
            });

            return convertView;
        }

        public int getCount() {
            return mCompanies.size();
        }

        public Object getItem(int position) {
            return mCompanies.get(position);
        }

        public long getItemId(int position) {
            return mCompanies.get(position).id;
        }
    }

    /**
     * A holder of the subviews so that android doesn't have to fetch
     * them everytime. Xml-parsing is relatively expensive.
     * @author Johan
     *
     */
    class ViewHolder {
        TextView companyName;
        ImageView companyLogotype;
    }
}

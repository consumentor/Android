package consumentor.shopgun.aidroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.Gravity;
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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.view.BrandActivity;
import consumentor.shopgun.aidroid.view.CompanyActivity;
import consumentor.shopgun.aidroid.model.Brand;
import consumentor.shopgun.aidroid.model.Company;
import consumentor.shopgun.aidroid.util.JsonDateDeserializer;
import consumentor.shopgun.aidroid.util.RESTLoader;

import java.util.Date;
import java.util.List;

/**
 * Created by Simon on 11.09.13.
 */
public class BrandListFragment extends BaseListFragment implements LoaderManager.LoaderCallbacks<RESTLoader.RESTResponse> {


    private static final int LOADER_GET_COMPANY_BRANDS = 0x1;
    private Company mCompany;

    private List<Brand> mBrands;
    private BrandAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(CompanyActivity.EXTRA_COMPANY)){
            mCompany = (Company) getArguments().getSerializable(CompanyActivity.EXTRA_COMPANY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_list, container, false);

        if(mCompany != null){
            retrieveBrandsForCompany();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                int brandId = (int) mAdapter.getItemId(pos);

                Intent intent = new Intent(getActivity(), BrandActivity.class);
                intent.putExtra(BrandActivity.EXTRA_BRAND_ID, brandId);
                startActivity(intent);
            }
        });
    }

    private void retrieveBrandsForCompany() {
        Uri uri = Uri.parse(RESTLoader.BASE_URL +"/companies/" + mCompany.id +"/brands");

        Bundle args = new Bundle();
        Bundle params = new Bundle();
        args.putParcelable(RESTLoader.ARGS_URI, uri);
        args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.GET);
        args.putParcelable(RESTLoader.ARGS_PARAMS, params);
        // Initialize the Loader.
        getLoaderManager().initLoader(LOADER_GET_COMPANY_BRANDS, args, this);
    }

    @Override
    public Loader<RESTLoader.RESTResponse> onCreateLoader(int i, Bundle args) {
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

            case LOADER_GET_COMPANY_BRANDS:{
                if (code == 200 && !json.equals("")) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
                    mBrands = gson.fromJson(json, new TypeToken<List<Brand>>(){}.getType());
                    mAdapter = new BrandAdapter(getActivity(), mBrands);
                    setListAdapter(mAdapter);

                }
                else {
                    Toast.makeText(getActivity(), "Failed to load brands!", Toast.LENGTH_SHORT).show();
                }
                getLoaderManager().destroyLoader(LOADER_GET_COMPANY_BRANDS);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> restResponseLoader) {

    }

    private class BrandAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private Context mContext;
        private List<Brand> mBrands;

        public BrandAdapter(Context context, List<Brand> brands) {
            this.mContext  		  = context;
            this.mBrands = brands;
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
                convertView 			 = mInflater.inflate(R.layout.brand_list_item, null);
                holder.brandName = (TextView) convertView.findViewById(R.id.brandName);
                holder.brandLogotype = (ImageView) convertView.findViewById(R.id.brandLogotype);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Brand brand = mBrands.get(position);
            final ViewHolder finalHolder = holder;
            finalHolder.brandName.setText(brand.name);
            if (brand.isMember){
                ImageLoader.getInstance().loadImage(brand.logotypeUrl,new ImageSize(100, 100), new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        finalHolder.brandName.setVisibility(View.GONE);
                        finalHolder.brandLogotype.setImageBitmap(loadedImage);
                    }
                });
            }else {
                finalHolder.brandLogotype.setVisibility(View.GONE);
                finalHolder.brandName.setGravity(Gravity.CENTER_VERTICAL);
            }
            return convertView;
        }

        public int getCount() {
            return mBrands.size();
        }

        public Object getItem(int position) {
            return mBrands.get(position);
        }

        public long getItemId(int position) {
            return mBrands.get(position).id;
        }
    }

    class ViewHolder {
        TextView brandName;
        ImageView brandLogotype;
    }
}

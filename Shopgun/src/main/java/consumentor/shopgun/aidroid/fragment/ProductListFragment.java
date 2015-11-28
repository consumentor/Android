package consumentor.shopgun.aidroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import consumentor.shopgun.aidroid.view.BaseActivity;
import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.view.BrandActivity;
import consumentor.shopgun.aidroid.view.ProductActivity;
import consumentor.shopgun.aidroid.model.Brand;
import consumentor.shopgun.aidroid.model.CertificationMark;
import consumentor.shopgun.aidroid.model.Product;
import consumentor.shopgun.aidroid.model.ProductCategory;
import consumentor.shopgun.aidroid.util.JsonDateDeserializer;
import consumentor.shopgun.aidroid.util.RESTLoader;
import consumentor.shopgun.aidroid.util.Util;
import consumentor.shopgun.aidroid.customview.ScrollingTextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Simon on 29.08.13.
 *
 */
public class ProductListFragment extends BaseListFragment implements
        LoaderManager.LoaderCallbacks<RESTLoader.RESTResponse>, AbsListView.OnScrollListener {

    public static final String EXTRA_PRODUCT_CATEGORY = "org.consumentor.shopgun.extra.EXTRA_PRODUCT_CATEGORY";
    public static final String EXTRA_ONLY_CERTIFIED_PRODUCTS = "org.consumentor.shopgun.extra.EXTRA_ONLY_CERTIFIED_PRODUCTS";

    private static final int LOADER_GET_PRODUCTS = 0x1;
    public static final String EXTRA_PRODUCTS = "org.consumentor.shopgun.extra.EXTRA_PRODUCTS";
    private List<Product> mProducts;
    private ProductAdapter mAdapter;

    private boolean mOnlyCertifiedProducts;
    private ProductCategory mProductCategory;
    private Brand mBrand;

    private int itemsPerPage = 25;
    private int pageIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(EXTRA_ONLY_CERTIFIED_PRODUCTS)){
            mOnlyCertifiedProducts = getArguments().getBoolean(EXTRA_ONLY_CERTIFIED_PRODUCTS);
        }
        if (getArguments().containsKey(EXTRA_PRODUCT_CATEGORY)){
            mProductCategory = (ProductCategory) getArguments().getSerializable(EXTRA_PRODUCT_CATEGORY);
        }
        if (getArguments().containsKey(BrandActivity.EXTRA_BRAND)){
            mBrand = (Brand) getArguments().getSerializable(BrandActivity.EXTRA_BRAND);
        }
        if(getArguments().containsKey(ProductListFragment.EXTRA_PRODUCTS)){
            mProducts = (List<Product>) getArguments().getSerializable(EXTRA_PRODUCTS);
        }
    }

    public void addProduct(Product product){
        mProducts.add(product);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_list, container, false);

        if (mProducts == null){
            mProducts = new ArrayList<Product>();
            loadProducts();
        }else {
            mAdapter = new ProductAdapter(getActivity(), mProducts);
            setListAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
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
                Product product = (Product) mAdapter.getItem(pos);

                Intent intent = new Intent(getActivity(), ProductActivity.class);
                if (product.id > 0){
                    intent.putExtra(ProductActivity.EXTRA_PRODUCT_ID, product.id);
                }else{
                    //Todo ProductActivity should also handle GTIN so we don't send the product but GTIN in this place.
                    intent.putExtra(ProductActivity.EXTRA_PRODUCT, product);
                }

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

            case LOADER_GET_PRODUCTS:{
                ((BaseActivity)getActivity()).updateLoaderIndicator();
                if (code == 200 && !json.equals("")) {
                    Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
                    List<Product> products = gson.fromJson(json, new TypeToken<List<Product>>(){}.getType());
                    mProducts.addAll(products);
                    if (mAdapter == null){
                        mAdapter = new ProductAdapter(getActivity(), mProducts);
                        setListAdapter(mAdapter);
                        getListView().setOnScrollListener(this);
                    }
                    else{
                        mAdapter.notifyDataSetChanged();
                    }
                }
                else {
                    Toast.makeText(getActivity(), "Failed to load products!", Toast.LENGTH_SHORT).show();
                }
                getLoaderManager().destroyLoader(LOADER_GET_PRODUCTS);
                break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<RESTLoader.RESTResponse> loader) {

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisible, int visibleCount, int totalCount) {

        boolean loadMore = /* maybe add a padding */
                firstVisible + visibleCount >= totalCount;

        if(loadMore && !getLoaderManager().hasRunningLoaders()) {
            loadProducts();
        }
    }

    private void loadProducts() {

        pageIndex++;
        String uriString = RESTLoader.BASE_URL
                +"/products?" +
                "resultsPerPage=" +itemsPerPage +
                "&pageIndex=" +pageIndex ;
        if (mOnlyCertifiedProducts){
            uriString += "&hasCertificationMarks=true";
        }
        if (mProductCategory != null){
            uriString += "&productCategoryId=" +mProductCategory.id;
        }
        if (mBrand != null){
            uriString += "&brandId=" +mBrand.id;
        }
        Uri loginUri = Uri.parse(uriString);

        Bundle args = new Bundle();
        Bundle params = new Bundle();
        args.putParcelable(RESTLoader.ARGS_URI, loginUri);
        args.putSerializable(RESTLoader.ARGS_VERB, RESTLoader.HTTPVerb.GET);
        args.putParcelable(RESTLoader.ARGS_PARAMS, params);
        // Initialize the Loader.
        getLoaderManager().initLoader(LOADER_GET_PRODUCTS, args, this);
        ((BaseActivity)getActivity()).showLoaderIndicator(true);
    }


    public class ProductAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private Context mContext;
        private List<Product> mProducts;

        public ProductAdapter(Context context, List<Product> products) {
            this.mContext  		  = context;
            this.mProducts = products;
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
                convertView 			 = mInflater.inflate(R.layout.product_list_item, null);
                holder.productName = (ScrollingTextView) convertView.findViewById(R.id.productName);
                holder.brandName = (TextView) convertView.findViewById(R.id.brandName);
                holder.certificationMarkContainer = (LinearLayout)convertView.findViewById(R.id.certificationMarkContainer);
                holder.productLogotype = (ImageView) convertView.findViewById(R.id.productLogotype);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Product product = mProducts.get(position);

            holder.productLogotype.setImageBitmap(null);
            holder.certificationMarkContainer.removeAllViews();

            if(!Util.isNullOrEmpty(product.name)){
                String productName = product.name;
                if (!Util.isNullOrEmpty(product.quantity) && !Util.isNullOrEmpty(product.quantityUnit)){
                    productName += " " +product.quantity +" " +product.quantityUnit;
                }
                holder.productName.setText(productName);
                holder.brandName.setText(product.brand.name);

                final ViewHolder finalHolder = holder;
                for (CertificationMark certificationMark : product.certificationMarks){
                    final ImageView imageView = new ImageView(getActivity());
                    imageView.setAdjustViewBounds(true);
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    ImageLoader.getInstance().loadImage(certificationMark.logotypeUrl, new ImageSize(50, 50), new SimpleImageLoadingListener(){
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            imageView.setImageBitmap(loadedImage);
                        }
                    });
                    holder.certificationMarkContainer.addView(imageView);
                }

                ImageLoader.getInstance().loadImage(mProducts.get(position).imageSmall,new ImageSize(100, 100), new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        finalHolder.productLogotype.setImageBitmap(loadedImage);
                    }
                });
            }
            else {
                holder.productName.setText(getString(R.string.product_info_missing));
                holder.brandName.setText(product.gtin);
            }


            return convertView;
        }

        public int getCount() {
            return mProducts.size();
        }

        public Object getItem(int position) {
            return mProducts.get(position);
        }

        public long getItemId(int position) {
            return mProducts.get(position).id;
        }
    }

    /**
     * A holder of the subviews so that android doesn't have to fetch
     * them everytime. Xml-parsing is relatively expensive.
     * @author Johan
     *
     */
    class ViewHolder {
        ScrollingTextView productName;
        TextView brandName;
        LinearLayout certificationMarkContainer;
        ImageView productLogotype;
    }
}
package consumentor.shopgun.aidroid.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import consumentor.shopgun.aidroid.customview.CircleDiagram;
import consumentor.shopgun.aidroid.util.AdviceRatingComparator;
import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.view.AdviceActivity;
import consumentor.shopgun.aidroid.view.BrandActivity;
import consumentor.shopgun.aidroid.view.CompanyActivity;
import consumentor.shopgun.aidroid.view.IngredientActivity;
import consumentor.shopgun.aidroid.view.ProductActivity;
import consumentor.shopgun.aidroid.model.AdviceFilterList;
import consumentor.shopgun.aidroid.model.AdviceInfo;
import consumentor.shopgun.aidroid.model.Adviceable;
import consumentor.shopgun.aidroid.model.Brand;
import consumentor.shopgun.aidroid.model.Company;
import consumentor.shopgun.aidroid.model.Ingredient;
import consumentor.shopgun.aidroid.model.ItemInfo;
import consumentor.shopgun.aidroid.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Simon on 29.08.13.
 */
public class AdviceListFragment extends BaseListFragment {

    public static final String EXTRA_ADVICEABLE = "org.consumentor.shopgun.extra.EXTRA_ADVICEABLE";

    private AdviceAdapter mAdapter;
    private Adviceable mAdviceable;

    private CheckBox mEthicsCheckBox;
    private CheckBox mHealthCheckBox;
    private CheckBox mEnvironmentCheckBox;
    private CheckBox mDidYouKnowCheckBox;
    private TextView mNoAdvicesTextView;
    private WeightedAdvicesDiagramFragment mWeightedAdvicesDiagramFragment;
    private CircleDiagram mCircleDiagram;
    private ImageView mItemImage;
    private TextView mItemName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments.containsKey(EXTRA_ADVICEABLE)) {
            mAdviceable = (Adviceable) arguments.get(EXTRA_ADVICEABLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advice_list, container, false);

        loadItemImage(view);

        mEthicsCheckBox = (CheckBox) view.findViewById(R.id.cbEthics);
        mHealthCheckBox = (CheckBox) view.findViewById(R.id.cbHealth);
        mEnvironmentCheckBox = (CheckBox) view.findViewById(R.id.cbEnvironment);
        mDidYouKnowCheckBox = (CheckBox) view.findViewById(R.id.cbDidYouKnow);

        mNoAdvicesTextView = (TextView) view.findViewById(R.id.noAdvices);
        if (mAdviceable.getAdvicesRecursively().size() > 0) {
            mNoAdvicesTextView.setVisibility(View.GONE);
        }

//        mWeightedAdvicesDiagramFragment = (WeightedAdvicesDiagramFragment) getFragmentManager().findFragmentById(R.id.adviceOverviewFragment);
//        mWeightedAdvicesDiagramFragment = (WeightedAdvicesDiagramFragment) WeightedAdvicesDiagramFragment.instantiate(getActivity(), WeightedAdvicesDiagramFragment.class.getName());
//        getChildFragmentManager()
//                .beginTransaction()
//                .replace(R.id.content_frame, mWeightedAdvicesDiagramFragment)
//                .commit();

        mCircleDiagram = (CircleDiagram)view.findViewById(R.id.circleDiagram);

        mAdapter = new AdviceAdapter(getActivity());
        mAdapter.addAdviceable(mAdviceable);
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                //mWeightedAdvicesDiagramFragment.setAdvices(mAdapter.getAdvices());
                mCircleDiagram.init(mAdapter.getAdvices());
            }
        });

        setListAdapter(mAdapter);

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                StringBuilder text = new StringBuilder(4); // initial capacity of 4 characters

                // append 1 if filter should be applied for the tag, 0 otherwise
                text.append(mEthicsCheckBox.isChecked() ? "1" : "0");
                text.append(mHealthCheckBox.isChecked() ? "1" : "0");
                text.append(mEnvironmentCheckBox.isChecked() ? "1" : "0");
                text.append(mDidYouKnowCheckBox.isChecked() ? "1" : "0");

                mAdapter.getFilter().filter(text.toString());
            }
        };
        mEthicsCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
        mHealthCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
        mEnvironmentCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);
        mDidYouKnowCheckBox.setOnCheckedChangeListener(onCheckedChangeListener);

        return view;
    }

    private void loadItemImage(View view) {
        mItemName = (TextView) view.findViewById(R.id.itemName);
        mItemName.setText(mAdviceable.getItemInfo().itemName);
        mItemImage = (ImageView) view.findViewById(R.id.itemLogotype);
        mItemImage.setVisibility(View.GONE);
        String imageUrl = null;
        if (mAdviceable instanceof Product){
            imageUrl = ((Product)mAdviceable).imageMedium;
        }
        if (mAdviceable instanceof Brand){
            imageUrl = ((Brand)mAdviceable).logotypeUrl;
        }
        if (mAdviceable instanceof Company){
            imageUrl = ((Company)mAdviceable).logotypeUrl;
        }
        if (imageUrl != null){
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.loadImage(imageUrl, new ImageSize(100, 100), null, new SimpleImageLoadingListener(){
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mItemImage.setImageBitmap(loadedImage);
                    mItemImage.setVisibility(View.VISIBLE);
                    mItemName.setVisibility(View.GONE);
                }
            });
        }else {
            mItemImage.setVisibility(View.GONE);
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int pos,
                                    long id) {
                Object selectedItem = mAdapter.getItem(pos);
                if (selectedItem instanceof ItemInfo) {
                    ItemInfo item = (ItemInfo) selectedItem;
                    Intent itemIntent = null;
                    switch (item.itemType) {
                        //Todo: Handle different item types...
                        case INGREDIENT:
                            itemIntent = new Intent(getActivity(), IngredientActivity.class);
                            itemIntent.putExtra(IngredientActivity.EXTRA_INGREDIENT_ID, item.id);
                            break;
                        case COMPANY:
                            itemIntent = new Intent(getActivity(), CompanyActivity.class);
                            itemIntent.putExtra(CompanyActivity.EXTRA_COMPANY_ID, item.id);
                            break;
                        case BRAND:
                            itemIntent = new Intent(getActivity(), BrandActivity.class);
                            itemIntent.putExtra(BrandActivity.EXTRA_BRAND_ID, item.id);
                            break;
                        case PRODUCT:
                            itemIntent = new Intent(getActivity(), ProductActivity.class);
                            itemIntent.putExtra(ProductActivity.EXTRA_PRODUCT_ID, item.id);
                            break;
                    }
                    if (itemIntent != null) {
                        startActivity(itemIntent);
                    }
                } else {
                    Intent adviceIntent = new Intent(getActivity().getBaseContext(), AdviceActivity.class);
                    int adviceId = (int) mAdapter.getItemId(pos);
                    adviceIntent.putExtra(AdviceActivity.EXTRA_ADVICE_ID, adviceId);
                    startActivity(adviceIntent);
                }
            }
        });
    }

    private class AdviceAdapter extends BaseAdapter implements Filterable {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;
        private final Drawable mProductIconDrawable;
        private final Drawable mBrandIconDrawable;
        private final Drawable mCompanyIconDrawable;

        private HashMap<Integer, AdviceInfo> mVisibleAdvices = new HashMap<Integer, AdviceInfo>();
        private AdviceFilterList mProductAdvices = new AdviceFilterList();
        private AdviceFilterList mBrandAdvices = new AdviceFilterList();
        private AdviceFilterList mCompanyAdvices = new AdviceFilterList();
        private HashMap<Integer, AdviceFilterList> mIngredientAdvices = new HashMap<Integer, AdviceFilterList>();
        private LayoutInflater mInflater;

        private int mListItemCount;
        private HashMap<Integer, ItemInfo> mSeparatorsByIndex = new HashMap<Integer, ItemInfo>();

        private Context mContext;
        private Drawable mIngredientIconDrawable;
        final float scale = getActivity().getResources().getDisplayMetrics().density;

        public AdviceAdapter(Context context) {
            this.mContext = context;
            this.mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mIngredientIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_ingredient);
            mProductIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_product);
            mBrandIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_brand);
            mCompanyIconDrawable = mContext.getResources().getDrawable(R.drawable.ic_company);
        }


        public void addAdviceable(Adviceable adviceable) {

            if (adviceable instanceof Product) {
                this.addAdvices((Product) adviceable);
            }
            if (adviceable instanceof Ingredient) {
                // Since only one ingredient, can take any constant as key for ingredients
                this.addAdvices((Ingredient) adviceable, 0);
            }
            if (adviceable instanceof Company) {
                this.addAdvices((Company) adviceable);
            }
            if (adviceable instanceof Brand) {
                this.addAdvices((Brand) adviceable);
            }

            // Filter and show all categories
            this.getFilter().filter("1111");
        }


        /**
         * Add a list of generic results with the name as advice list separator
         */
        private void addAdvices(final List<? extends AdviceInfo> advices) {

            if (advices.size() > 0) {
                addSeparatorItem(mListItemCount, advices.get(0).getItemInfo());
                mListItemCount++;

                Collections.sort(advices, new AdviceRatingComparator());
                for (AdviceInfo adviceInfo : advices) {

                    mVisibleAdvices.put(mListItemCount, adviceInfo);
                    mListItemCount++;
                }

            }
        }

        /**
         * Adds and categorizes all advices from a product.
         *
         * @param product Product
         */
        private void addAdvices(Product product) {

            if (product.advices.size() > 0) {
                mProductAdvices.add(product.advices);
            }
            if (product.brand != null) {
                addAdvices(product.brand);
            }
            for (int i = 0; i < product.ingredients.size(); i++) {
                addAdvices(product.ingredients.get(i), i);
            }
        }

        /**
         * Adds and categorizes all advices from an ingredient.
         *
         * @param ingredient Ingredient
         * @param key        The key used to save this particular ingredient
         */
        private void addAdvices(Ingredient ingredient, int key) {
            if (ingredient.advices.size() > 0) {
                // in order to show actual ingredient name we apply a little hack...
                for (AdviceInfo adviceInfo : ingredient.advices){
                    adviceInfo.itemName = ingredient.name;
                    adviceInfo.ingredientId = ingredient.id;
                }
                AdviceFilterList list = new AdviceFilterList();
                list.add(ingredient.advices);
                mIngredientAdvices.put(key, list);
            }
        }

        /**
         * Adds and categorizes all advices from a company.
         *
         * @param company Company
         */
        private void addAdvices(Company company) {
            if (company.advices.size() > 0) {
                mCompanyAdvices.add(company.advices);
            }
        }

        /**
         * Adds and categorizes all advices from a brand.
         *
         * @param brand Brand
         */
        private void addAdvices(Brand brand) {
            if (brand.advices.size() > 0) {
                mBrandAdvices.add(brand.advices);
                //addAdvices(brand.advices);
            }
            if (brand.company != null) {
                addAdvices(brand.company);
            }
        }

        public ArrayList<AdviceInfo> getAdvices(){
            ArrayList<AdviceInfo> adviceInfos = new ArrayList<AdviceInfo>();
            adviceInfos.addAll(mVisibleAdvices.values());
            return adviceInfos;
        }

        /**
         * Add a advice_list_separator item.
         */
        public void addSeparatorItem(final int index, final ItemInfo itemInfo) {
            // save advice_list_separator position
            mSeparatorsByIndex.put(index, itemInfo);

        }

        private void reset() {
            mVisibleAdvices.clear();
            mSeparatorsByIndex.clear();
            notifyDataSetChanged();
        }

        /**
         * Since we don't want to recycle the advice_list_separator we
         * return IGNORE_ITEM_VIEW_TYPE. That means that the
         * recycler will discard that view.
         */
        @Override
        public int getItemViewType(int position) {
            return mSeparatorsByIndex.keySet().contains(position) ? IGNORE_ITEM_VIEW_TYPE : TYPE_ITEM;
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
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
            SeparatorHolder separatorHolder;
            int type = getItemViewType(position);

            System.out.println("getView " + position + " " + convertView + " type = " + type);

            if (convertView == null || type == IGNORE_ITEM_VIEW_TYPE) {
                // Should be used if we switch the layout of the advice_list_item to relative.
                // convertView = mInflater.inflate(R.layout.advice_list_item, parent, false);
                holder = new ViewHolder();
                switch (type) {
                    case (TYPE_ITEM):
                        convertView = mInflater.inflate(R.layout.overview_advice_item, null);
                        holder.mentorName = (TextView) convertView.findViewById(R.id.advisorName);
                        holder.adviceDescription = (TextView) convertView.findViewById(R.id.adviceAbstract);
                        holder.semaphoreIcon = (ImageView) convertView.findViewById(R.id.semaphoreIndicator);
                        convertView.setTag(holder);
                        setAdviceListItemFields(position, convertView);
                        break;
                    case (IGNORE_ITEM_VIEW_TYPE):
                        View separator = mInflater.inflate(R.layout.advice_list_separator, null);
                        setSeparatorItemFields(position, separator);
                        return separator;

                }
            } else {
                switch (type) {
                    case TYPE_ITEM: {
                        setAdviceListItemFields(position, convertView);
                        break;
                    }
                    case IGNORE_ITEM_VIEW_TYPE: {
                        setSeparatorItemFields(position, convertView);
                    }
                }
            }
            return convertView;
        }

        private void setSeparatorItemFields(int position, View convertView) {
            TextView itemTypeText = (TextView) convertView.findViewById(R.id.separatorText);
            ImageView itemTypeImageView = (ImageView) convertView.findViewById(R.id.itemTypeImage);
            ItemInfo item = mSeparatorsByIndex.get(position);
            Drawable icon;

            switch (item.itemType) {
                case BRAND:
                    icon = mBrandIconDrawable;
                    break;
                case COMPANY:
                    icon = mCompanyIconDrawable;
                    break;
                case INGREDIENT:
                    icon = mIngredientIconDrawable;
                    break;
                case PRODUCT:
                    icon = mProductIconDrawable;
                    break;
                default:
                    icon = null;
            }
            itemTypeText.setText(item.itemName);
            itemTypeImageView.setImageDrawable(icon);
        }

        private void setAdviceListItemFields(int position, View convertView) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            AdviceInfo adviceInfo = mVisibleAdvices.get(position);
            holder.mentorName.setText(adviceInfo.mentorInfo.name);
            holder.adviceDescription.setText(adviceInfo.introduction);

            ImageView semaphoreIndicator = holder.semaphoreIcon;
            Drawable semaphore = null;
            int semaphoreColor = 0;

            switch (adviceInfo.semaphoreValue) {
                case -1:
                    semaphoreColor = getResources().getColor(R.color.shopgun_red);
                    semaphore = getRedSemaphoreForTag(adviceInfo.tag);
                    break;
                case 0:
                    semaphoreColor = getResources().getColor(R.color.shopgun_yellow_darker);
                    semaphore = getYellowSemaphoreForTag(adviceInfo.tag);
                    break;
                case 1:
                    semaphoreColor = getResources().getColor(R.color.shopgun_green);
                    semaphore = getGreenSemaphoreForTag(adviceInfo.tag);
                    break;
                case -2:
                    semaphoreColor = getResources().getColor(R.color.shopgun_gray);
                    semaphore = getGraySemaphoreForTag(adviceInfo.tag);
            }
            holder.mentorName.setTextColor(semaphoreColor);
            semaphoreIndicator.setImageDrawable(semaphore);
        }

        private Drawable getGreenSemaphoreForTag(String tag) {
            if (tag.equals(AdviceInfo.TXT_ENVIRONMENT)){
                return getResources().getDrawable(R.drawable.ic_environment_green);
            }
            if (tag.equals(AdviceInfo.TXT_HEALTH)){
                return getResources().getDrawable(R.drawable.ic_health_green);
            }
            if (tag.equals(AdviceInfo.TXT_ETHICS)){
                return getResources().getDrawable(R.drawable.ic_ethic_green);
            }
            return null;
        }

        private Drawable getYellowSemaphoreForTag(String tag) {
            if (tag.equals(AdviceInfo.TXT_ENVIRONMENT)){
                return getResources().getDrawable(R.drawable.ic_environmentl_yellow);
            }
            if (tag.equals(AdviceInfo.TXT_HEALTH)){
                return getResources().getDrawable(R.drawable.ic_health_yellow);
            }
            if (tag.equals(AdviceInfo.TXT_ETHICS)){
                return getResources().getDrawable(R.drawable.ic_ethic_yellow);
            }
            return null;
        }

        private Drawable getRedSemaphoreForTag(String tag) {
            if (tag.equals(AdviceInfo.TXT_ENVIRONMENT)){
                return getResources().getDrawable(R.drawable.ic_environmentl_red);
            }
            if (tag.equals(AdviceInfo.TXT_HEALTH)){
                return getResources().getDrawable(R.drawable.ic_health_red);
            }
            if (tag.equals(AdviceInfo.TXT_ETHICS)){
                return getResources().getDrawable(R.drawable.ic_ethic_red);
            }
            return null;
        }

        private Drawable getGraySemaphoreForTag(String tag) {
            if (tag.equals(AdviceInfo.TXT_ENVIRONMENT)){
                return getResources().getDrawable(R.drawable.ic_environment_gray);
            }
            if (tag.equals(AdviceInfo.TXT_HEALTH)){
                return getResources().getDrawable(R.drawable.ic_health_gray);
            }
            if (tag.equals(AdviceInfo.TXT_ETHICS)){
                return getResources().getDrawable(R.drawable.ic_ethic_gray);
            }
            if (tag.equals(AdviceInfo.TXT_DIDYOUKNOW)){
                return getResources().getDrawable(R.drawable.ic_info_gray);
            }
            return null;
        }

        public int getCount() {
            return mVisibleAdvices.size() + mSeparatorsByIndex.size();
        }

        public Object getItem(int position) {
            if (mSeparatorsByIndex.keySet().contains(position)) {
                return mSeparatorsByIndex.get(position);
            }
            return mVisibleAdvices.get(position);
        }

        public long getItemId(int position) {
            if (mSeparatorsByIndex.keySet().contains(position)) {
                return mSeparatorsByIndex.get(position).id;
            }
            return mVisibleAdvices.get(position).id;
        }

        @Override
        public Filter getFilter() {

            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    // constraint should be a 4-char string consisting of ones and zeros.
                    // A one in the first position means that ethics should be shown, a one in
                    // the second means that health should be shown, third position is for
                    // environment and fourth position is for 'did you know'.

                    FilterResults fResults = new FilterResults();
                    fResults.values = false; // will contain true if everything is working
                    if (constraint == null || constraint.length() != 4) {
                        //Something is wrong
                        Log.d("debug", "filtering went wrong: " + constraint);
                    }

                    Log.d("debug", "performing filtering: " + constraint);

                    mProductAdvices.applyFilter(constraint);
                    mBrandAdvices.applyFilter(constraint);
                    mCompanyAdvices.applyFilter(constraint);
                    for (AdviceFilterList ingredientList : mIngredientAdvices.values()) {
                        ingredientList.applyFilter(constraint);
                    }

                    fResults.values = true;
                    return fResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.values instanceof Boolean && ((Boolean) results.values == true)) {
                        // Good, filtering should have worked
                    } else {
                        //This shouldn't happen
                        Log.d("debug", "could not publish filtered results");
                    }

                    // clear mVisibleAdvices and add results
                    mVisibleAdvices.clear();
                    mSeparatorsByIndex.clear();
                    mListItemCount = 0;
                    // The list is now cleared without problems, just need to add things again.

                    /*
                    Log.d("debug", "#productAdvices: " + mProductAdvices.getFilteredAdvices().size());
                    Log.d("debug", "#brandAdvices: " + mBrandAdvices.getFilteredAdvices().size());
                    Log.d("debug", "#companyAdvices: " + mCompanyAdvices.getFilteredAdvices().size());
                    Log.d("debug", "#ingredientAdvices: " + mIngredientAdvices.size());
                    */
                    addAdvices(mProductAdvices.getFilteredAdvices());
                    addAdvices(mBrandAdvices.getFilteredAdvices());
                    addAdvices(mCompanyAdvices.getFilteredAdvices());
                    for (AdviceFilterList ingredientList : mIngredientAdvices.values()) {
                        addAdvices(ingredientList.getFilteredAdvices());
                    }

                    notifyDataSetChanged();
                }
            };
            return filter;
        }
    }

    /**
     * A holder of the subviews so that android doesn't have to fetch
     * them everytime. Xml-parsing is relatively expensive.
     *
     * @author Johan
     */
    static class ViewHolder {
        TextView mentorName;
        TextView adviceDescription;
        ImageView semaphoreIcon;
    }

    static class SeparatorHolder {
        TextView separatorTitle;
    }

    public class AdviceComparator implements Comparator<AdviceInfo> {

        public int compare(AdviceInfo adv1, AdviceInfo adv2) {
            if (adv1.semaphoreValue > adv2.semaphoreValue) {
                return -1;
            }
            if (adv1.semaphoreValue < adv2.semaphoreValue) {
                return 1;
            }
            return 0;
        }
    }
}

package consumentor.shopgun.aidroid.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.view.CompanyActivity;
import consumentor.shopgun.aidroid.model.CSRInfo;
import consumentor.shopgun.aidroid.model.Company;
import consumentor.shopgun.aidroid.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Simon on 11.09.13.
 */
public class CompanyCSRFragment extends BaseFragment {

    private Context mContext;

    private static final String TAG = CompanyInfoFragment.class.getName();
    private Company mCompany;
    private ExpandableListView listView;

    private static final String CSR_TAG = "CSR_TAG";
    private static final String CSR_TEXT = "CSR_TEXT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        mCompany = (Company) getArguments().getSerializable(CompanyActivity.EXTRA_COMPANY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_company_csr, container, false);

        View csrListContainer = view.findViewById(R.id.csrListContainer);

        final ImageView companyLogotype = (ImageView) view.findViewById(R.id.companyLogotype);
        View csrNoMemberInfo = view.findViewById(R.id.csrNoMemberInfo);
        if (mCompany.isMember){
            csrNoMemberInfo.setVisibility(View.GONE);
            if (!Util.isNullOrEmpty(mCompany.logotypeUrl)){
                ImageLoader.getInstance().loadImage(mCompany.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        companyLogotype.setImageBitmap(loadedImage);
                    }
                });
            }

            listView = (ExpandableListView) view.findViewById(R.id.listView);


            List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
            List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();

            for(CSRInfo csrInfo : mCompany.csrInfos){
                Map<String, String> curGroupMap = new HashMap<String, String>();
                groupData.add(curGroupMap);
                curGroupMap.put(CSR_TAG, csrInfo.tag);
                List<Map<String, String>> children = new ArrayList<Map<String, String>>();

                Map<String, String> curChildMap = new HashMap<String, String>();
                children.add(curChildMap);
                curChildMap.put(CSR_TEXT, Util.trim(Html.fromHtml(csrInfo.description)).toString());
                childData.add(children);
            }

            CSRExpandableListViewAdapter listViewAdapter = new CSRExpandableListViewAdapter();

            listView.setAdapter(listViewAdapter);
        }
        else {
            csrNoMemberInfo.setVisibility(View.VISIBLE);
            companyLogotype.setVisibility(View.GONE);
            csrListContainer.setVisibility(View.GONE);
        }

        return view;
    }

    private class CSRExpandableListViewAdapter extends BaseExpandableListAdapter{

        @Override
        public int getGroupCount() {
            return mCompany.csrInfos.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mCompany.csrInfos.get(groupPosition).tag;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mCompany.csrInfos.get(groupPosition).description;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String group = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.csr_info_group_row, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.row_name);
            tv.setText(group);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            String description = (String) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.csr_info_child_row, null);
            }
            //TextView tv = (TextView) convertView.findViewById(R.id.description);
            //tv.setText(Util.trim(Html.fromHtml(description)));
            WebView tv = (WebView) convertView.findViewById(R.id.csrText);
            tv.loadData(description, "text/html; charset=UTF-8", null);
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
package consumentor.shopgun.aidroid.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class CompanyFactsFragment extends BaseFragment {

    private static final String TAG = CompanyInfoFragment.class.getName();
    private Company mCompany;
    private TextView mCompanyAddressTextView;
    private TextView mCompanyPhoneTextView;
    private TextView mCompanyEmailTextView;
    private View mView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCompany = (Company) getArguments().getSerializable(CompanyActivity.EXTRA_COMPANY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_company_facts, container, false);

        initCompanyLogotype();
        initAllabolagButton();

        mCompanyAddressTextView = (TextView) mView.findViewById(R.id.companyAddress);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Util.isNullOrEmpty(mCompany.postalCode) ? "" : mCompany.postalCode +" ");
        stringBuilder.append(Util.isNullOrEmpty(mCompany.city) ? "" : mCompany.city +"\n");
        stringBuilder.append(Util.isNullOrEmpty(mCompany.address) ? "" : mCompany.address +"\n");
        mCompanyAddressTextView.setText(stringBuilder.toString());

        mCompanyPhoneTextView = (TextView) mView.findViewById(R.id.companyPhone);
        if (!Util.isNullOrEmpty(mCompany.phone)){
            mCompanyPhoneTextView.setText(mCompany.phone);
        }
        else{
            mCompanyPhoneTextView.setVisibility(View.GONE);
        }

        mCompanyEmailTextView = (TextView) mView.findViewById(R.id.companyEmail);
        if (!Util.isNullOrEmpty(mCompany.email)){
            mCompanyEmailTextView.setText(mCompany.email);
        }
        else{
            mCompanyEmailTextView.setVisibility(View.GONE);
        }

        return mView;
    }

    private void initAllabolagButton() {
        View allabolagButton = mView.findViewById(R.id.allabolagButton);
        allabolagButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String url = "http://mobil.allabolag.se/search.php?what="
                        + mCompany.name.replace(' ', '+');
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getActivity().startActivity(intent);
            }
        });
    }

    private void initCompanyLogotype() {
        final TextView companyName = (TextView) mView.findViewById(R.id.companyName);
        companyName.setText(mCompany.name);
        final ImageView companyLogotype = (ImageView) mView.findViewById(R.id.companyLogotype);
        if (mCompany.isMember){
            ImageLoader.getInstance().loadImage(mCompany.logotypeUrl, new ImageSize(100, 100), new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    companyLogotype.setImageBitmap(loadedImage);
                    companyName.setVisibility(View.GONE);
                }
            });
        }else {
            companyLogotype.setVisibility(View.GONE);
        }
    }
}

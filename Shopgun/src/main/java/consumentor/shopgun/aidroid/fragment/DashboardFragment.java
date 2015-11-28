package consumentor.shopgun.aidroid.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;

import consumentor.shopgun.aidroid.util.RESTLoader;
import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.view.BatchScanActivity;

/**
 * Created by Simon on 27.08.13.
 */
public class DashboardFragment extends Fragment {

    private Button mFreeTextSearchButton;
    private ImageButton mScanButton;
    private OnScanButtonClickedListener mListener;

    public interface OnScanButtonClickedListener{
        public void onScanButtonClick();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        assert activity instanceof OnScanButtonClickedListener;
        mListener = (OnScanButtonClickedListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mFreeTextSearchButton = (Button) view.findViewById(R.id.gtinInputButton);
        mFreeTextSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity a = getActivity();
                a.onSearchRequested();
            }
        });

        mScanButton = (ImageButton) view.findViewById(R.id.barcodeScanButton);
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BatchScanActivity.class);
                startActivity(intent);
                //mListener.onScanButtonClick();
            }
        });

        return view;
    }
}

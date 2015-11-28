package consumentor.shopgun.aidroid.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.view.IngredientActivity;
import consumentor.shopgun.aidroid.model.Ingredient;

/**
 * Created by Simon on 10.09.13.
 */
public class IngredientOverviewFragment extends OverviewFragment {

    private static final String TAG = IngredientOverviewFragment.class.getName();
    private Ingredient mIngredient;

    public interface IngredientOverviewFragmentListener extends OverviewFragment.OverviewFragmentListener {
        public void onShowWikipedia(View view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIngredient = (Ingredient) getArguments().getSerializable(IngredientActivity.EXTRA_INGREDIENT);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mContentView =  inflater.inflate(R.layout.fragment_ingredient_overview, container, false);
        
        View overviewHeader = mContentView.findViewById(R.id.overviewHeader);
        initOverviewHeader(overviewHeader);

        initAdviceOverviewFragment(mIngredient.getAdvicesRecursively());
        initTopWeightedAdvicesFragment(mIngredient.getAdvicesRecursively());
        return mContentView;
    }

    private void initOverviewHeader(View view) {

        final TextView ingredientName = (TextView) view.findViewById(R.id.itemName);
        ingredientName.setText(mIngredient.name);

        final ImageView itemLogotype = (ImageView) view.findViewById(R.id.itemLogotype);
        itemLogotype.setVisibility(View.GONE);
    }
}

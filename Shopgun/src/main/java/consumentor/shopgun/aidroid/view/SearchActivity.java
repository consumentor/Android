package consumentor.shopgun.aidroid.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import consumentor.shopgun.aidroid.model.ItemInfo;

import java.util.StringTokenizer;

//import com.google.analytics.tracking.android.EasyTracker;

public class SearchActivity extends FragmentActivity {

    public SearchActivity(){}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    Intent intent = getIntent();

	    //if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      
	      // If query is null, the intent was send from a suggestion
	      if(query == null) {
	    	  query = intent.getDataString();
              String idString = query.substring(query.indexOf("/")+1);
              int id = Integer.parseInt(idString);
              String action = intent.getAction();
              //Intent activityIntent = new Intent(action); Todo: wouldn't work for some reason
              Intent activityIntent = null;
              ItemInfo.ItemType itemType = ItemInfo.ItemType.valueOf(intent.getExtras().getString(SearchManager.EXTRA_DATA_KEY));
              switch (itemType){
                  case INGREDIENT:
                      activityIntent = new Intent(this, IngredientActivity.class);
                      activityIntent.putExtra(IngredientActivity.EXTRA_INGREDIENT_ID, id);
                      break;
                  case COMPANY:
                      activityIntent = new Intent(this, CompanyActivity.class);
                      activityIntent.putExtra(CompanyActivity.EXTRA_COMPANY_ID, id);
                      break;
                  case BRAND:
                      activityIntent = new Intent(this, BrandActivity.class);
                      activityIntent.putExtra(BrandActivity.EXTRA_BRAND_ID, id);
                      break;
                  case PRODUCT:
                      activityIntent = new Intent(this, ProductActivity.class);
                      activityIntent.putExtra(ProductActivity.EXTRA_PRODUCT_ID, id);
                      break;
              }
	    	  startActivity(activityIntent);
	      }else{
              //Todo: free text search
//		      //query = removeSpaces(query);
//		      Intent semaphoreIntent = new Intent(SearchActivity.this, ProductActivity.class);
//		      Bundle bundle = new Bundle();
//		      bundle.putString("Gtin", query.trim());
//		      bundle.putBoolean("productSearch", false);
//		      semaphoreIntent.putExtras(bundle);
//		      startActivity(semaphoreIntent);
	      }
	//    }
	    this.finish();

	}
	
	public static Intent createIntent(Context context) {
		
        Intent i = new Intent(context, SearchActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
	
	public String removeSpaces(String s) {
		  StringTokenizer st = new StringTokenizer(s," ",false);
		  String t = "";
		  while (st.hasMoreElements()) t += st.nextElement();
		  return t;
	}
}

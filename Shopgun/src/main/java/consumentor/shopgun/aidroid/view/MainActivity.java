package consumentor.shopgun.aidroid.view;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import consumentor.shopgun.aidroid.fragment.WebViewFragment;
import consumentor.shopgun.aidroid.util.RESTLoader;
import consumentor.shopgun.aidroid.view.R;
import consumentor.shopgun.aidroid.fragment.CertificationMarkListFragment;
import consumentor.shopgun.aidroid.fragment.CompanyListFragment;
import consumentor.shopgun.aidroid.fragment.DashboardFragment;
import consumentor.shopgun.aidroid.fragment.MentorListFragment;

public class MainActivity extends BaseActivity implements DashboardFragment.OnScanButtonClickedListener {

    private MainActivity activity;
    private CharSequence mTitle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private SharedPreferences mAppPreferences;

    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;
        setTitle(getString(R.string.app_name));

        mAppPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);

        setupNavigationDrawer();

        DashboardFragment dashboardFragment = (DashboardFragment) DashboardFragment.instantiate(this, DashboardFragment.class.getName());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, dashboardFragment)
                .commit();

//        boolean skipLogin = getIntent().getBooleanExtra(LoginActivity.PREFS_SKIP_LOGIN, false)|| mAppPreferences.getBoolean(LoginActivity.PREFS_SKIP_LOGIN, false);
//        if(!skipLogin ){
//            Intent loginIntent = new Intent(this, LoginActivity.class);
//            if (mAppPreferences.contains(LoginActivity.LOGIN_EMAIL)
//                    && mAppPreferences.contains(LoginActivity.LOGIN_PASSWORD)){
//                loginIntent.putExtra(LoginActivity.EXTRA_EMAIL, mAppPreferences.getString(LoginActivity.LOGIN_EMAIL, ""));
//                loginIntent.putExtra(LoginActivity.EXTRA_PASSWORD, mAppPreferences.getString(LoginActivity.LOGIN_PASSWORD, ""));
//            }
//            startActivity(loginIntent);
//        }

//          Intent intent = new Intent(this, AdviceActivity.class);
//          intent.putExtra(AdviceActivity.EXTRA_ADVICE_ID, 3642);
//          startActivity(intent);

//        Intent productViewIntent = new Intent(this, ProductActivity.class);
//        productViewIntent.putExtra(ScanActivity.EXTRA_BARCODE, "7391961122102");
//        startActivity(productViewIntent);

//        Intent productViewIntent = new Intent(this, ProductActivity.class);
//        productViewIntent.putExtra(ScanActivity.EXTRA_BARCODE, "5024333148005");
//        startActivity(productViewIntent);

//        Intent intent = new Intent(this, IngredientActivity.class);
  //      intent.putExtra(IngredientActivity.EXTRA_INGREDIENT_ID, 1510);
    //    startActivity(intent);
//
//        Intent intent = new Intent(this, CompanyActivity.class);
//        intent.putExtra(CompanyActivity.EXTRA_COMPANY_ID, 2577);
//        startActivity(intent);

//        Intent intent = new Intent(this, BrandActivity.class);
//        intent.putExtra(BrandActivity.EXTRA_BRAND_ID, 2580);
//        startActivity(intent);

//        MentorListFragment advisorListFragment = (MentorListFragment) Fragment.instantiate(this, MentorListFragment.class.getName());
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.content_frame, advisorListFragment)
//                .commit();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupNavigationDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        final String[] listItems = {
                getResources().getString(R.string.nav_drawer_about),
//                getResources().getString(R.string.nav_drawer_vision),
                getResources().getString(R.string.nav_drawer_faq),
//                getResources().getString(R.string.nav_drawer_partners),
                getResources().getString(R.string.nav_drawer_mentors),
//                getResources().getString(R.string.nav_drawer_companies),
//                getResources().getString(R.string.nav_drawer_sponsors),
                getResources().getString(R.string.nav_drawer_certificationmark),
                getResources().getString(R.string.nav_drawer_feedback)};
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, R.id.txtDrawerListItem, listItems));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Fragment fragment = null;
                // Uses position based on the String array above
                Bundle args = new Bundle();
                switch (position)
                {
                    case 0: // About
                        fragment = Fragment.instantiate(activity, WebViewFragment.class.getName());
                        args.putString(WebViewFragment.EXTRA_URL, "http://www.shopgun.se/om_shopgun.html");
                        fragment.setArguments(args);
                        break;
                    case 1: // FAQ
                        fragment = Fragment.instantiate(activity, WebViewFragment.class.getName());
                        args.putString(WebViewFragment.EXTRA_URL, "http://www.shopgun.se/faq.html");
                        fragment.setArguments(args);
                        break;
                    case 2: // Mentors
                        fragment = Fragment.instantiate(activity, MentorListFragment.class.getName());
                        break;
                    case 3: // CertificationMarks
                        fragment = Fragment.instantiate(activity, CertificationMarkListFragment.class.getName());
                        break;
                    case 4: // Feedback
                        final Intent feedbackIntent = new Intent(android.content.Intent.ACTION_SEND);

                        feedbackIntent.setType("plain/text");
                        feedbackIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"feedback@shopgun.se"});
                        feedbackIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Jag vill tycka till om appen...");
                        feedbackIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                        startActivity(Intent.createChooser(feedbackIntent, "Skicka mejl..."));
                        break;
                }

                if (fragment != null){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null)
                            .commit();
                }

                mDrawerList.setItemChecked(position, false);
                //setTitle(listItems[position]);
                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(getString(R.string.app_name));
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            getActionBar().setHomeButtonEnabled(true);
        }


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if(mDrawerToggle != null){
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onScanButtonClick() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, SCAN_REQUEST);
    }

    @Override
    public void setTitle(CharSequence title)
    {
        mTitle = title;
        getActionBar().setTitle(title);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_debug:
                if (checked)
                    RESTLoader.setServerUrlToDebug(true);
                break;
            case R.id.radio_production:
                if (checked)
                    RESTLoader.setServerUrlToDebug(false);
                break;
        }
    }
}

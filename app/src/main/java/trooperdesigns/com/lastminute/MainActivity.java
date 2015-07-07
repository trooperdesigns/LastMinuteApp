package trooperdesigns.com.lastminute;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Locale;

public class MainActivity extends FragmentActivity implements FragmentChangeListener, FragmentListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    static ViewPager mViewPager;

    private TextView message;
    private PagerTitleStrip titleStrip;
    private SlidingUpPanelLayout settingsPanel;
    private int numEvents;
    private ParseUser currentUser;
    private TextView userNameView, userGenderView, userEmailView;
    private Dialog progressDialog;
    private Button logout;
    private GridView gridView;
    private ImageView slideIcon;
    private LinearLayout sliderHelper;
    private Button viewAllContactsButton;
    private EditText search;

    private EventsFragment eventsFragment;
    private NewEventFragment newEventFragment;
    private RootFragment rootFragment;

    private Integer[] mThumbIds = {
            R.drawable.settings_icon,
            R.drawable.settings_icon,
            R.drawable.settings_icon
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.initialize(this, "MPkD1KSw9UFLdb3KpG46SgGKnrvdjEeG6OvGX9xv",
                "TjirfnSYcOmD1kW90vvJtSyfqZuFlboXzWnRVSca");
        ParseInstallation.getCurrentInstallation().saveEventually();

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // determine if current user is anonymous
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            // if user is anonymous, send user to login page
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);

            finish();
        } else {
            // if user is not anonymous
            // get current user data from parse.com
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                // let logged in users stay
                this.currentUser = currentUser;
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("userId", currentUser.getObjectId());
                installation.saveInBackground();
            } else {
                // send user to login page if currentUser doesn't exist
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);

                finish();
            }

        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(0);

        logout = (Button) findViewById(R.id.logout_button);
        message = (TextView) findViewById(R.id.message);
        sliderHelper = (LinearLayout) findViewById(R.id.slider_helper);

        settingsPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        settingsPanel.setAnchorPoint(0.75f);

        slideIcon = (ImageView) findViewById(R.id.settings_icon);

        search = (EditText) findViewById(R.id.search);

        settingsPanel.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View view, float slideOffset) {
                sliderHelper.setOnTouchListener(new View.OnTouchListener(){

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
            }

            @Override
            public void onPanelCollapsed(View view) {
                slideIcon.setRotation(0f);

            }

            @Override
            public void onPanelExpanded(View view) {
                slideIcon.setRotation(180f);

                sliderHelper.setOnTouchListener(new View.OnTouchListener(){

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        settingsPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        return false;
                    }
                });

            }

            @Override
            public void onPanelAnchored(View view) {
                slideIcon.setRotation(180f);

                sliderHelper.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        settingsPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        return false;
                    }
                });
            }

            @Override
            public void onPanelHidden(View view) {

            }
        });

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new GridAdapter(this));
        gridView.setNumColumns(3);

        // disable gridView scrolling so it doesn't get in the way
        gridView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }

        });

        // gridView item click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                switch (position) {
                    case 0:
                        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(settingsIntent);
                        break;
                    case 1:
                        Intent viewContactsIntent = new Intent(MainActivity.this, ViewContactsActivity.class);
                        startActivity(viewContactsIntent);
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                ParseUser.logOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                Toast.makeText(MainActivity.this, "Logout successful!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showOtherFragment() {
        Fragment fr = new NewEventFragment();
        FragmentChangeListener fc = (FragmentChangeListener) this;
        fc.replaceFragment(fr);
    }

    @Override
    public void onBackPressed() {
        // if sliding panel is open, close it
        if (settingsPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                settingsPanel.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED) {
            settingsPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            // if settings panel collapsed but not on main page, switch to page 0
            if(mViewPager.getCurrentItem() == 1){
                // TODO: if viewing all contacts to invite in event creation page (fragment), can't go back to creation page
                //mViewPager.setCurrentItem(0);
                // if sliding panel is open, close it
                /*
                InputMethodManager imm = (InputMethodManager) this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

                if(imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0) == true){
                    super.onBackPressed();
                } else if(imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0) == false && !search.getText().toString().matches("")){
                    search.setText(null);
                } else {
                    super.onBackPressed();
                }
                */
                super.onBackPressed();
            } else {
                // otherwise do as it normally would (finish())
                super.onBackPressed();
            }

        }



        return;
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(mViewPager.getId(), fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onSwitchToNextFragment() {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final FragmentManager mFragmentManager;
        private int currentPos;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // return the selected page

            this.currentPos = position;

            switch (position) {
                case 0:
                    // Events Fragment
                    Log.d("fragment", "event Fragment");
                    eventsFragment = new EventsFragment();
                    return eventsFragment;
                case 1:
                    // Create new event fragment
                    Log.d("fragment", "right fragment");
                    // root fragment used so we can replace rootFragment with NewEventFragment and ContactsFragment
                    rootFragment = new RootFragment();
                    return rootFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_events).toUpperCase(l);
                case 1:
                    return getString(R.string.title_create).toUpperCase(l);
            }
            return null;
        }

        public int getCurrentPos(){
            return currentPos;
        }
    }

    public class GridAdapter extends BaseAdapter {

        private Context mContext;

        public GridAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mThumbIds.length;
        }

        @Override
        public Object getItem(int arg0) {
            return mThumbIds[arg0];
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View grid;

            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                grid = inflater.inflate(R.layout.settings_grid, parent, false);
            } else {
                grid = (View) convertView;
            }

            ImageView imageView = (ImageView) grid.findViewById(R.id.image);
            imageView.setImageResource(mThumbIds[position]);

            TextView tv = (TextView) grid.findViewById(R.id.grid_item_title);

            switch(position){
                case(0):
                    tv.setText("Profile");
                    break;
                case(1):
                    tv.setText("Contacts");
                    break;
                case(2):
                    tv.setText("Settings");
                    break;
            }

            return grid;
        }
    }

}
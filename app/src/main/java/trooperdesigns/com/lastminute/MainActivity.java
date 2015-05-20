package trooperdesigns.com.lastminute;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MainActivity extends FragmentActivity {

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
    ViewPager mViewPager;

    private TextView message;
    private PagerTitleStrip titleStrip;
    private SlidingUpPanelLayout settingsPanel;
    private int numEvents;
    private ParseUser currentUser;
    private TextView userNameView, userGenderView, userEmailView;
    private Dialog progressDialog;
    private Button logout;
    private EditText searchFriend;
    private Button searchFriendButton;

    private static final List<String> PERMISSIONS = new ArrayList<String>() {
        {
            add("user_friends");
            add("public_profile");
        }
    };
    private static final int PICK_FRIENDS_ACTIVITY = 1;
    private TextView resultsTextView;

    private ParseUser foundUser;
    private String alertText = "";

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
        mViewPager.setCurrentItem(1);

        logout = (Button) findViewById(R.id.logout_button);
        message = (TextView) findViewById(R.id.message);
        settingsPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            // Fragment fragment = new EventFragment();
            // Bundle args = new Bundle();
            // args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position +
            // 1);
            // fragment.setArguments(args);

            switch (position) {
                case 0:
                    // Event fragment
                    Log.d("fragment", "2nd fragment");
                    return new NewEventFragment();
                case 1:
                    // Events Fragment
                    Log.d("fragment", "event Fragment");
                    return new EventsFragment();

                case 2:
                    // Create new event fragment
                    Log.d("fragment", "left fragment");
                    return new NewEventFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

}
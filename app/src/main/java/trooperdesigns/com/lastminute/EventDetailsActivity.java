package trooperdesigns.com.lastminute;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DateFormatSymbols;


public class EventDetailsActivity extends FragmentActivity implements FragmentChangeListener {

    private TextView textView;
    private ParseImageView imageView;
    private FrameLayout container;
    private SlidingUpPanelLayout settingsPanel;
    private LinearLayout sliderHelper;
    private ImageView slideIcon;
    private Toolbar toolbar;
    public static ParseObject event;

    private GridView gridView;
    private Integer[] mThumbIds = {
            R.drawable.settings_icon,
            R.drawable.settings_icon,
            R.drawable.settings_icon,
            R.drawable.settings_icon
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_event_details);

        // container for current fragment
        container = (FrameLayout) findViewById(R.id.event_details_fragment);
        slideIcon = (ImageView) findViewById(R.id.settings_icon);
        sliderHelper = (LinearLayout) findViewById(R.id.slider_helper);
        settingsPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);



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
            }

            @Override
            public void onPanelHidden(View view) {

            }
        });

        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(new GridAdapter(this));
        gridView.setNumColumns(4);

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
                        Intent i = new Intent(EventDetailsActivity.this, SettingsActivity.class);
                        startActivity(i);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
            }
        });

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.event_details_fragment) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            EventDetailsFragment eventDetailsFrag = new EventDetailsFragment();
            eventDetailsFrag.setArguments(getIntent().getExtras());

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            eventDetailsFrag.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.event_details_fragment, eventDetailsFrag).commit();
        }

    }

    public void setEvent(ParseObject event) {
        this.event = event;
    }

    public ParseObject getEvent() {
        return event;
    }

    String getMonthName(int num) {
        String month = "wrong";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(container.getId(), fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        // if sliding panel is open, close it
        if (settingsPanel.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ||
                settingsPanel.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED) {
            settingsPanel.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            // otherwise do as it normally would (finish())
            super.onBackPressed();
        }

        return;
    }

    // TODO: adds action bar at bottom, need to integrate into custom action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        return false;
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
                grid = new View(mContext);
                LayoutInflater inflater = getLayoutInflater();
                grid = inflater.inflate(R.layout.settings_grid, parent, false);
            } else {
                grid = (View) convertView;
            }

            ImageView imageView = (ImageView) grid.findViewById(R.id.image);
            imageView.setImageResource(mThumbIds[position]);

            return grid;
        }
    }
}

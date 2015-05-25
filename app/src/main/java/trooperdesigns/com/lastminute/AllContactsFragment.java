package trooperdesigns.com.lastminute;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;

public class AllContactsFragment extends ListFragment {
    /**
     * The fragment argument representing the section number for this fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static int numEvents;

    private AllContactsListAdapter allContactsAdapter;
    private ListView listView;
    private SwipeRefreshLayout swipeLayout;

    public AllContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_all_contacts, container,
                false);

        listView = (ListView) rootView.findViewById(android.R.id.list);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                load();
                allContactsAdapter.loadObjects();
                swipeLayout.setRefreshing(false);
            }
        });

        // setting custom adapter for listFragment
        allContactsAdapter = new AllContactsListAdapter(getActivity());
        listView.setAdapter(allContactsAdapter);

        // initial load of events list contents
        load();
        allContactsAdapter.loadObjects();
        setNumEvents();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return rootView;
    }

    private void load(){
        // TODO Auto-generated method stub
        /*
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        animation.setDuration(800);
        listView.startAnimation(animation);

        animation = null;
        */

        setNumEvents();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        listView = getListView(); // since you extend ListFragment

        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> list, View view,
                                           int position, long id) {
                // TODO Auto-generated method stub
                ParseObject object = (ParseObject) list.getItemAtPosition(position);
                Toast.makeText(getActivity(), "Long click on: " + position + ": " + object.getString("title"),
                        Toast.LENGTH_SHORT).show();
                return true;
            }

        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        ParseObject object = (ParseObject) l.getItemAtPosition(position);
        EventDetailsActivity containerActivity = (EventDetailsActivity) getActivity();

        final android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();

        String tag = EventDetailsFragment.class.getName();
        //Fragment fragment = getFragmentManager().findFragmentByTag(tag);
/*		if (fragment == null) {
		   fragment = new EventDetailsFragment();
		   ft.replace(R.id.pager, new EventDetailsFragment(), "NewFragmentTag"); 
		}
		*/
        Intent i = new Intent(getActivity(), EventDetailsActivity.class);


        // put all event information necessary for full event page in bundle
        Bundle bundle = new Bundle();
        bundle.putString("objectId", object.getObjectId()); // objectId of event
        bundle.putString("title", object.getString("title")); // title of event
        bundle.putString("createdAt", object.getCreatedAt().toString()); // createdAt

        ParseFile pf = object.getParseFile("image");
        if(pf != null){
            bundle.putString("image", object.getParseFile("image").getUrl()); // put parse image file in bundle
        }

        i.putExtras(bundle);

        startActivity(i);
        update();


        // do something
        //Toast.makeText(getActivity(), object.getString("title"), Toast.LENGTH_SHORT).show();
    }



    public void setNumEvents(){
        numEvents = listView.getCount();
        EventDetailsActivity containerActivity = (EventDetailsActivity) getActivity();
        //containerActivity.mSectionsPagerAdapter.setNumevents();
    }

    public void update(){
        EventDetailsActivity containerActivity = (EventDetailsActivity) getActivity();
        //containerActivity.update();
    }

}
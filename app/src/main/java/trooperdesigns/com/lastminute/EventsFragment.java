package trooperdesigns.com.lastminute;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseObject;

public class EventsFragment extends ListFragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	public static int numEvents;

	private CustomAdapter eventsAdapter;
	private ListView listView;
	private Button refresh;
	private EditText eventSearch;

	public EventsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_events, container,
				false);

		listView = (ListView) rootView.findViewById(android.R.id.list);
		refresh = (Button) rootView.findViewById(R.id.refresh);
		eventSearch = (EditText) rootView.findViewById(R.id.event_search);
		
		// refresh button to refresh the contents of events TODO: i.e. pull down to refresh
		refresh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				load();
				eventsAdapter.loadObjects();
			}
		});
		
		// setting custom adapter for listFragment
		eventsAdapter = new CustomAdapter(getActivity());
		listView.setAdapter(eventsAdapter);
		
		// initial load of events list contents
		load();
		eventsAdapter.loadObjects();
		setNumEvents();
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		eventSearch.addTextChangedListener(new TextWatcher(){
		    @Override
		    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
		        // When user changed the Text
		        eventsAdapter.getFilter().filter(cs);
		    }

		    @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }

		    @Override
		    public void afterTextChanged(Editable arg0) {}
		});
		return rootView;
	}
	
	private void load(){
		// TODO Auto-generated method stub
		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
		animation.setDuration(800);
		listView.startAnimation(animation);
		setNumEvents();
		animation = null;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		listView = getListView(); // since you extend ListFragment
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener(){

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
		MainActivity containerActivity = (MainActivity) getActivity();
		
		final android.support.v4.app.FragmentTransaction ft = getFragmentManager().beginTransaction();

		String tag = EventDetailsFragment.class.getName();
		//Fragment fragment = getFragmentManager().findFragmentByTag(tag);
/*		if (fragment == null) {
		   fragment = new EventDetailsFragment();
		   ft.replace(R.id.pager, new EventDetailsFragment(), "NewFragmentTag"); 
		}
		*/
		Intent i = new Intent(getActivity(), EventDetailsActivity.class);
		startActivity(i);
		update();
		

		// do something
		Toast.makeText(getActivity(), object.getString("title"),
				Toast.LENGTH_SHORT).show();
	}
	
	
	
	public void setNumEvents(){
		numEvents = listView.getCount();
		MainActivity containerActivity = (MainActivity) getActivity();
		//containerActivity.mSectionsPagerAdapter.setNumevents();
	}
	
	public void update(){
		MainActivity containerActivity = (MainActivity) getActivity();
		//containerActivity.update();
	}
	
}
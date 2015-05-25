package trooperdesigns.com.lastminute;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventDetailsFragment extends Fragment implements View.OnClickListener {
	private Button logoutButton;
	private TextView textView;
	private ParseImageView imageView;
	private FrameLayout container;

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		final View rootView = inflater.inflate(R.layout.fragment_event_details, container, false);

		imageView = (ParseImageView) rootView.findViewById(R.id.icon);


		// get extras passed from intent to activity into current fragment
		Bundle bundle = getArguments();
		String objectId = bundle.getString("objectId");
		String title = bundle.getString("title");
		String createdAt = bundle.getString("createdAt");
		String imgUrl = bundle.getString("image");

		Toast toast = Toast.makeText(super.getActivity(), objectId + "," + title + "," + createdAt + "," + imgUrl,
				Toast.LENGTH_SHORT);
		toast.show();

		// View Contacts button to open new All Contacts Fragment
		Button viewContactsButton = (Button) rootView.findViewById(R.id.testButton);
		viewContactsButton.setOnClickListener(this);



		ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
		query.whereEqualTo("objectId", objectId);
		// First try to find from the cache and only then go to network
		query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE); // or CACHE_ONLY
		// Execute the query to find the object with ID
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> list, ParseException e) {
				if (e == null) {
					ParseObject object = list.get(0);
					ParseFile imageFile = object.getParseFile("image");
					if (imageFile != null) {
						imageView.setParseFile(imageFile);
						imageView.loadInBackground();
					}

					// Add the title view
					TextView titleTextView = (TextView) rootView.findViewById(R.id.eventTitle);
					titleTextView.setFocusable(false);
					titleTextView.setText(object.getString("title").toUpperCase());

					// TextView for Location (using details for dummy)
					TextView locationTextView = (TextView) rootView.findViewById(R.id.eventLocation);
					locationTextView.setFocusable(false);
					locationTextView.setText(object.getString("details").toUpperCase());

					// get Date object and use for formatting
					Date startDate = object.getDate("startTime");
					Date endDate = object.getDate("endTime");

					SimpleDateFormat dateFormat;

					dateFormat = new SimpleDateFormat("MM");
					String month = getMonthName(Integer.parseInt(dateFormat.format(startDate))).toUpperCase();

					// textView for Date
					dateFormat = new SimpleDateFormat(" dd yyyy");
					TextView dateTextView = (TextView) rootView.findViewById(R.id.eventDate);
					dateTextView.setFocusable(false);
					dateTextView.setText(month + dateFormat.format(startDate));

					// textView for start time
					dateFormat = new SimpleDateFormat("hh:mma");
					TextView timeTextView = (TextView) rootView.findViewById(R.id.eventTime);
					timeTextView.setFocusable(false);
					timeTextView.setText(dateFormat.format(startDate) + " - " + dateFormat.format(endDate));
				}
				else {
					textView.setText(e.getLocalizedMessage());
				}
			}
		});
		
		return rootView;
	}

	// used to get month name from numberered month MM
	String getMonthName(int num) {
		String month = "wrong";
		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] months = dfs.getMonths();
		if (num >= 0 && num <= 11 ) {
			month = months[num];
		}
		return month;
	}

	// replace current fragment (event details) with new fragment (All contacts fragment)
	public void openAllContactsFragment()
	{
		// create new AllContacts Fragment
		AllContactsFragment fragment = new AllContactsFragment();

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(this.getId(), fragment, fragment.toString());
		fragmentTransaction.addToBackStack(fragment.toString());
		fragmentTransaction.commit();

	}

	@Override
	public void onClick(View v) {
		openAllContactsFragment();
	}
}

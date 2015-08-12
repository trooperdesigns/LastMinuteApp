package trooperdesigns.com.lastminute;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EventDetailsFragment extends Fragment implements View.OnClickListener {
	private Button viewContactsButton, responseAccept, responseDecline;
	private TextView textView;
	private ParseImageView imageView;
	private FrameLayout container;
	private ParseObject event;
	private AllContactsFragment allContactsFragment;
	private static AllContactsAdapter allContactsAdapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		final View rootView = inflater.inflate(R.layout.fragment_event_details, container, false);

		this.event = EventListAdapter.event;

		imageView = (ParseImageView) rootView.findViewById(R.id.icon);
		responseAccept = (Button) rootView.findViewById(R.id.response_accept);
		responseDecline = (Button) rootView.findViewById(R.id.response_decline);

		responseAccept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				replyInvitation("accept");
				EventsFragment.clearCache = true;
				//EventsFragment.eventsAdapter.loadObjects();
			}
		});

		responseDecline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				replyInvitation("decline");
				EventsFragment.clearCache = true;
				//EventsFragment.eventsAdapter.loadObjects();
			}
		});

		// get extras passed from intent to activity into current fragment
		Bundle bundle = getArguments();
		String objectId = bundle.getString("objectId");
		String title = bundle.getString("title");
		String details = bundle.getString("details");
		String createdAt = bundle.getString("createdAt");
		String imgUrl = bundle.getString("image");

		Toast toast = Toast.makeText(super.getActivity(), objectId + "," + title + "," + createdAt + "," + imgUrl,
				Toast.LENGTH_SHORT);
		toast.show();

		// View Contacts button to open new All Contacts Fragment
		viewContactsButton = (Button) rootView.findViewById(R.id.testButton);
		viewContactsButton.setOnClickListener(this);
		viewContactsButton.setVisibility(View.GONE);

		EventDetailsActivity.event = event;

		ParseQuery eventQuery = new ParseQuery("Event");
		eventQuery.whereEqualTo("objectId", objectId);
		eventQuery.getFirstInBackground(new GetCallback() {
			@Override
			public void done(ParseObject parseObject, ParseException e) {
				ParseRelation<ParseObject> relation = event.getRelation("invitees");
				ParseQuery<ParseObject> relationQuery = relation.getQuery();
				relationQuery.include("user");

				relationQuery.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);

				relationQuery.findInBackground(new FindCallback<ParseObject>() {
					@Override
					public void done(List<ParseObject> invitations, ParseException e) {
						if (e == null) {
							Log.d("invitation", String.valueOf(invitations.size()));
							for (ParseObject object : invitations) {
								Log.d("invitation", object.getParseUser("user").getObjectId());
							}
							allContactsFragment = new AllContactsFragment();
							allContactsAdapter = new AllContactsAdapter(getActivity(), invitations);
							//allContactsFragment.setListAdapter(allContactsAdapter);
							viewContactsButton.setVisibility(View.VISIBLE);

							ParseFile imageFile = event.getParseFile("image");
							if (imageFile != null) {
								imageView.setParseFile(imageFile);
								imageView.loadInBackground();
							}

							// Add the title view
							TextView titleTextView = (TextView) rootView.findViewById(R.id.eventTitle);
							titleTextView.setFocusable(false);
							titleTextView.setText(event.getString("title").toUpperCase());

							// TextView for Location (using details for dummy)
							TextView locationTextView = (TextView) rootView.findViewById(R.id.eventLocation);
							locationTextView.setFocusable(false);
							locationTextView.setText(event.getString("details").toUpperCase());

							// get Date object and use for formatting
							Date startDate = event.getDate("startTime");
							Date endDate = event.getDate("endTime");

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

						} else {
							Log.d("invitation", e.getLocalizedMessage());
						}
					}
				});
			}

			@Override
			public void done(Object o, Throwable throwable) {

			}
		});
		return rootView;
	}

	private void replyInvitation(String invitationResponse) {
		HashMap<String, String> response = new HashMap<>();
		response.put("response", invitationResponse);
		response.put("eventId", EventListAdapter.event.getObjectId());

		ParseCloud.callFunctionInBackground("replyInvitation", response, new FunctionCallback<String>() {
			@Override
			public void done(String response, ParseException e) {
				if(e == null) {
					Toast.makeText(getActivity(), "Success: " + response, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
				}

			}
		});
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
	public void openAllParticipantsFragment()
	{

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(this.getId(), allContactsFragment, allContactsFragment.toString());
		fragmentTransaction.addToBackStack(allContactsFragment.toString());
		fragmentTransaction.commit();

	}

	@Override
	public void onClick(View v) {
		openAllParticipantsFragment();
	}

	public static AllContactsAdapter getAllContactsAdapter() {
		return allContactsAdapter;
	}
}

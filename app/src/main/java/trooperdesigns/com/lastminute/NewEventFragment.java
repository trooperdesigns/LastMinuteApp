package trooperdesigns.com.lastminute;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewEventFragment extends Fragment implements View.OnClickListener {

	private Button viewAllContactsButton;
	private Button createButton;

	// variables used for parse
	ParseUser currentUser;
	ParseRelation<ParseObject> invitees;
	ParseObject invitation;
	ParseObject event;
	ParseUser foundUser;
	ArrayList<String> list;


	public NewEventFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_new_event, container, false);

		// don't need this, just keep for now (
		// determine if current user is anonymous
		if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
			// if user is anonymous, send user to login page
			Intent i = new Intent(getActivity(), LoginActivity.class);
			startActivity(i);

			getActivity().finish();
		} else {
			// if user is not anonymous
			// get current user data from parse.com
			ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser != null) {
				// let logged in users stay
				this.currentUser = currentUser;
			} else {
				// send user to login page if currentUser doesn't exist
				Intent i = new Intent(getActivity(), LoginActivity.class);
				startActivity(i);

				getActivity().finish();
			}

		}

		viewAllContactsButton = (Button) rootView.findViewById(R.id.testButton);
		viewAllContactsButton.setOnClickListener(this);

		createButton = (Button) rootView.findViewById(R.id.create_button);
		createButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {


				event = new ParseObject("Event");
				event.put("title", "Android Test");
				event.put("details", "2232 Langham");
				event.put("status", 0);
				Calendar c = Calendar.getInstance();
				Date date = c.getTime();
				event.put("startTime", date);
				event.put("endTime", date);
				event.put("minAttendees", 1);
				event.put("creator", currentUser);

				list = new ArrayList<>();
				list.add("justin");
				list.add("justin2");

				event.saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if (e == null) {
							// success

							HashMap<String, Object> newEvent = new HashMap<>();
							newEvent.put("event", event.getObjectId());
							newEvent.put("invitees", list);

							ParseCloud.callFunctionInBackground("sendInvitations", newEvent, new FunctionCallback<String>() {
								public void done(String response, ParseException e) {
									if (e == null) {
										// success
										Toast.makeText(getActivity(), "Success: " + response, Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(getActivity(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
									}
								}
							});

						} else {
							Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
						}
					}
				});

			}
		});

		return rootView;
	}

	public static NewEventFragment newInstance(FragmentListener fragmentListener) {
		NewEventFragment f = new NewEventFragment();
		return f;
	}

	// replace current fragment (event details) with new fragment (All contacts fragment)
	public void openAllContactsFragment()
	{
		// create new AllContacts Fragment
		AllContactsFragment fragment = new AllContactsFragment();

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.root_frame, fragment, fragment.toString());

		//fragmentTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
		fragmentTransaction.addToBackStack(fragment.toString());
		fragmentTransaction.commit();

	}

	@Override
	public void onClick(View v) {
		//openAllContactsFragment();
		Intent intent = new Intent(getActivity(), ViewContactsActivity.class);
		startActivity(intent);
	}


}
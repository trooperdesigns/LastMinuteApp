package trooperdesigns.com.lastminute;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseACL;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NewEventFragment extends Fragment implements View.OnClickListener {

	static final int PICK_CONTACTS_REQUEST = 2;

	private Button viewAllContactsButton;
	private Button createButton;
	private Button doneButton;
	private ListView inviteesList;

	// variables used for parse
	ParseUser currentUser;
	ParseRelation<ParseObject> invitees;
	ParseObject invitation;
	ParseObject event;
	ParseUser foundUser;
	ArrayList<String> list;
	// TODO: empty allContacts list when event is created
	static List<Contact> allContacts = new ArrayList<>();
	List<Contact> selectedContacts = new ArrayList<>();
	ArrayList<Parcelable> returnParcel = new ArrayList<>();
	StringBuilder sb = new StringBuilder();

	InviteesAdapter invAdapter;

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

			// TODO: Move event creation to parse code & pass in event parameters only
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

			HashMap<String, Object> newEvent = new HashMap<>();
			//newEvent.put("event", event.getObjectId());
			newEvent.put("invitees", list);

			ParseCloud.callFunctionInBackground("createEvent", newEvent, new FunctionCallback<String>() {
				public void done(String response, ParseException e) {
					if (e == null) {
						// success
						Toast.makeText(getActivity(), "Success: " + response, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getActivity(), "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
					}
				}
			});

			}
		});

		doneButton = (Button) rootView.findViewById(R.id.done_button);
		doneButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getActivity(), MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent pIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

				// Build notification
				Notification noti = new Notification.Builder(getActivity())
						.setContentTitle("Last Minute App")
						.setContentText("You have been invited to ______").setSmallIcon(R.drawable.twitter_icon_small)
						.setContentIntent(pIntent)
						.addAction(R.drawable.twitter_icon_small, "Call", pIntent)
						.addAction(R.drawable.twitter_icon_small, "More", pIntent)
						.addAction(R.drawable.twitter_icon_small, "And more", pIntent)
						//Vibration
						.setVibrate(new long[]{0, 300, 300, 300})
						//LED
						.setLights(1001, 1000, 1000).build();

				NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
				// hide the notification after its selected
				noti.flags |= Notification.FLAG_AUTO_CANCEL;

				notificationManager.notify(0, noti);

				MainActivity.mViewPager.setCurrentItem(0); // switch to events (home) page
				resetNewEventFragment(); // recreate NewEventFragment
				// set all contacts to unchecked
				for(int i = 0; i < allContacts.size(); i++){
					allContacts.get(i).setIsChecked(false);
				}
			}
		});

		inviteesList = (ListView) rootView.findViewById(R.id.invitees_list);
		invAdapter = new InviteesAdapter(getActivity().getApplicationContext(), selectedContacts);
		inviteesList.setAdapter(invAdapter);


		return rootView;
	}

	// replace current fragment (event details) with new fragment (All contacts fragment)
	public void openSelectContactsFragment()
	{
		// create new AllContacts Fragment
		SelectContactsFragment fragment = new SelectContactsFragment();

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.root_frame, fragment, fragment.toString());

		//fragmentTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
		fragmentTransaction.addToBackStack(fragment.toString());
		fragmentTransaction.commit();

	}

	public void resetNewEventFragment(){
		// create new NewEventFragment
		NewEventFragment fragment = new NewEventFragment();

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.root_frame, fragment, fragment.toString());

		//fragmentTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
		//fragmentTransaction.addToBackStack(fragment.toString());
		fragmentTransaction.commit();
	}

	@Override
	public void onClick(View v) {
		//openSelectContactsFragment();
		Intent intent = new Intent(getActivity(), ViewContactsActivity.class);
		startActivityForResult(intent, PICK_CONTACTS_REQUEST);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Check which request we're responding to
		if (requestCode == PICK_CONTACTS_REQUEST) {
			// Make sure the request was successful
			if (resultCode == FragmentActivity.RESULT_OK) {
				// The user selected contacts

				selectedContacts = new ArrayList<>();

				// print out all contacts in allContacts
				sb = new StringBuilder();
				for(int i = 0 ; i < allContacts.size(); i++){
					if(allContacts.get(i).getIsChecked() == true){
						sb.append(allContacts.get(i).getName() + ",");
						selectedContacts.add(allContacts.get(i));
					}
				}
				Log.d("recreate", "after: " + sb.toString());
				Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_SHORT).show();

				// TODO: refresh list of invited contacts
				//invAdapter.notifyDataSetChanged();

			}
		}
	}

	public class InviteesAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		InviteesAdapter (Context context, List<Contact> contacts){

			mInflater = LayoutInflater.from(context);
			mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public int getCount() {
			return selectedContacts.size();
		}

		@Override
		public Object getItem(int position) {
			return selectedContacts.get(position).getName();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View vi = convertView;
			if (convertView == null) vi = mInflater.inflate(R.layout.invitees_row, null);
			TextView nameText = (TextView) vi.findViewById(R.id.contact_name);
			nameText.setText(getItem(position).toString());

			return vi;
		}
	}
}
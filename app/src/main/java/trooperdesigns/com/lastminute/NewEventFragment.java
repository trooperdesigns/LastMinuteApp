package trooperdesigns.com.lastminute;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NewEventFragment extends Fragment implements View.OnClickListener {

	private Button viewAllContactsButton;
	

	public NewEventFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_new_event, container, false);

		viewAllContactsButton = (Button) rootView.findViewById(R.id.testButton);
		viewAllContactsButton.setOnClickListener(this);

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
		fragmentTransaction.addToBackStack(fragment.toString());
		fragmentTransaction.commit();

	}

	@Override
	public void onClick(View v) {
		openAllContactsFragment();
	}


}
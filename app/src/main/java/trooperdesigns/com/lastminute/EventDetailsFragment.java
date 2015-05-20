package trooperdesigns.com.lastminute;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class EventDetailsFragment extends Fragment {
	private Button logoutButton;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_event_details, container, false);
		

		
		return rootView;
	}
}

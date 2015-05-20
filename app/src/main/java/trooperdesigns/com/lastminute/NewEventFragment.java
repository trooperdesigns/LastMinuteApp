package trooperdesigns.com.lastminute;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewEventFragment extends Fragment {
	

	public NewEventFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_new_event, container, false);
		

		
		return rootView;
	}
}
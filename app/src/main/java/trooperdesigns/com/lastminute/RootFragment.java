package trooperdesigns.com.lastminute;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// root of second page of viewPager
// acts as container so NewEventFragment can be replaced by AllContactsFragment
public class RootFragment extends Fragment {

    private static final String TAG = "RootFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		/* Inflate the layout for this fragment */
        View view = inflater.inflate(R.layout.root_fragment, container, false);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
		/*
		 * When this container fragment is created, we fill it with our first
		 * "real" fragment
		 */

        NewEventFragment fragment = new NewEventFragment();
        transaction.replace(R.id.root_frame, fragment, fragment.toString());

        transaction.commit();

        return view;
    }

}
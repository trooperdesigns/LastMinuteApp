package trooperdesigns.com.lastminute;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;

import java.util.List;

public class AllContactsFragment extends ListFragment {

    private ListView listView;
    private SwipeRefreshLayout swipeLayout;

    private AllContactsAdapter allContactsAdapter;

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
                allContactsAdapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
            }
        });

        listView.setAdapter(EventDetailsFragment.getAllContactsAdapter());

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return rootView;
    }

    public void setListAdapter(AllContactsAdapter adapter) {
        listView.setAdapter(allContactsAdapter);
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
        ParseObject invitation = (ParseObject) l.getItemAtPosition(position);

    }

    public static AllContactsFragment newInstance() {
        AllContactsFragment f = new AllContactsFragment();
        return f;
    }

}
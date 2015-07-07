package trooperdesigns.com.lastminute;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventListAdapter extends ParseQueryAdapter implements Filterable {

	@SuppressWarnings("unchecked")
	public EventListAdapter(Context context) {
		
		// Use the QueryFactory to construct a PQA that will only show
		// Todos marked as high-pri
		super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
			public ParseQuery<ParseObject> create() {
				ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Event");
				// First try to find from the cache and only then go to network
				//query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE); // or CACHE_ONLY
				//query.whereEqualTo("highPri", true);
				return query;
			}
		});
	}

	// Customize the layout by overriding getItemView
	@Override
	public View getItemView(ParseObject object, View v, ViewGroup parent) {
		if (v == null) {
			v = View.inflate(getContext(), R.layout.row, null);
		}

		super.getItemView(object, v, parent);

		// Add and download the image
		ParseImageView eventImage = (ParseImageView) v.findViewById(R.id.icon);
		eventImage.setFocusable(false);
		ParseFile imageFile = object.getParseFile("image");
		if (imageFile != null) {
			eventImage.setParseFile(imageFile);
			eventImage.loadInBackground();
		}

		// Add the title view
		TextView titleTextView = (TextView) v.findViewById(R.id.eventTitle);
		titleTextView.setFocusable(false);
		titleTextView.setText(object.getString("title").toUpperCase());

		// TextView for Location (using details for dummy)
		TextView locationTextView = (TextView) v.findViewById(R.id.eventLocation);
		locationTextView.setFocusable(false);
		locationTextView.setText(object.getString("details").toUpperCase());

		// TODO: Check that dates exist, otherwise parsing error

		// get Date object and use for formatting
		Date startDate = object.getDate("startTime");
		Date endDate = object.getDate("endTime");

		SimpleDateFormat dateFormat;

		dateFormat = new SimpleDateFormat("MM");
		String month = getMonthName(Integer.parseInt(dateFormat.format(startDate))).toUpperCase();

		// textView for Date
		dateFormat = new SimpleDateFormat(" dd yyyy");
		TextView dateTextView = (TextView) v.findViewById(R.id.eventDate);
		dateTextView.setFocusable(false);
		dateTextView.setText(month + dateFormat.format(startDate));

		// textView for start time
		dateFormat = new SimpleDateFormat("hh:mma");
		TextView timeTextView = (TextView) v.findViewById(R.id.eventTime);
		timeTextView.setFocusable(false);
		timeTextView.setText(dateFormat.format(startDate) + " - " + dateFormat.format(endDate));

		return v;
	}

	String getMonthName(int num) {
		String month = "wrong";
		DateFormatSymbols dfs = new DateFormatSymbols();
		String[] months = dfs.getMonths();
		if (num >= 0 && num <= 11 ) {
			month = months[num];
		}
		return month;
	}

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
	    Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                //arrayListNames = (List<String>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<String> FilteredArrayNames = new ArrayList<String>();

                // perform your search here using the searchConstraint String.

                constraint = constraint.toString().toLowerCase();
/*                for (int i = 0; i < mDatabaseOfNames.size(); i++) {
                    String dataNames = mDatabaseOfNames.get(i);
                    if (dataNames.toLowerCase().startsWith(constraint.toString()))  {
                        FilteredArrayNames.add(dataNames);
                    }
                }*/

                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;
                Log.e("VALUES", results.values.toString());

                return results;
            }
        };

        return filter;
	}

}

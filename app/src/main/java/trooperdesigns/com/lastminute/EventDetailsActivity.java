package trooperdesigns.com.lastminute;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class EventDetailsActivity extends Activity {

    private TextView textView;
    private ParseImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_event_details);

        imageView = (ParseImageView) findViewById(R.id.icon);
        //imageView.setFocusable(false);


        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        String objectId = bundle.getString("objectId");
        String title = bundle.getString("title");
        String createdAt = bundle.getString("createdAt");
        String imgUrl = bundle.getString("image");

        Toast toast = Toast.makeText(this, objectId + "," + title + "," + createdAt + "," + imgUrl,
                Toast.LENGTH_SHORT);
        toast.show();
        //textView.setText(imgUrl);


        //Picasso.with(this).load(imgUrl).into(imageView);


        //textView.setText(objectId);

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
                    TextView titleTextView = (TextView) findViewById(R.id.eventTitle);
                    titleTextView.setFocusable(false);
                    titleTextView.setText(object.getString("title").toUpperCase());

                    // TextView for Location (using details for dummy)
                    TextView locationTextView = (TextView) findViewById(R.id.eventLocation);
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
                    TextView dateTextView = (TextView) findViewById(R.id.eventDate);
                    dateTextView.setFocusable(false);
                    dateTextView.setText(month + dateFormat.format(startDate));

                    // textView for start time
                    dateFormat = new SimpleDateFormat("hh:mma");
                    TextView timeTextView = (TextView) findViewById(R.id.eventTime);
                    timeTextView.setFocusable(false);
                    timeTextView.setText(dateFormat.format(startDate) + " - " + dateFormat.format(endDate));
                }
                else {
                    textView.setText(e.getLocalizedMessage());
                }
            }
        });

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

}

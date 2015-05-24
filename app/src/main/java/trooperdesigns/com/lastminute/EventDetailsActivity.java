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

import java.util.List;


public class EventDetailsActivity extends Activity {

    private TextView textView;
    private ParseImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_event_details);



        textView = (TextView) findViewById(R.id.text1);
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

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Todo");
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
                    TextView titleTextView = (TextView) findViewById(R.id.text1);
                    titleTextView.setFocusable(false);
                    titleTextView.setText(object.getString("title"));

                    // Add a reminder of how long this item has been outstanding
                    TextView timestampView = (TextView) findViewById(R.id.timestamp);
                    timestampView.setFocusable(false);
                    timestampView.setText(object.getCreatedAt().toString());
                }
                else {
                    textView.setText(e.getLocalizedMessage());
                }
            }
        });

    }

}

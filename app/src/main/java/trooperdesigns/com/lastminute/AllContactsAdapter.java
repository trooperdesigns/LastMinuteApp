package trooperdesigns.com.lastminute;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class AllContactsAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<ParseObject> invitations;
    private Context context;

    public AllContactsAdapter(Context context, List<ParseObject> invitations) {
        // alphabetically sort invitees list
        Collections.sort(invitations, comparator);
        this.invitations = invitations;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return invitations.size();
    }

    @Override
    public ParseObject getItem(int position) {
        return invitations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if (v == null) v = mInflater.inflate(R.layout.contacts_row, null);

        TextView tv = (TextView) v.findViewById(R.id.eventTitle);
        tv.setText("Name: " + invitations.get(position).getParseUser("user").getUsername());

        return v;
    }

    // use to sort invitees list alphabetically
    Comparator<ParseObject> comparator = new Comparator<ParseObject>() {
        public int compare(ParseObject obj1, ParseObject obj2) {
            return obj1.getParseUser("user").getUsername().compareToIgnoreCase(obj2.getParseUser("user").getUsername());
        }
    };
}



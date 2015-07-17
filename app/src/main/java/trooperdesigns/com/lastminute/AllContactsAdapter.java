package trooperdesigns.com.lastminute;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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
        if (v == null) v = mInflater.inflate(R.layout.invitees_row, null);

        ParseObject invitation = invitations.get(position);

        TextView tv = (TextView) v.findViewById(R.id.contact_name);
        tv.setText("Name: " + invitation.getParseUser("user").getUsername());

        View status = (View) v.findViewById(R.id.contact_status);
        GradientDrawable statusColour = (GradientDrawable) status.getBackground();
        switch(invitation.getNumber("status").intValue()) {
            case 0:
                statusColour.setColor(Color.GRAY);
                break;
            case 1:
                statusColour.setColor(Color.GREEN);
                break;
            case 2:
                statusColour.setColor(Color.RED);
                break;
            default:
                statusColour.setColor(Color.GRAY);
                break;
        }

        return v;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    // use to sort invitees list alphabetically
    Comparator<ParseObject> comparator = new Comparator<ParseObject>() {
        public int compare(ParseObject obj1, ParseObject obj2) {
            return obj1.getParseUser("user").getUsername().compareToIgnoreCase(obj2.getParseUser("user").getUsername());
        }
    };
}



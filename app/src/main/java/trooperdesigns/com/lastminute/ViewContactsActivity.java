package trooperdesigns.com.lastminute;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import trooperdesigns.com.lastminute.util.StringMatcher;
import trooperdesigns.com.lastminute.widget.IndexableListView;

public class ViewContactsActivity extends Activity implements AdapterView.OnItemClickListener {

    ContactsAdapter contactsAdapter;

    private List<Contact> originalContacts = new ArrayList<>();
    private List<Contact> filteredContacts = new ArrayList<>();
    private ArrayList<Contact> contactsResult;
    // used to remove contacts who were unchecked
    private ArrayList<String> uncheckedContacts;

    private Button select;
    private EditText search;
    IndexableListView lv;
    public static boolean isKeyboardOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: setting to full screen makes checkboxes not work correctly for some reason..
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_contacts);

        isKeyboardOpen = false;
        originalContacts = NewEventFragment.allContacts;

        search = (EditText) findViewById(R.id.search);

        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isKeyboardOpen = true;
                return false;
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contactsAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        getAllContacts(this.getContentResolver());
        lv  = (IndexableListView) findViewById(R.id.lv);
        contactsAdapter = new ContactsAdapter(getApplicationContext(), NewEventFragment.allContacts);
        lv.setClickable(true);
        lv.setAdapter(contactsAdapter);
        lv.setOnItemClickListener(this);
        lv.setItemsCanFocus(false);
        lv.setTextFilterEnabled(true);

        // adding
        select = (Button) findViewById(R.id.button1);
        select.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Boolean noneSelected = true;
                // check all contacts if any are selected
                for(int i = 0; i < NewEventFragment.allContacts.size(); i++){
                    if(NewEventFragment.allContacts.get(i).getIsChecked() == true){
                        noneSelected = false;
                    }
                }
                // if at least one selected, return
                if(!noneSelected){
                    Intent data = new Intent();
                    if (getParent() == null) {
                        setResult(Activity.RESULT_OK, data);
                    } else {
                        getParent().setResult(Activity.RESULT_OK, data);
                    }
                    finish();
                } else {
                    // else notify user to select contacts
                    Toast.makeText(ViewContactsActivity.this, "Select at least one contact", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        // if sliding panel is open, close it
        InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0) == true) {
            super.onBackPressed();
        } else if (imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0) == false && !search.getText().toString().matches("")) {
            search.setText(null);
        } else {
            NewEventFragment.allContacts = originalContacts;
            super.onBackPressed();
        }
        isKeyboardOpen = false;
        return;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        filteredContacts.get(position).toggle();
    }

    public void getAllContacts(ContentResolver cr) {

        String prevName = "";

        if(NewEventFragment.allContacts.isEmpty()){
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            // initialize Contact object for each contact in phonebook
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if(name.equalsIgnoreCase(prevName)){
                    NewEventFragment.allContacts.add(new Contact(name + " (" +
                            getType(phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))) + ")",
                            phoneNumber, false));
                } else {
                    NewEventFragment.allContacts.add(new Contact(name,
                            phoneNumber, false));
                }
                prevName = name;

            }

            phones.close();

        } else {
            // contacts list already created
        }

    }

    class ContactsAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, SectionIndexer, Filterable {
        CheckBox cb;
        private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Canvas canvas;

        private LayoutInflater mInflater;
        private ItemFilter mFilter = new ItemFilter();

        View vi;

        ContactsAdapter(Context context, List<Contact> contacts) {

            // set originalContacts and filteredContacts as original Contacts list
            NewEventFragment.allContacts = contacts;
            filteredContacts = contacts;

            mInflater = LayoutInflater.from(context);
            mInflater = (LayoutInflater) ViewContactsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return filteredContacts.size();
        }

        @Override
        public Object getItem(int position) {
            // return the name of the contact at that position (for listview)
            return filteredContacts.get(position).getName();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            vi = convertView;
            if (convertView == null) vi = mInflater.inflate(R.layout.all_contacts_row, null);

            TextView tv = (TextView) vi.findViewById(R.id.contact_name);
            TextView tv1 = (TextView) vi.findViewById(R.id.contact_number);
            cb = (CheckBox) vi.findViewById(R.id.contact_is_checked);
            tv.setText("Name: " + filteredContacts.get(position).getName());

            // TODO: Uncomment next line when using phone number as username to query
            tv1.setText("Phone No: " + filteredContacts.get(position).getPhone());
//            tv1.setText("Phone No:" + phone.get(position));
            cb.setTag(position);
            cb.setChecked(filteredContacts.get(position).getIsChecked());
            cb.setOnCheckedChangeListener(this);

            // A ViewHolder keeps references to children views to avoid unnecessary calls
            // to findViewById() on each row.
            ViewHolder holder;

            // When convertView is not null, we can reuse it directly, there is no need
            // to reinflate it. We only inflate a new View when the convertView supplied
            // by ListView is null.
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.all_contacts_row, null);

                // Creates a ViewHolder and store references to the two children views
                // we want to bind data to.
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.contact_name);
                holder.checked = (CheckBox) convertView.findViewById(R.id.contact_is_checked);
                holder.phone = (TextView) convertView.findViewById(R.id.contact_number);
                //holder.phone = (TextView) convertView.findViewById(R.id.textView2);

                // Bind the data efficiently with the holder.
                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // If weren't re-ordering this you could rely on what you set last time
            holder.name.setText(filteredContacts.get(position).getName());
            holder.phone.setText(filteredContacts.get(position).getPhone());
            holder.checked.setChecked(filteredContacts.get(position).getIsChecked());

            return convertView;
        }

        public class ViewHolder {
            TextView name;
            TextView phone;
            CheckBox checked;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            Contact c = filteredContacts.get((Integer) buttonView.getTag());
            c.setIsChecked(isChecked);

        }

        @Override
        public int getPositionForSection(int section) {
            // If there is no item for current section, previous section will be selected
            for (int i = section; i >= 0; i--) {
                for (int j = 0; j < getCount(); j++) {
                    if (i == 0) {
                        // For numeric section
                        for (int k = 0; k <= 9; k++) {
                            if (StringMatcher.match(String.valueOf(getItem(j).toString().charAt(0)), String.valueOf(k)))
                                return j;
                        }
                    } else {
                        if (StringMatcher.match(String.valueOf(getItem(j).toString().charAt(0)), String.valueOf(mSections.charAt(i))))
                            return j;
                    }
                }
            }
            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override
        public Object[] getSections() {
            String[] sections = new String[mSections.length()];
            for (int i = 0; i < mSections.length(); i++)
                sections[i] = String.valueOf(mSections.charAt(i));
            return sections;
        }

        @Override
        public Filter getFilter() {
            return mFilter;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();

                // remember original list
                final List<Contact> origList = NewEventFragment.allContacts;
                int count = origList.size();

                // create new list that will be used as filteredList
                ArrayList<Contact> newList = new ArrayList<Contact>(count);

                String filterableString;

                // populate new list based on filter criteria
                for (int i = 0; i < count; i++) {
                    filterableString = origList.get(i).getName();
                    if (filterableString.toLowerCase().contains(filterString)) {
                        newList.add(origList.get(i));
                    }
                }

                results.values = newList;
                results.count = newList.size();

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // set result of filter as the filteredContacts
                filteredContacts = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();
            }

        }

        public View getView(){
            return vi;
        }
    }

    public String getType(int value){
        switch(value){
            case(ContactsContract.CommonDataKinds.Phone.TYPE_HOME):
                return "home";
            case(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE):
                return "cell";
            case(ContactsContract.CommonDataKinds.Phone.TYPE_WORK):
                return "work";
            case(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK):
                return "work fax";
            case(ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME):
                return "home fax";
            case(ContactsContract.CommonDataKinds.Phone.TYPE_PAGER):
                return "pager";
            case(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER):
                return "other";
            case(ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK):
                return "callback";
            case(ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN):
                return "company main";
            default:
                return "";
        }
    }
}
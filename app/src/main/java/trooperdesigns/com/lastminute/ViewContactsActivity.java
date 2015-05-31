package trooperdesigns.com.lastminute;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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

    private Button select;
    private EditText search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);

        search = (EditText) findViewById(R.id.search);

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
        IndexableListView lv = (IndexableListView) findViewById(R.id.lv);
        contactsAdapter = new ContactsAdapter(getApplicationContext(), originalContacts);
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

                StringBuilder checkedcontacts = new StringBuilder();
                System.out.println(".............." + originalContacts.size());
                // iterate through all contacts, and if checked, append to stringbuilder
                for (int i = 0; i < originalContacts.size(); i++)

                {
                    if (originalContacts.get(i).getIsChecked() == true) {
                        // append names of checked contacts to StringBuilder for Toast output
                        checkedcontacts.append(originalContacts.get(i).getName() + "\n");
                    } else {
                        System.out.println("Not Checked......" + originalContacts.get(i).getName());
                    }

                }

                Toast.makeText(ViewContactsActivity.this, checkedcontacts, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        // if sliding panel is open, close it
        InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if(imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0) == true){
            super.onBackPressed();
        } else if(imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0) == false && !search.getText().toString().matches("")){
            search.setText(null);
        } else {
            super.onBackPressed();
        }

        return;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        filteredContacts.get(position).toggle();
    }

    public void getAllContacts(ContentResolver cr) {

        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        // initialize Contact object for each contact in phonebook
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            originalContacts.add(new Contact(name, phoneNumber, false));
        }

        phones.close();
    }

    class ContactsAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, SectionIndexer, Filterable {
        CheckBox cb;
        private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        private LayoutInflater mInflater;
        private ItemFilter mFilter = new ItemFilter();

        ContactsAdapter(Context context, List<Contact> contacts) {

            // set originalContacts and filteredContacts as original Contacts list
            originalContacts = contacts;
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
            View vi = convertView;
            if (convertView == null) vi = mInflater.inflate(R.layout.all_contacts_row, null);

            TextView tv = (TextView) vi.findViewById(R.id.contact_name);
            cb = (CheckBox) vi.findViewById(R.id.contact_is_checked);
            tv.setText("Name: " + filteredContacts.get(position).getName());

            // TODO: Uncomment next line when using phone number as username to query
            //tv1.setText("Phone No:" + phone.get(position));
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
            holder.checked.setChecked(filteredContacts.get(position).getIsChecked());

            return convertView;
        }

        public class ViewHolder {
            TextView name;
            CheckBox checked;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            filteredContacts.get((Integer) buttonView.getTag()).setIsChecked(isChecked);
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
                final List<Contact> origList = originalContacts;
                int count = origList.size();

                // create new list that will be used as filteredList
                ArrayList<Contact> newList = new ArrayList<Contact>(count);

                String filterableString ;

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
    }

    // Contact class used to hold contact information
    public class Contact {
        private boolean isChecked;
        private String name;
        private String phone;

        public Contact(String name, String phone, boolean isChecked) {
            this.name = name;
            this.phone = phone;
            this.isChecked = isChecked;
        }

        public void setName(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }

        public void setPhone(String phone){
            this.phone = phone;
        }

        public String getPhone(){
            return phone;
        }

        public void setIsChecked(boolean isChecked){
            this.isChecked = isChecked;
        }

        public boolean getIsChecked(){
            return isChecked;
        }

        public void toggle(){
            this.isChecked = !isChecked;
        }

    }
}
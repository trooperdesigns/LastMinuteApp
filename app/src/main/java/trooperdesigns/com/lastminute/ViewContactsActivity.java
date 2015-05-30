package trooperdesigns.com.lastminute;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
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

    List<String> name = new ArrayList<String>();
    List<String> phone = new ArrayList<String>();
    ContactsAdapter contactsAdapter;

    private List<String> originalData = null;
    private List<String> filteredData = null;

    Button select;
    EditText search;

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
        contactsAdapter = new ContactsAdapter(getApplicationContext(), name, phone);
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
                System.out.println(".............." + contactsAdapter.mCheckStates.size());
                for (int i = 0; i < filteredData.size(); i++)

                {
                    if (contactsAdapter.mCheckStates.get(i) == true) {
                        checkedcontacts.append(filteredData.get(i).toString() + "\n");
                    } else {
                        System.out.println("Not Checked......" + filteredData.get(i).toString());
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
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        contactsAdapter.toggle(arg2);
    }

    public void getAllContacts(ContentResolver cr) {

        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        // iterate through all contacts and add names and numbers to their own arraylists
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            this.name.add(name);
            phone.add(phoneNumber);
        }

        phones.close();
    }

    class ContactsAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener, SectionIndexer, Filterable {
        private SparseBooleanArray mCheckStates;
        TextView tv1, tv;
        CheckBox cb;
        private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";


        private List<String> originalPhoneData = null;

        private List<String> filteredPhoneData = null;
        private LayoutInflater mInflater;
        private ItemFilter mFilter = new ItemFilter();

        ContactsAdapter(Context context, List<String> nameData, List<String> phoneData) {

            filteredData = nameData ;
            originalData = nameData ;

            this.filteredPhoneData = phoneData;
            this.originalPhoneData = phoneData;

            mCheckStates = new SparseBooleanArray(filteredData.size());

            mInflater = LayoutInflater.from(context);
            mInflater = (LayoutInflater) ViewContactsActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return filteredData.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return filteredData.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub

            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi = convertView;
            if (convertView == null) vi = mInflater.inflate(R.layout.all_contacts_row, null);
            TextView tv = (TextView) vi.findViewById(R.id.textView1);
            //tv1 = (TextView) vi.findViewById(R.id.textView2);

            cb = (CheckBox) vi.findViewById(R.id.checkBox1);
            tv.setText("Name: " + filteredData.get(position));
            //tv1.setText("Phone No:" + phone.get(position));
            cb.setTag(position);
            cb.setChecked(mCheckStates.get(position, false));
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
                holder.name = (TextView) convertView.findViewById(R.id.textView1);
                //holder.phone = (TextView) convertView.findViewById(R.id.textView2);

                // Bind the data efficiently with the holder.

                convertView.setTag(holder);
            } else {
                // Get the ViewHolder back to get fast access to the TextView
                // and the ImageView.
                holder = (ViewHolder) convertView.getTag();
            }

            // If weren't re-ordering this you could rely on what you set last time
            holder.name.setText(filteredData.get(position));
            //holder.phone.setText(filteredPhoneData.get(position));

            return convertView;
        }

        public class ViewHolder {
            TextView name;
            TextView phone;
        }

        public boolean isChecked(int position) {
            return mCheckStates.get(position, false);
        }

        public void setChecked(int position, boolean isChecked) {
            mCheckStates.put(position, isChecked);
            System.out.println("hello...........");
            notifyDataSetChanged();
        }

        public void toggle(int position) {
            setChecked(position, !isChecked(position));
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {
            // TODO Auto-generated method stub

            mCheckStates.put((Integer) buttonView.getTag(), isChecked);
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

                final List<String> origList = originalData;

                int count = origList.size();
                final ArrayList<String> nlist = new ArrayList<String>(count);

                String filterableString ;

                for (int i = 0; i < count; i++) {
                    filterableString = origList.get(i);
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(filterableString);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<String>) results.values;
                //mCheckStates = new SparseBooleanArray(filteredData.size());

                notifyDataSetChanged();
            }

        }
    }
}
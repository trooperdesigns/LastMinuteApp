package trooperdesigns.com.lastminute;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

// custom gridview class created so don't need to configure gridview every single time with adapter

public class SlideUpGrid extends GridView {

    private LayoutInflater inflater;
    private Integer[] mThumbIds = {
            R.drawable.settings_icon,
            R.drawable.settings_icon,
            R.drawable.settings_icon,
            R.drawable.settings_icon
    };

    public SlideUpGrid(Context context) {
        super(context);
        super.setAdapter(new GridAdapter(context));
        super.setNumColumns(4);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent i = new Intent(getContext(), SettingsActivity.class);
                        getContext().startActivity(i);
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
            }
        });

        // disable gridView scrolling so it doesn't get in the way
        setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }

        });

    }

    public SlideUpGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideUpGrid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public class GridAdapter extends BaseAdapter {

        private Context mContext;

        public GridAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return mThumbIds.length;
        }

        @Override
        public Object getItem(int arg0) {
            return mThumbIds[arg0];
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View grid;

            if (convertView == null) {
                grid = new View(mContext);
                grid = inflater.inflate(R.layout.settings_grid, parent, false);
            } else {
                grid = (View) convertView;
            }

            ImageView imageView = (ImageView) grid.findViewById(R.id.image);
            imageView.setImageResource(mThumbIds[position]);

            return grid;
        }
    }

}



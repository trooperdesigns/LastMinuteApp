/*
 * Copyright 2011 woozzu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package trooperdesigns.com.lastminute.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

import trooperdesigns.com.lastminute.NewEventFragment;
import trooperdesigns.com.lastminute.ViewContactsActivity;

public class IndexableListView extends ListView {

	//static boolean isKeyboardOpen = false;
	private boolean mIsFastScrollEnabled = false;
	private IndexScroller mScroller = new IndexScroller(getContext(), this);
	private GestureDetector mGestureDetector = null;
	Canvas canvas;


	public IndexableListView(Context context) {
		super(context);
	}

	public IndexableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IndexableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFastScrollEnabled() {
		return mIsFastScrollEnabled;
	}

	@Override
	public void setFastScrollEnabled(boolean enabled) {
		Log.d("fastscroll", "fastScrollEnabled: " + enabled);
		mIsFastScrollEnabled = enabled;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		this.canvas = canvas;
		
		// Overlay index bar
		if (mScroller != null && !ViewContactsActivity.isKeyboardOpen)
			mScroller.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Intercept ListView's touch event
		if (mScroller != null && mScroller.onTouchEvent(ev))
			return true;
		
		if (mGestureDetector == null) {
			mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					// If fling happens, index bar shows
					if (mScroller != null)
						mScroller.show();
					return super.onFling(e1, e2, velocityX, velocityY);
				}
				
			});
		}
		mGestureDetector.onTouchEvent(ev);
		
		return super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(mScroller == null){
			Log.d("fastscroll", "scroller is null");
		}
		if(mScroller != null){
			if(mScroller.contains(ev.getX(), ev.getY()))
				return true;
		}
		
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		if (mScroller != null)
			mScroller.setAdapter(adapter);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mScroller != null)
			mScroller.onSizeChanged(w, h, oldw, oldh);
	}

	public Canvas getCanvas(){
		return canvas;
	}

	public IndexScroller getScroller(){
		return mScroller;
	}

}

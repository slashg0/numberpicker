package com.slashg.pickerview;

import android.R.color;
import android.content.Context;
import android.media.MediaPlayer;
import android.test.suitebuilder.annotation.Smoke;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class HorizontalNumberPicker extends RelativeLayout implements OnScrollChangedListener{

	/*
	 * This is a custom implementation of a horizontal number picker
	 * 
	 * ---
	 * Author: SlashG
	 */
	
	/*
	 * Set temporary views
	 * 
	 */
	TextView scroll, selected;
	View overlay;
	public void setScroll(TextView scroll) {
		this.scroll = scroll;
	}

	public void setSelected(TextView selected) {
		this.selected = selected;
	}

	float x, y;
	int intScroll, selectedItem = 0;
	/* End of temporary view declaration  */
	HorizontalScrollViewExt scroller;
	LinearLayout container;
	NumberPickerItem[] items;
	MediaPlayer ticker;										//MediaPlayer instance to play the optional tick sound
	GestureDetector gestureAdapter;
	View topHighlight, bottomHighlight;
	// 	Variables defining the look and behaviour of the view
	
	private int visibleChildrenCount = 3,					//	Number of children visible at a particular time (plays with the width of elements vs thhat of the view)
				childWidth = -1;
	private boolean playSounds = true;
	
	public NumberPickerItem[] getItems() {
		return items;
	}

	public void setItems(NumberPickerItem[] items) {
		this.items = items;
	}

	public int getChildWidth() {
		return childWidth;
	}

	public void setChildWidth(int childWidth) {
		this.childWidth = childWidth;
	}

	public int getVisibleChildrenCount() {
		return visibleChildrenCount;
	}

	public void setSoundsEnabled(boolean playSounds) {
		this.playSounds = playSounds;
	}

	public HorizontalNumberPicker(Context context) {
		super(context);
		init(context);
	}

	public HorizontalNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public HorizontalNumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
		
	}
	
	
	public void init(Context context)
	{
		/*
		 * Init objects and references of the layout
		 */
		setBackgroundColor(color.black);
		View layout = RelativeLayout.inflate(context, R.layout.layout_horizontal_picker, this);
		scroller = (HorizontalScrollViewExt) layout.findViewById(R.id.number_picker_scroller);
		container = (LinearLayout) layout.findViewById(R.id.number_picker_items_go_here);
		overlay = findViewById(R.id.number_picker_overlay);
		topHighlight = findViewById(R.id.number_picker_highlight_top);
		bottomHighlight = findViewById(R.id.number_picker_highlight_bottom);
		//scroller = layout.findViewById(R.id.number_picker_scroller);
		ticker = MediaPlayer.create(context, R.raw.tick_sound);
		
		try {
			
			overlay.setBackgroundDrawable(((View)getParent()).getBackground());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		scroller.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener() {
			
			@Override
			public void onScrollChanged() {
				
				int scrolledItem;
				intScroll = scroller.getScrollX() + (childWidth / 2);
				
				x = (float) intScroll % childWidth;
				y = x / childWidth;
				
				if(childWidth > 0)
				{
					scrolledItem = 1 + (int) (intScroll / childWidth);
					if( selectedItem != scrolledItem)
					{
						selectedItem = scrolledItem;
						onTick();
					}
				}
				
//				if(scroll != null)
//					scroll.setText(" x = " + x);
				if(selected != null)
					selected.setText(" y = " + y);
			}
		});
		
		scroller.addOnFlingCallback(new OnFlingCallback() {
			
			@Override
			public void flung(float velocity) {
				Toast.makeText(getContext(), "Flung", Toast.LENGTH_SHORT).show();
			}
		});
		scroller.addOnStoppedCallback(new OnStoppedCallback() {
			
			@Override
			public void onStopScroll() {
				Toast.makeText(getContext(), "Stopped Scrolling", Toast.LENGTH_SHORT).show();
				autoAdjustScroll();
			}
		});
		
		scroller.addVelocityCallback(new VelocityCallback() {
			
			@Override
			public void velocityUpdate(float velocity) {
				if(scroll != null)
					scroll.setText(" velocity = " + velocity);
			}
		});
	}
	
	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();

		System.out.println("HorizontalNumberPicker::onFinishInflate() childCount = " + getChildCount());
		int i = 0 ;
		while(getChildCount() > 1)
		{
			/*
			 * Loop to move all children to the actual intended container
			 */
			
			System.out.println("HorizontalNumberPicker::onFinishInflate() iteration #" + (i++) + " | childCount = " + getChildCount());
			View temp = getChildAt(1);
			removeViewAt(1);
			container.addView(new NumberPickerItem(getContext(), temp));
		}
	}
	
	public void onTick()
	{
		/**
		 * Method called when the ticker crosses a new value
		 * 
		 * All actions to be performed on change of selected value should be put here. For example, playing the optional sound
		 */
		System.out.println("HorizontalNumberPicker::onTick() Selected value : " + getSelectedIndex());
		//playTickSound();
	}
	
	public int getSelectedIndex()
	{
		return selectedItem;
	}
	
	@Override
	protected void onMeasure(int w, int h)
	{
		super.onMeasure(w, h);
		
		System.out.println("HorizontalNumberPicker::onMeasure()         width : " + getMeasuredWidth() + ",         height : " + getMeasuredHeight());
		measureChildWidth(getMeasuredWidth(), visibleChildrenCount);
		updateAllChildren();
		adjustHightlightWidth();
		setScrollerPadding(getMeasuredWidth());
	}
	
	private void measureChildWidth ( int parentWidth, int noOfVisibleElements )
	{
		/**
		 * Method to calculate child width for scroller elements based on scroller width and no. of visible elements
		 */
		childWidth = parentWidth / noOfVisibleElements;

	}
	
	private void adjustHightlightWidth()
	{
		if(topHighlight != null)
		{
			android.view.ViewGroup.LayoutParams params = topHighlight.getLayoutParams();
			params.width = childWidth;
			topHighlight.setLayoutParams(params);
		}
		if(bottomHighlight != null)
		{
			android.view.ViewGroup.LayoutParams params = bottomHighlight.getLayoutParams();
			params.width = childWidth;
			bottomHighlight.setLayoutParams(params);
		}
	}
	
	private void updateAllChildren()
	{
		for (int i = 0 ; i < container.getChildCount(); i++)
		{
			View temp = container.getChildAt(i);
			temp.setLayoutParams(new LinearLayout.LayoutParams(childWidth, temp.getLayoutParams().height));
		}
	}
	
	private void setScrollerPadding(int width)
	{
		int padding = width/2;
		padding -= childWidth/2;
		if(scroller != null)
		{
			scroller.setPadding(padding, 0, padding, 0);
		}
	}
	
	public void setChildren(NumberPickerItem[] items)
	{
		/*
		 * Method receives an array of children as NumberPickerItems
		 * this method puts them in the scrolling view
		 */
		this.items = items;
	}
	
	private void autoAdjustScroll()
	{
		final int expectedScrollX = (getSelectedIndex() - 1) * childWidth;
		
		scroller.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				scroller.smoothScrollTo(expectedScrollX, 0);
				scroller.invalidate();
			}
		}, 500);
	}

	@Override
	public void onScrollChanged() {
		
		/*
		 * Method to contain 'auto-scroll to item' logic
		 * and tick sounds
		 */
	}
	
	private void setScaleAndAlphaFromScroll (NumberPickerItem item, int position)
	{
		int fullWidth = childWidth * visibleChildrenCount;					//Calculate total width of the scroller
		int halfWidth = fullWidth / 2;										//Calculate center of the scroller
		int scrollDistance = position * childWidth;
		int distanceFromCenter = halfWidth - scrollDistance;				//To see how far the view is from the center
		
	}
	
	public void playTickSound()
	{
		/*
		 * If the 'ticker' object isn't null and playSounds isn't false,
		 * play the tick sound
		 */
		if(ticker == null || !playSounds)
		{
			return;
		}
		if(ticker.isPlaying())
			ticker.seekTo(0);
		else
			ticker.start();
	}
}

package com.slashg.pickerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class HorizontalScrollViewExt extends HorizontalScrollView {
	

	public HorizontalScrollViewExt(Context context) {
		super(context);
		init(context);
	}
	
	public HorizontalScrollViewExt(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public HorizontalScrollViewExt(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}


	private OnFlingCallback[] flingCallbacks;
	private OnStoppedCallback[] stoppedCallbacks;
	private VelocityCallback[] velocityUpdateCallbacks;
	private GestureDetector gestureDetector;
	private CountDownTimer speedMeasureTimer;										// 100 ms timer that measure scroll distance per second (scroll velocity)
	private float scrollVelocity = 0f;
	int scrollX = 0, newScrollX = 0;
	boolean isScrolled = false;														//boolean flag to distinguish between scrolls, flings and taps
	
	
	
	private void init(Context context)
	{
		flingCallbacks = new OnFlingCallback[0];
		stoppedCallbacks = new OnStoppedCallback[0];
		velocityUpdateCallbacks = new VelocityCallback[0];
		
		speedMeasureTimer = new CountDownTimer(100, 100) {
			
			@Override
			public void onTick(long millisUntilFinished) {
			
			}
			
			@Override
			public void onFinish() {
				float seconds = 0.1f;
				newScrollX = getScrollX();
				scrollVelocity = (newScrollX - scrollX) / seconds;
				onVelocityUpdate(scrollVelocity);
				scrollX = newScrollX;
			}
		};
		
		gestureDetector = new GestureDetector(context, new OnGestureListener() {
			
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				System.out.println("HorizontalScrollViewExt::onSingleTapUp( )" );
				return false;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {
				System.out.println("HorizontalScrollViewExt::onShowPress( )" );
				
			}
			
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
					float distanceY) {
				isScrolled = true;
				System.out.println("HorizontalScrollViewExt::onScroll( )" );
				return true;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
				System.out.println("HorizontalScrollViewExt::onLongPress( )" );
				
			}
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
				isScrolled = false;																		//set scroll flag to false to make 'fling' more dominant
				System.out.println("HorizontalScrollViewExt::onFling( )" );
				speedMeasureTimer.start();
				triggerOnFlingCallbacks(velocityX);
				return true;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				System.out.println("HorizontalScrollViewExt::onDown( )" );
				return false;
			}
		});
		
	}
	
	@SuppressLint("ClickableViewAccessibility") @Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		
		gestureDetector.onTouchEvent(ev);
		if(ev.getAction() == MotionEvent.ACTION_UP && isScrolled)
		{
			triggerOnStoppedCallback();
			isScrolled = false;
		}
		return super.onTouchEvent(ev);
	}
	
	private void onVelocityUpdate(float velocity)
	{
		System.out.println("HorizontalScrollViewExt::onVelocityUpdate( " + velocity + " )" );
		scrollVelocity = velocity;
		triggerVelocityCallback(velocity);
		if(velocity == 0)
		{
			onStopScroll();
		}
		else
		{
			speedMeasureTimer.start();
		}
	}
	
	private void onStopScroll()
	{
		System.out.println("HorizontalScrollViewExt::onStopScroll()" );
		speedMeasureTimer.cancel();
		triggerOnStoppedCallback();
	}
	
	public void addOnFlingCallback(OnFlingCallback callback)
	{
		OnFlingCallback[] temp = flingCallbacks;
		flingCallbacks = new OnFlingCallback[temp.length + 1];
		for (int i = 0; i<temp.length ; i++)
		{
			flingCallbacks[i] = temp[i];
		}
		flingCallbacks[temp.length] = callback;
	}
	
	public void addVelocityCallback(VelocityCallback callback)
	{
		VelocityCallback[] temp = velocityUpdateCallbacks;
		velocityUpdateCallbacks = new VelocityCallback[temp.length + 1];
		for (int i = 0; i<temp.length ; i++)
		{
			velocityUpdateCallbacks[i] = temp[i];
		}
		velocityUpdateCallbacks[temp.length] = callback;
	}
	
	public void addOnStoppedCallback(OnStoppedCallback callback)
	{
		OnStoppedCallback[] temp = stoppedCallbacks;
		stoppedCallbacks = new OnStoppedCallback[temp.length + 1];
		for (int i = 0; i<temp.length ; i++)
		{
			stoppedCallbacks[i] = temp[i];
		}
		stoppedCallbacks
		[temp.length] = callback;
	}
	
	private void triggerOnFlingCallbacks(float velocity)
	{
		/**
		 * Triggers all registered callback actions for fling events
		 */
		
		for (OnFlingCallback flingCallback : flingCallbacks) {
			if(flingCallback != null)
			{
				flingCallback.flung(velocity);
			}
		}
	}
	
	private void triggerVelocityCallback(float velocity)
	{
		/**
		 * Triggers all registered callback actions with scroll velocity updates
		 */
		for (VelocityCallback velocityCallback : velocityUpdateCallbacks) {
			if(velocityCallback != null)
			{
				velocityCallback.velocityUpdate(velocity);
			}
		}
	}
	
	private void triggerOnStoppedCallback()
	{
		/**
		 * Triggers all registered callback actions for end-of-scroll events
		 */
		for (OnStoppedCallback onStoppedCallback : stoppedCallbacks) {
			if(onStoppedCallback != null)
			{
				onStoppedCallback.onStopScroll();
			}
		}
	}
	
}

// Callback interfaces for fling, velocity-update and end of scroll
interface OnFlingCallback {	public void flung(float velocity); }
interface VelocityCallback { public void velocityUpdate(float velocity); }
interface OnStoppedCallback { public void onStopScroll(); }
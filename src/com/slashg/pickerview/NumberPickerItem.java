package com.slashg.pickerview;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;;

public class NumberPickerItem extends FrameLayout {

	public NumberPickerItem(Context context) {
		super(context);
	}
	
	public NumberPickerItem(Context context, View child)
	{
		this(context);
		init(context, child);
	}
	
	private void init(Context context, View  child){
		setPadding(
				(int)context.getResources().getDimension(R.dimen.number_picker_item_horizontal_padding),
				(int)context.getResources().getDimension(R.dimen.number_picker_item_vertical_padding),
				(int)context.getResources().getDimension(R.dimen.number_picker_item_horizontal_padding),
				(int)context.getResources().getDimension(R.dimen.number_picker_item_vertical_padding)
				);
		removeAllViews();
		addView(child);
	}

}

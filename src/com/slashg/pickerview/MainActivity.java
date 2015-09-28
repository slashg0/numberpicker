package com.slashg.pickerview;

import org.w3c.dom.Text;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		int SCREEN_WIDTH = getWindowManager().getDefaultDisplay().getWidth();
		int SCREEN_HEIGHT = getWindowManager().getDefaultDisplay().getHeight();
		
        HorizontalNumberPicker hnp = (HorizontalNumberPicker)findViewById(R.id.scroller);
        TextView scroll = (TextView) findViewById(R.id.scroll_indicator);
        TextView select = (TextView) findViewById(R.id.selected_indicator);
        hnp.setScroll(scroll);
        hnp.setSelected(select);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

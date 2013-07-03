package com.barkeeper;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class BarKeeper extends ListActivity {
	private DrinksDbAdapter mDbHelper;
	
	private static final int MY_CABINET_ID = Menu.FIRST;
	private static final int SPIRIT_FILTER_ID = Menu.FIRST + 1;
	private static final int DRINK_CREATOR_ID = Menu.FIRST + 2;
	private static final int RANDOM_DRINK_ID = Menu.FIRST + 3;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drink_list);
        mDbHelper = new DrinksDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
    }
    
    private void fillData() {
        // Get all of the rows from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchAllDrinks();
        startManagingCursor(notesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{DrinksDbAdapter.KEY_DRINK_NAME};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes = 
            new SimpleCursorAdapter(this, R.layout.drinks_row, notesCursor, from, to);
        setListAdapter(notes);
    }
    
    private void displayDrinksWithBase(ArrayList<String> baseSpirits) {
        // Get all of the rows from the database and create the item list
        Cursor notesCursor = mDbHelper.fetchDrinksWithBase(baseSpirits);
        startManagingCursor(notesCursor);

        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{DrinksDbAdapter.KEY_DRINK_NAME};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes = 
            new SimpleCursorAdapter(this, R.layout.drinks_row, notesCursor, from, to);
        setListAdapter(notes);
    }    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, MY_CABINET_ID, 0, R.string.my_cabinet);
        menu.add(0, DRINK_CREATOR_ID, 0, R.string.drink_creator);
        menu.add(0, SPIRIT_FILTER_ID, 0, R.string.spirit_filter);
        menu.add(0, RANDOM_DRINK_ID, 0, R.string.random_drink);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case MY_CABINET_ID:
        	return true;
        case SPIRIT_FILTER_ID:
            Intent i = new Intent(this, SpiritFilter.class);
            startActivityForResult(i, 2);
            return true;
        case DRINK_CREATOR_ID:        	
        	return true;
        case RANDOM_DRINK_ID:
        	Random random = new Random();
            Cursor notesCursor = mDbHelper.fetchAllDrinks();
            //startManagingCursor(notesCursor);
        	long randomInt = random.nextInt(notesCursor.getCount()); //returns random number from 0 - getCount
            Intent j = new Intent(this, DrinkDisplay.class);
            j.putExtra(DrinksDbAdapter.KEY_ROWID, randomInt);
            startActivityForResult(j, 1);
        	return true;
        }        	
        return super.onMenuItemSelected(featureId, item);
    }
       
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, DrinkDisplay.class);
        i.putExtra(DrinksDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
        	case (2) : {
        		if (resultCode == Activity.RESULT_OK) {
        			ArrayList<String> spiritList = intent.getStringArrayListExtra("SpiritList");
        			displayDrinksWithBase(spiritList);
        		}
        	}
        	break;
        }
        //fillData();
    }
}
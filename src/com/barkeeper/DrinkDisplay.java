package com.barkeeper;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class DrinkDisplay extends Activity {
	
    private TextView mDrinkName;
    private TextView mDrinkIngredients;
    private TextView mDrinkDirections;
    private Long mRowId;
    private DrinksDbAdapter mDbHelper;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new DrinksDbAdapter(this);
        mDbHelper.open();
        setContentView(R.layout.drink_display);
        mDrinkName = (TextView) findViewById(R.id.drink_name);
        mDrinkIngredients = (TextView) findViewById(R.id.ingredients);
        mDrinkDirections = (TextView) findViewById(R.id.directions);
        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(DrinksDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(DrinksDbAdapter.KEY_ROWID)
                                    : null;
        }
        populateDrinkDisplay();
        
    }
    
    public void populateDrinkDisplay() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchDrink(mRowId);
            startManagingCursor(note);
            mDrinkName.setText(note.getString(
                        note.getColumnIndexOrThrow(DrinksDbAdapter.KEY_DRINK_NAME)));
            mDrinkIngredients.setText(note.getString(
                    note.getColumnIndexOrThrow(DrinksDbAdapter.KEY_INGREDIENTS)));
            mDrinkDirections.setText(note.getString(
                    note.getColumnIndexOrThrow(DrinksDbAdapter.KEY_DIRECTIONS)));
        }
    }
}
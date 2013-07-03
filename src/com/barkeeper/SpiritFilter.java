package com.barkeeper;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class SpiritFilter extends Activity implements OnClickListener {
	private static final String PUBLIC_STATIC_SPIRIT_LIST = "SpiritList";
	CheckBox vodkaCheckbox, ginCheckbox, whiskeyCheckbox, rumCheckbox, tequilaCheckbox,
	brandyCheckbox, piscoCheckbox, liqueurCheckbox;
	private ArrayList<String> mBase;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mBase = new ArrayList<String>();
		setContentView(R.layout.spirit_filter);
    	vodkaCheckbox = (CheckBox) findViewById(R.id.vodka_checkbox);
    	ginCheckbox = (CheckBox) findViewById(R.id.gin_checkbox);
    	whiskeyCheckbox = (CheckBox) findViewById(R.id.whiskey_checkbox);
    	rumCheckbox = (CheckBox) findViewById(R.id.rum_checkbox);
    	tequilaCheckbox = (CheckBox) findViewById(R.id.tequila_checkbox);
    	brandyCheckbox = (CheckBox) findViewById(R.id.brandy_checkbox);
    	piscoCheckbox = (CheckBox) findViewById(R.id.pisco_checkbox);
    	liqueurCheckbox = (CheckBox) findViewById(R.id.liqueur_checkbox);
        Button button = (Button)findViewById(R.id.ok);
        button.setOnClickListener(this);
	}
    public void onClick(View v) {
        // do something when the button is clicked
      if (vodkaCheckbox.isChecked()) mBase.add("Vodka");
      if (ginCheckbox.isChecked()) mBase.add("Gin");
      if (whiskeyCheckbox.isChecked()) { 
    	  mBase.add("Whiskey (Bourbon)");
    	  mBase.add("Whiskey (Scotch)");
    	  mBase.add("Whiskey (Rye)");
    	  mBase.add("Whiskey (Irish)");
      }
      if (rumCheckbox.isChecked()) mBase.add("Rum");
      if (tequilaCheckbox.isChecked()) mBase.add("Tequila");
      if (brandyCheckbox.isChecked()) mBase.add("Brandy");
      if (piscoCheckbox.isChecked()) mBase.add("Pisco");
      if (liqueurCheckbox.isChecked()) mBase.add("Liqueur");
      Intent resultIntent = new Intent();
      resultIntent.putExtra(PUBLIC_STATIC_SPIRIT_LIST, mBase);
      setResult(Activity.RESULT_OK, resultIntent);
      finish();
    }

	
}

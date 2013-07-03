package com.barkeeper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DrinksDbAdapter {
    public static final String KEY_DRINK_NAME = "Name";
    public static final String KEY_BASE_SPIRIT = "Base";
    public static final String KEY_INGREDIENTS = "Ingredients";
    public static final String KEY_DIRECTIONS = "Directions";
    public static final String KEY_ROWID = "_id";
    
    private static final String DATABASE_PATH = "/data/data/com.barkeeper/databases";
    private static final String DATABASE_NAME = "barkeeper_db";
    private static final String DATABASE_TABLE = "drinks";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    private final Context mCtx;
    

    private static class DatabaseHelper extends SQLiteOpenHelper {

    	/**
         * Database creation sql statement
         */
        private static final String DATABASE_CREATE =
        	"create table " + DATABASE_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, "
        	+ KEY_DRINK_NAME + " text not null, " + KEY_INGREDIENTS + " text not null);";

        private static final int DATABASE_VERSION = 2;        
        private static final String TAG = "DrinksDbAdapter";
        private final Context dbCtx;
        
    	DatabaseHelper(Context context) {
    		super(context, DATABASE_NAME, null, DATABASE_VERSION);
    		dbCtx = context;
    	}

    	@Override
    	public void onCreate(SQLiteDatabase db) {

    		db.execSQL(DATABASE_CREATE);
    	}

    	@Override
    	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
    				+ newVersion + ", which will destroy all old data");
    		db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
    		onCreate(db);
    	}
    	
    	  /**
         * Creates a empty database on the system and rewrites it with your own database.
         * */
        public void createDatabase() throws IOException{     
        	boolean dbExist = checkDatabase();     
        	//if(dbExist){
        		//do nothing - database already exist
        	//}
        	//else{     
        		//By calling this method and empty database will be created into the default system path
                   //of your application so we are gonna be able to overwrite that database with our database.
            	this.getReadableDatabase();     
            	try {     
        			copyDatabase();     
        		} catch (IOException e) {     
            		throw new Error("Error copying database");     
            	}
        	//}
     
        }
    	
        /**
         * Check if the database already exist to avoid re-copying the file each time you open the application.
         * @return true if it exists, false if it doesn't
         */
        private boolean checkDatabase(){     
        	SQLiteDatabase checkDB = null;     
        	try{
        		String myPath = DATABASE_PATH + DATABASE_NAME;
        		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
           	}
        	catch(SQLiteException e){     
        		//database does't exist yet.     
        	}     
        	if(checkDB != null){     
        		checkDB.close();     
        	}     
        	return checkDB != null ? true : false;
        }
        /**
         * Copies your database from your local assets-folder to the just created empty database in the
         * system folder, from where it can be accessed and handled.
         * This is done by transfering bytestream.
         * */
        private void copyDatabase() throws IOException{     
        	//Open your local db as the input stream
        	InputStream myInput = dbCtx.getAssets().open(DATABASE_NAME);     
        	// Path to the just created empty db
        	String outFileName = DATABASE_PATH + DATABASE_NAME;     
        	//Open the empty db as the output stream
        	OutputStream myOutput = new FileOutputStream(outFileName);     
        	//transfer bytes from the inputfile to the outputfile
        	byte[] buffer = new byte[1024];
        	int length;
        	while ((length = myInput.read(buffer))>0){
        		myOutput.write(buffer, 0, length);
        	}     
        	//Close the streams
        	myOutput.flush();
        	myOutput.close();
        	myInput.close();     
        }
    }
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public DrinksDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DrinksDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        try {
        	mDbHelper.createDatabase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String myPath = DATABASE_PATH + DATABASE_NAME;
        mDb = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    /**
     * Create a new drink with the name and ingredients given. If the drink is
     * successfully created return the new rowId for that drink, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createDrink(String name, String ingredients) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DRINK_NAME, name);
        initialValues.put(KEY_INGREDIENTS, ingredients);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteDrink(long rowId) {

        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllDrinks() {

        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_DRINK_NAME,
        		KEY_INGREDIENTS, KEY_DIRECTIONS}, null, null, null, null, KEY_DRINK_NAME);
    }

    public Cursor fetchDrink(long rowId) throws SQLException {

        Cursor mCursor =

            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
            		KEY_DRINK_NAME, KEY_INGREDIENTS, KEY_DIRECTIONS}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    public Cursor fetchDrinksWithBase(ArrayList<String> baseSpirits) {
    	if (baseSpirits.size() == 0) {
    		return null;
    	}
    	String[] spiritArray = baseSpirits.toArray(new String[baseSpirits.size()]);
    	String questionMarks = "";
    	for (int i=1; i < spiritArray.length; ++i) {
    		//construct ?'s for query
    		questionMarks = questionMarks + ",?";
    	}
        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
            		KEY_DRINK_NAME, KEY_BASE_SPIRIT, KEY_INGREDIENTS, KEY_DIRECTIONS}, 
            		KEY_BASE_SPIRIT + " IN (?" + questionMarks + ")", spiritArray,
                    null, null, KEY_DRINK_NAME, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }    

    public boolean updateDrink(long rowId, String title, String body) {
        ContentValues args = new ContentValues();
        args.put(KEY_DRINK_NAME, title);
        args.put(KEY_INGREDIENTS, body);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
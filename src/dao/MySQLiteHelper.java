package dao;

import java.util.Date;

import com.example.weatherapp.Location;
import com.example.weatherapp.Weather;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_NAME_LOC = "locations";
	public static final String TABLE_NAME_DATA = "weatherdata";
	public static final String COLUMN_LOCID = "_id";
	public static final String COLUMN_DATID = "_id";
	
	public static final String COLUMN_TIMESTAMP = "timestamp";
	
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LONGITUDE = "longitude";
	public static final String COLUMN_LATITUDE = "latitude";
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_MAX_TEMP = "max_temperature";
	
	public static final String COLUMN_MAIN = "main";
	public static final String COLUMN_TEMPERATURE = "temperature";
	public static final String COLUMN_WINDSPEED = "windspeed";
	public static final String COLUMN_WINDDEGREE = "winddegree";
	public static final String COLUMN_DOWNFALL = "downfall";
	public static final String COLUMN_LOCATION = "location";
	
	public static final String DATABASE_NAME = "weather.db";
	public static final int DATABASE_VERSION = 6;
	
	public static final String CREATE_TABLE_LOCATIONS = "create table " + TABLE_NAME_LOC + "(" + COLUMN_LOCID + 
			" integer primary key autoincrement, " + COLUMN_NAME + " text, " + COLUMN_LONGITUDE + " double, " + COLUMN_LATITUDE + " double, " + COLUMN_ADDRESS + " text, " + COLUMN_MAX_TEMP + " text);";
	
	public static final String CREATE_TABLE_DATA = "create table " + TABLE_NAME_DATA + "(" + COLUMN_DATID + 
			" integer primary key autoincrement, " + COLUMN_MAIN + " text, " + COLUMN_TEMPERATURE + " text, " + COLUMN_WINDSPEED + " text, " + COLUMN_WINDDEGREE + " text, " +
			COLUMN_DOWNFALL + " text, " + COLUMN_LOCATION + " text, " + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP);";
	
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_LOCATIONS);
		db.execSQL(CREATE_TABLE_DATA);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LOC);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_DATA);
		
		onCreate(db);
	}
	
	public WeatherCursor queryAllWeather(int sortBy){
		Cursor wrapped = null;
		
		switch(sortBy){
		case(1): {
			wrapped = getReadableDatabase().query(TABLE_NAME_DATA, null, null, null, null, null, COLUMN_TIMESTAMP);
			Log.d("sort", "Time");
		}
		case(2): {
			wrapped = getReadableDatabase().query(TABLE_NAME_DATA, null, null, null, null, null, COLUMN_LOCATION + " COLLATE NOCASE");
			Log.d("sort", "Location");
		}	
		case(3): {
			wrapped = getReadableDatabase().query(TABLE_NAME_DATA, null, null, null, null, null, COLUMN_TEMPERATURE);
			Log.d("sort", "Temperature");
		}
		
		}
		return new WeatherCursor(wrapped);
	}
	
	public void deleteData(String deletionTime){
		SQLiteDatabase db = this.getWritableDatabase();
		String deletionString = "DELETE FROM " + TABLE_NAME_DATA + " WHERE " + COLUMN_TIMESTAMP + " <= date('now','-" + deletionTime + " day')";
		db.execSQL(deletionString);
		db.close();
		Log.d("Del", "Deleted, data: - MYSQLITEHELPER");
	}
	
	public WeatherCursor queryWeather(long id){
		Cursor wrapped = getReadableDatabase().query(TABLE_NAME_DATA, null, COLUMN_DATID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
		return new WeatherCursor(wrapped);
	}
	
	public LocationCursor queryLocations(){
		Cursor wrapped = getReadableDatabase().query(TABLE_NAME_LOC, null, null, null, null, null, null);
		return new LocationCursor(wrapped);
	}
	
	public LocationCursor queryLocation(long id){
		Cursor wrapped = getReadableDatabase().query(TABLE_NAME_LOC, null, COLUMN_LOCID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
		return new LocationCursor(wrapped);
	}
	
	public static class WeatherCursor extends CursorWrapper{

		public WeatherCursor(Cursor cursor) {
			super(cursor);
			// TODO Auto-generated constructor stub
		}
		
	    public Weather getWeather() {
            if (isBeforeFirst() || isAfterLast()){
                return null;
            }
            Weather weather = new Weather();
            weather.setId(getLong(getColumnIndex(COLUMN_DATID)));
            weather.setMain(getString(getColumnIndex(COLUMN_MAIN)));
    		weather.setTemperature(getString(getColumnIndex(COLUMN_TEMPERATURE)));
    		weather.setWindSpeed(getString(getColumnIndex(COLUMN_WINDSPEED)));
    		weather.setWindDegree(getString(getColumnIndex(COLUMN_WINDDEGREE)));
    		weather.setDownfall(getString(getColumnIndex(COLUMN_DOWNFALL)));
    		weather.setLocation(getString(getColumnIndex(COLUMN_LOCATION)));
    		weather.setTimestamp(getString(getColumnIndex(COLUMN_TIMESTAMP)));
            
    		return weather;
        }
		
	}
	
	public static class LocationCursor extends CursorWrapper{

		public LocationCursor(Cursor cursor) {
			super(cursor);
			// TODO Auto-generated constructor stub
		}
		
	    public Location getLocation() {
            if (isBeforeFirst() || isAfterLast()){
                return null;
            }
            Location location = new Location();
            location.setId(getLong(getColumnIndex(COLUMN_DATID)));
            location.setName(getString(getColumnIndex(COLUMN_NAME)));
    		location.setLongitude(getDouble(getColumnIndex(COLUMN_LONGITUDE)));
    		location.setLatitude(getDouble(getColumnIndex(COLUMN_LATITUDE)));
    		location.setAddress(getString(getColumnIndex(COLUMN_ADDRESS)));
    		location.setMaxTemp(getString(getColumnIndex(COLUMN_MAX_TEMP)));
            
    		return location;
        }
		
	}
	
}

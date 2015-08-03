package dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.weatherapp.Location;
import com.example.weatherapp.UserPreferenceFragment;
import com.example.weatherapp.Weather;

import dao.MySQLiteHelper.LocationCursor;
import dao.MySQLiteHelper.WeatherCursor;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;

public class DAO {
	
	private static DAO instance = null;
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private Context c;
	private String[] locationColumns = {MySQLiteHelper.COLUMN_LOCID, MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_LONGITUDE,
			MySQLiteHelper.COLUMN_LATITUDE, MySQLiteHelper.COLUMN_ADDRESS, MySQLiteHelper.COLUMN_MAX_TEMP};
	private String[] weatherColumns = {MySQLiteHelper.COLUMN_DATID, MySQLiteHelper.COLUMN_MAIN, MySQLiteHelper.COLUMN_TEMPERATURE,
			MySQLiteHelper.COLUMN_WINDSPEED, MySQLiteHelper.COLUMN_WINDDEGREE, MySQLiteHelper.COLUMN_DOWNFALL, MySQLiteHelper.COLUMN_LOCATION, MySQLiteHelper.COLUMN_TIMESTAMP};
	
	private DAO(Context context) {
		dbHelper = new MySQLiteHelper(context);
		this.c = context;
	}
	
	public static DAO getInstance(Context context){
		if(instance == null){
			instance = new DAO(context.getApplicationContext());
		}
		return instance;
	}
	
	public void open(){
		database = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	//Sletter data baseret på hvad der er angivet i Preference Framework
	
	public void deleteData(){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
		String prefs = sp.getString(UserPreferenceFragment.PREF_DELETION_TIME, "7");
		dbHelper.deleteData(prefs);
		Log.d("Del", "Deleted, data: " + prefs);
	}
	
	//Opretter ny location i databasen
	
	public Location createLocation(Location location){
		open();
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, location.getName());
		values.put(MySQLiteHelper.COLUMN_LONGITUDE, location.getLongitude());
		values.put(MySQLiteHelper.COLUMN_LATITUDE, location.getLatitude());
		values.put(MySQLiteHelper.COLUMN_ADDRESS, location.getAddress());
		long insertId = database.insert(MySQLiteHelper.TABLE_NAME_LOC, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_LOC, locationColumns, MySQLiteHelper.COLUMN_LOCID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Location newLocation = cursorToLocation(cursor);
		cursor.close();
		close();
		return newLocation;
	}
	
	//Opretter et nyt weather object i databasen, ud fra et objekt givet som parameter
	// Den udkommenterede del er et forsøg på at hente den højeste temperatur for en lokation med.
	
	public Weather createWeather(Weather weather){
		open();
		String timestamp = new Date().toString();
		Log.d("time", "date: " + timestamp);
		ContentValues values  = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_MAIN, weather.getMain());
		values.put(MySQLiteHelper.COLUMN_TEMPERATURE, weather.getTemperature());
		values.put(MySQLiteHelper.COLUMN_WINDSPEED, weather.getWindSpeed());
		values.put(MySQLiteHelper.COLUMN_WINDDEGREE, weather.getWindDegree());
		values.put(MySQLiteHelper.COLUMN_DOWNFALL, weather.getDownfall());
		values.put(MySQLiteHelper.COLUMN_LOCATION, weather.getLocation());
		long insertId = database.insert(MySQLiteHelper.TABLE_NAME_DATA, null, values);
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_DATA, weatherColumns, MySQLiteHelper.COLUMN_DATID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Weather newWeather = cursorToWeather(cursor);
		cursor.close();
//		String sql = "Select max(cast(" + MySQLiteHelper.COLUMN_TEMPERATURE + " as DECIMAL)) FROM " + MySQLiteHelper.TABLE_NAME_DATA +" WHERE " + MySQLiteHelper.COLUMN_LOCATION + " = " + "'" + weather.getLocation() + "'";
//		Cursor cursor2 = database.rawQuery(sql,null);
//		cursor2.moveToFirst();
//		String temp = cursor2.getString(cursor2.getColumnIndex(MySQLiteHelper.COLUMN_TEMPERATURE));
//		String update = "UPDATE " + MySQLiteHelper.TABLE_NAME_LOC + " SET " + MySQLiteHelper.COLUMN_MAX_TEMP + " = " + temp + " WHERE " + MySQLiteHelper.COLUMN_ADDRESS + " = " + weather.getLocation();
//		database.rawQuery(update, null);
//		cursor2.close();
		close();
		return newWeather;
	}
	
	public void deleteLocation(Location location){
		long id = location.getId();
		database.delete(MySQLiteHelper.TABLE_NAME_LOC, MySQLiteHelper.COLUMN_LOCID + " = " + id, null);
	}
	
	public void deleteWeather(Weather weather){
		long id = weather.getId();
		database.delete(MySQLiteHelper.TABLE_NAME_DATA, MySQLiteHelper.COLUMN_DATID + " = " + id, null);
	}
	
	public List<Location> getAllLocations(){
		List<Location> locations = new ArrayList<Location>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_LOC, locationColumns, null, null, null, null, null);
		cursor.moveToFirst();
		
		//Cursor tjekker om den er efter den sidste række, hvis ikke fortsætter loop
		
		while(!cursor.isAfterLast()){
			Location location = cursorToLocation(cursor);
			locations.add(location);
			cursor.moveToNext();
		}
		cursor.close();
		return locations;
	}
	
	public List<Weather> getAllWeather(){
		List<Weather> weatherList = new ArrayList<Weather>();
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_DATA, weatherColumns, null, null, null, null, null);
		cursor.moveToFirst();
		
		while(!cursor.isAfterLast()){
			Weather weather = cursorToWeather(cursor);
			weatherList.add(weather);
			cursor.moveToNext();
		}
		cursor.close();
		return weatherList;
	}
	
	public LocationCursor queryLocations(){
		return dbHelper.queryLocations();
	}
	
	public WeatherCursor queryWeatherData(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		int sortBy = Integer.parseInt(prefs.getString(UserPreferenceFragment.PREF_SORT_BY, "1"));
		return dbHelper.queryAllWeather(sortBy);
	}
	
	private Location cursorToLocation(Cursor cursor){
		Location location = new Location();
		location.setId(cursor.getLong(0));
		location.setName(cursor.getString(1));
		location.setLongitude(cursor.getDouble(2));
		location.setLatitude(cursor.getDouble(3));
		location.setAddress(cursor.getString(4));
		return location;
	}
	
	private Weather cursorToWeather(Cursor cursor){
		Weather weather = new Weather();
		weather.setId(cursor.getLong(0));
		weather.setMain(cursor.getString(1));
		weather.setTemperature(cursor.getString(2));
		weather.setWindSpeed(cursor.getString(3));
		weather.setWindDegree(cursor.getString(4));
		weather.setDownfall(cursor.getString(5));
		weather.setLocation(cursor.getString(6));
		weather.setTimestamp(cursor.getString(7));
		return weather;
	}
}

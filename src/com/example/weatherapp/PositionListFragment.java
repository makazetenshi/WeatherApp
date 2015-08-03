package com.example.weatherapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dao.DAO;
import dao.MySQLiteHelper.LocationCursor;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PositionListFragment extends ListFragment implements LoaderCallbacks<Cursor> {
	

	private String latitude,longitude;
	private DAO dao;
	private Weather weatherData;
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState); 
		dao = DAO.getInstance(getActivity());
		weatherData = new Weather();
		getLoaderManager().initLoader(0, null, this);
	}
	
	//Initialiser en cursor og bruger den til at oprette nyt weather element i DB'en ved click på item i listen
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Object o = getListAdapter().getItem(position);
		final CharSequence text = "Weather Added.";
		LocationCursor lc = (LocationCursor) o;
		final Location loc = lc.getLocation();
		lc = null;
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				refreshWeather(loc.getLatitude(), loc.getLongitude(), loc.getAddress());
			}});
		
		thread.start();	
		
		Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
		toast.show();
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.container, new MenuFragment());
		ft.addToBackStack(null);
		ft.commit();
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// TODO Auto-generated method stub
		return new LocationListCursorLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		LocationCursorAdapter adapter = new LocationCursorAdapter(getActivity(), (LocationCursor)cursor);
		setListAdapter(adapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		setListAdapter(null);
	}
	
	//Tilpasser koordinaterne til OpenWeatherMap API der ikke tager decimaler med ved brug af koordinater
	
	private void trimCoords(double lat, double lon){
		String latit = lat + "";
		String longit = lon + "";
		String[] partsLat = latit.split("\\.");
		String[] partsLon = longit.split("\\.");
		latitude = partsLat[0];
		longitude = partsLon[0];
	}
	
	//Henter JSON object fra OpenWeatherMap
	
	private void refreshWeather(double lat, double lon, String locationName){
		trimCoords(lat, lon);
		
		HttpURLConnection httpConnection;
		try {
			
			httpConnection = (HttpURLConnection) new URL(getString(R.string.weather_feed)+ "lat=" + latitude + "&lon=" + longitude + "&units=metric").openConnection();
			InputStream input = httpConnection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String line;
			StringBuilder builder = new StringBuilder();
			while((line = reader.readLine()) != null){
				builder.append(line);
			}
			
			readWeather(builder.toString(), locationName);
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//Læser det hentede object og læser det ind som et weather object, som så bliver tilføjet til SQLite-databasen
	
	private void readWeather(String string, String locationName){
		try {
			JSONObject weatherJSON = new JSONObject(string);
			
			JSONArray weather = weatherJSON.getJSONArray("weather");
			JSONObject main = weatherJSON.getJSONObject("main");
			JSONObject wind = weatherJSON.getJSONObject("wind");
			
		try{
			JSONObject rain = weatherJSON.getJSONObject("rain");
			String downfall = rain.getDouble("3h")+"";
			weatherData.setDownfall(downfall);
		} catch (JSONException e){
			
		}
			JSONObject weatherDesc = weather.getJSONObject(0);
			
			String mainWeather = weatherDesc.getString("main");
			
			String temperature = main.getDouble("temp")+"";
			String windSpeed = wind.getDouble("speed")+"";
			String windDegree = wind.getInt("deg")+"";
			
			
			weatherData.setMain(mainWeather);
			weatherData.setTemperature(temperature);
			weatherData.setWindSpeed(windSpeed);
			weatherData.setWindDegree(windDegree);
			weatherData.setLocation(locationName);
			
	
			dao.createWeather(weatherData);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static class LocationListCursorLoader extends SQLiteCursorLoader{

		public LocationListCursorLoader(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Cursor loadCursor() {
			// TODO Auto-generated method stub
			return DAO.getInstance(getContext()).queryLocations();
		}
		
	}
	
	private static class LocationCursorAdapter extends CursorAdapter{

		private LocationCursor mLocationCursor;
		
		public LocationCursorAdapter(Context context, LocationCursor c) {
			super(context, c, 0);
			mLocationCursor = c;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Location location = mLocationCursor.getLocation();
			
			TextView text = (TextView) view;
			text.setText(location.toString());
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		}
		
	}

}

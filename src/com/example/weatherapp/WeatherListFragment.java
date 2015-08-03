package com.example.weatherapp;

import dao.DAO;
import dao.MySQLiteHelper.WeatherCursor;
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
import android.widget.TextView;

public class WeatherListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	private static final int LOADER = 1;
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getLoaderManager().initLoader(LOADER, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		DAO.getInstance(getActivity()).deleteData();
		return new WeatherListCursorLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		WeatherCursorAdapter adapter = new WeatherCursorAdapter(getActivity(), (WeatherCursor)cursor);
		setListAdapter(adapter);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		setListAdapter(null);
	}
	
	private static class WeatherListCursorLoader extends SQLiteCursorLoader{

		public WeatherListCursorLoader(Context context) {
			super(context);
		}

		@Override
		protected Cursor loadCursor() {
			return DAO.getInstance(getContext()).queryWeatherData();
		}
		
		
	}
	
	private static class WeatherCursorAdapter extends CursorAdapter{
		
		private WeatherCursor mWeatherCursor;

		public WeatherCursorAdapter(Context context, WeatherCursor cursor) {
			super(context, cursor, 0);
			mWeatherCursor = cursor;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			Weather weather = mWeatherCursor.getWeather();
			
			TextView text = (TextView) view;
			text.setText(weather.toString());
			
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		}
		
	}
	

}

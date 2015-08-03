package com.example.weatherapp;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends Activity {
	private static final int SHOW_PREFERENCES = 1;

	private int deletionTime = 0;
	private int sortingType = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new MenuFragment()).commit();
		}

	}

	public void clickCreatePos(View view) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.container, new LocationFragment());
		ft.addToBackStack(null);
		ft.commit();
	}

	public void clickShowPos(View view) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.container, new PositionListFragment());
		ft.addToBackStack(null);
		ft.commit();
	}

	public void clickShowData(View view) {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.container, new WeatherListFragment());
		ft.addToBackStack(null);
		ft.commit();
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
		switch (id) {
		case (R.id.menu_preferences): {
			Intent i = new Intent(this, FragmentPreferences.class);
			startActivityForResult(i, SHOW_PREFERENCES);
			return true;
			}
		}
		return false;
	}
	
	private void updateFromPreferences(){
		Context context = getApplicationContext();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		deletionTime = Integer.parseInt(prefs.getString(UserPreferenceFragment.PREF_DELETION_TIME, "7"));
		
		sortingType = Integer.parseInt(prefs.getString(UserPreferenceFragment.PREF_SORT_BY, "1"));
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == SHOW_PREFERENCES){
			updateFromPreferences();
		}
	}

	public int getDeletionTime() {
		return deletionTime;
	}

	public int getSortingType() {
		return sortingType;
	}
	
	
	
}

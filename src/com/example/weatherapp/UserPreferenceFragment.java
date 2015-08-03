package com.example.weatherapp;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class UserPreferenceFragment extends PreferenceFragment {

	public static final String PREF_DELETION_TIME = "PREF_DELETION_TIME";
	public static final String PREF_SORT_BY = "PREF_SORT_BY";

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.userpreferences);
	}
	
}

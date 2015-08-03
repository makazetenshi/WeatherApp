package com.example.weatherapp;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import dao.DAO;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LocationFragment extends Fragment {
	
	private DAO dao;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private EditText etCoords;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.new_position, container, false);
		
		dao = DAO.getInstance(getActivity()); 
		final CharSequence text = "Location created.";
		final int duration = Toast.LENGTH_SHORT;
		Button btnGPS = (Button) view.findViewById(R.id.btnGPSAssist);
		Button btnSave = (Button) view.findViewById(R.id.btnSave);
		
		final EditText etName = (EditText) view.findViewById(R.id.etName);
		final EditText etAddress = (EditText) view.findViewById(R.id.etAddress);
		etCoords = (EditText) view.findViewById(R.id.etCoords);
		
		locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
		
		btnGPS.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				locationListener = new MyLocationListener();
				
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
			}
		});
		
		
		btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = etName.getText()+"";
				String address = etAddress.getText()+"";
				String coords = etCoords.getText()+"";
				
				String[] coord = coords.split(",");
				String latitude = coord[0];
				String longitude = coord[1];
				Location location = new Location();
				location.setName(name);
				location.setAddress(address);
				location.setLatitude(Double.parseDouble(latitude));
				location.setLongitude(Double.parseDouble(longitude));
				dao.createLocation(location);
				
				if(locationListener != null){
				locationManager.removeUpdates(locationListener);
				}
				Toast toast = Toast.makeText(getActivity(), text, duration);				
				toast.show();
				
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.container, new MenuFragment());
				ft.addToBackStack(null);
				ft.commit();
				
			}
		});
		
		return view;
	}
	
	private class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(android.location.Location location) {
			// TODO Auto-generated method stub
			DecimalFormat df = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));
			etCoords.setText(df.format(location.getLatitude()) + "," + df.format(location.getLongitude()));
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	}

}

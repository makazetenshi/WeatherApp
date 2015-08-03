package com.example.weatherapp;

public class Location {
	
	public String name, address, maxTemp;
	public long id;
	public double latitude, longitude;
	
	public Location() {
		this.maxTemp = "0";
		this.name = "";
		this.address = "";
		this.id = 0;
		this.latitude = 0.0;
		this.longitude = 0.0;
	}
	
	
	
	public double getLatitude() {
		return latitude;
	}



	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}



	public double getLongitude() {
		return longitude;
	}



	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}



	public String getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(String maxTemp) {
		this.maxTemp = maxTemp;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	
	public String toString(){
		return "Name: " + name + "\n" + "Highest temperature: " + maxTemp;
	}

}

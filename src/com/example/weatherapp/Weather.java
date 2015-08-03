package com.example.weatherapp;

import java.io.Serializable;

public class Weather implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String main, temperature, windSpeed, windDegree, downfall, location;
	private long id;
	private String timestamp;

	public Weather() {
		this.location = "";
		this.main = "";
		this.temperature = "";
		this.windSpeed = "";
		this.windDegree = "";
		this.downfall = "";
		this.id = 0;
		timestamp = "";
	}
	
	
	public String getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}



	public String getLocation() {
		return location;
	}


	public void setLocation(String location) {
		this.location = location;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getMain() {
		return main;
	}


	public void setMain(String main) {
		this.main = main;
	}


	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(String windSpeed) {
		this.windSpeed = windSpeed;
	}

	public String getWindDegree() {
		return windDegree;
	}

	public void setWindDegree(String windDegree) {
		this.windDegree = windDegree;
	}

	public String getDownfall() {
		return downfall;
	}

	public void setDownfall(String downfall) {
		this.downfall = downfall;
	}
	
	public String toString(){
		return "Location: " + getLocation() + "\n" + "Weather: " + main + "\n" + "Temperature: " + temperature + "\n" + "Windspeed: " + windSpeed + "\n" + "Winddegree: " + getWindDegree() + "\n" + "Time: " + getTimestamp();
	}
	
	
}

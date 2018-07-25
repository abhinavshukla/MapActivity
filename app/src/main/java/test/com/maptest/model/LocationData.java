package test.com.maptest.model;

import com.google.gson.annotations.SerializedName;


public class LocationData{

	@SerializedName("image")
	private String image;

	@SerializedName("country")
	private String country;

	@SerializedName("companyType")
	private String companyType;

	@SerializedName("latitude")
	private String latitude;

	@SerializedName("userLocation")
	private String userLocation;

	@SerializedName("newJoined")
	private boolean newJoined;

	@SerializedName("addressTwo")
	private String addressTwo;

	@SerializedName("name")
	private String name;

	@SerializedName("id")
	private String id;

	@SerializedName("state")
	private String state;

	@SerializedName("addressOne")
	private String addressOne;

	@SerializedName("category")
	private String category;

	@SerializedName("longitude")
	private String longitude;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setCountry(String country){
		this.country = country;
	}

	public String getCountry(){
		return country;
	}

	public void setCompanyType(String companyType){
		this.companyType = companyType;
	}

	public String getCompanyType(){
		return companyType;
	}

	public void setLatitude(String latitude){
		this.latitude = latitude;
	}

	public String getLatitude(){
		return latitude;
	}

	public void setUserLocation(String userLocation){
		this.userLocation = userLocation;
	}

	public String getUserLocation(){
		return userLocation;
	}

	public void setNewJoined(boolean newJoined){
		this.newJoined = newJoined;
	}

	public boolean isNewJoined(){
		return newJoined;
	}

	public void setAddressTwo(String addressTwo){
		this.addressTwo = addressTwo;
	}

	public String getAddressTwo(){
		return addressTwo;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setState(String state){
		this.state = state;
	}

	public String getState(){
		return state;
	}

	public void setAddressOne(String addressOne){
		this.addressOne = addressOne;
	}

	public String getAddressOne(){
		return addressOne;
	}

	public void setCategory(String category){
		this.category = category;
	}

	public String getCategory(){
		return category;
	}

	public void setLongitude(String longitude){
		this.longitude = longitude;
	}

	public String getLongitude(){
		return longitude;
	}

	@Override
 	public String toString(){
		return 
			"LocationData{" + 
			"image = '" + image + '\'' + 
			",country = '" + country + '\'' + 
			",companyType = '" + companyType + '\'' + 
			",latitude = '" + latitude + '\'' + 
			",userLocation = '" + userLocation + '\'' + 
			",newJoined = '" + newJoined + '\'' + 
			",addressTwo = '" + addressTwo + '\'' + 
			",name = '" + name + '\'' + 
			",id = '" + id + '\'' + 
			",state = '" + state + '\'' + 
			",addressOne = '" + addressOne + '\'' + 
			",category = '" + category + '\'' + 
			",longitude = '" + longitude + '\'' + 
			"}";
		}
}
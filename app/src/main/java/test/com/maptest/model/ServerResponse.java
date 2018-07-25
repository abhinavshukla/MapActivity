package test.com.maptest.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class ServerResponse{

	@SerializedName("locationData")
	private List<LocationDataItem> locationData;

	@SerializedName("error")
	private boolean error;

	@SerializedName("status")
	private String status;

	public void setLocationData(List<LocationDataItem> locationData){
		this.locationData = locationData;
	}

	public List<LocationDataItem> getLocationData(){
		return locationData;
	}

	public void setError(boolean error){
		this.error = error;
	}

	public boolean isError(){
		return error;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"ServerResponse{" + 
			"locationData = '" + locationData + '\'' + 
			",error = '" + error + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}
}
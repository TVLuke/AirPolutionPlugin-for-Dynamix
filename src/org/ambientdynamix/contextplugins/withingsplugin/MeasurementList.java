package org.ambientdynamix.contextplugins.withingsplugin;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class MeasurementList  implements Parcelable 
{
	private List<Measurement> measurements = new ArrayList<Measurement>();

	/**
     * Static Creator factory for Parcelable.
     */
    public static Parcelable.Creator<MeasurementList> CREATOR = new Parcelable.Creator<MeasurementList>() 
    {
		public MeasurementList createFromParcel(Parcel in) 
		{
		    return new MeasurementList(in);
		}
	
		public MeasurementList[] newArray(int size) 
		{
		    return new MeasurementList[size];
		}
    };

    public MeasurementList(Measurement[] x)
    {
    	for(int i=0; i<x.length; i++)
    	{
    		measurements.add(x[i]);
    	}
    }
    
	private MeasurementList(Parcel in) 
	{
		in.readList(measurements, getClass().getClassLoader());
	}
	
	public List getMeasurements()
	{
		return measurements;
	}

	@Override
	public int describeContents() 
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) 
	{
		parcel.writeList(measurements);
	}
}

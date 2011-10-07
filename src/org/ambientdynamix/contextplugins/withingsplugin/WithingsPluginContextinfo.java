/*
 * Copyright (C) the Dynamix Framework Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ambientdynamix.contextplugins.withingsplugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ambientdynamix.application.api.IContextInfo;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

public class WithingsPluginContextinfo implements IContextInfo 
{
    public static Parcelable.Creator<WithingsPluginContextinfo> CREATOR = new Parcelable.Creator<WithingsPluginContextinfo>() 
    {
    	public WithingsPluginContextinfo createFromParcel(Parcel in) 
    	{
    		return new WithingsPluginContextinfo(in);
    	}

    	public WithingsPluginContextinfo[] newArray(int size) 
    	{
    		return new WithingsPluginContextinfo[size];
    	}
    };
    
    // Sample context data
    private MeasurementList contextData;

    public MeasurementList getSampleData() 
    {
    	return contextData;
    }

    @Override
    public String toString() 
    {
    	return this.getClass().getSimpleName();
    };

    @Override
    public String getContextType() 
    {
    	return "org.ambientdynamix.contextplugins.airpolutantsplugin";
    }

    @Override
    public String getStringRepresentation(String format) 
    {
    	
    	String result="";
    	List m = contextData.getMeasurements();
    	for(int i=0; i<m.size(); i++)
    	{
    		if(i>0)
    		{
    			result=result+"; ";
    		}
    		Measurement m1 = (Measurement) m.get(i);
    		result=result+m1.getName()+" "+m1.getValue()+" "+m1.getUnit()+" ("+m1.getDistance()+" meters)";
    	}
    	return result;
    }

    @Override
    public String getImplementingClassname() 
    {
    	return this.getClass().getName();
    }

    @Override
    public Set<String> getStringRepresentationFormats() 
    {
    	Set<String> formats = new HashSet<String>();
    	formats.add("text/plain");
    	return formats;
    };

    public WithingsPluginContextinfo(MeasurementList m) 
    {
    	this.contextData = m;
    }

    private WithingsPluginContextinfo(final Parcel in) 
    {
    	this.contextData = in.readParcelable(getClass().getClassLoader());
    }

    public IBinder asBinder() 
    {
    	return null;
    }

    public int describeContents() 
    {
    	return 0;
    }

    public void writeToParcel(Parcel out, int flags) 
    {
    	out.writeParcelable(this.contextData, flags);
    }
}
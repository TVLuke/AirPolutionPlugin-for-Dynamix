/*
 * Copyright (C) the Dynamix Framework Project (well, whatever)
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;

import org.ambientdynamix.contextplugin.api.ContextPluginSettings;
import org.ambientdynamix.contextplugin.api.PluginPowerScheme;
import org.ambientdynamix.contextplugin.api.PluginState;
import org.ambientdynamix.contextplugin.api.PushPullContextPluginRuntime;
import org.ambientdynamix.contextplugin.api.security.FidelityLevel;
import org.ambientdynamix.contextplugin.api.security.SecuredContextInfo;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Bla
 * 
 * @author Lukas Ruge
 */
public class WithingsPluginRuntime extends PushPullContextPluginRuntime {
    private final String TAG = this.getClass().getSimpleName();
	private boolean okToRun=true;
    private String sampleData;
    private PluginPowerScheme powerScheme;
    public static final String SAMPLE_DATA_KEY = "SAMPLE_DATA_KEY";
    private static final int EVENT_VALID_MILLS = 5000;

    
	@Override
    public void init(PluginPowerScheme scheme, ContextPluginSettings settings) throws Exception 
    {
    	if (getState() == PluginState.NEW)
    	{
    		// Setup new state
    		this.setPowerScheme(scheme);
    		setState(PluginState.INITIALIZED);
    		
    	}
    	else
    	{
    		throw new Exception("Cannot init from state: " + getState());
    	}
		/*
		 * Try to load our settings. Note: init can be called when we're NEW and INITIALIZED (during updates)
		 */
		if (loadSettings(settings)) 
		{
		    // Since we successfully loaded settings, tell Dynamix we're configured.
		    getAndroidFacade().setPluginConfiguredStatus(getSessionId(), true);
		}
		else
		{
		    // Since failed to load our settings, tell Dynamix we're not configured.
		    getAndroidFacade().setPluginConfiguredStatus(getSessionId(), false);
		}
    }

	@Override
    public void start() 
    {
		okToRun=true;
    	if (getState() == PluginState.INITIALIZED) 
    	{
		    setState(PluginState.STARTED);
    	}
    	while(okToRun)
    	{
    		if(!(getState() == PluginState.STARTED))
    		{
    			okToRun=false;
    		}
    		//HERE COMES ALL THE LOGIC
    		try 
    		{
    			Thread.sleep(60000); //one minute
    			if(powerScheme == powerScheme.BALANCED || powerScheme == powerScheme.MANUAL)
    			{
    				Thread.sleep(295000); //five minutes
    			}
    			if(powerScheme == powerScheme.POWER_SAVER)
    			{
    				Thread.sleep(10500000); //three hours
    			}
    		}
    		catch (Exception e) 
    		{
				e.printStackTrace();
			}
    	}
    }

	@Override
    public void handleContextRequest(UUID requestId, String contextDataType) 
    {
		/*
		 * Perform context scan without configuration.
		 */
		if (getState() == PluginState.STARTED) 
		{
		    Log.i(TAG, "handleContextRequest for requestId: " + requestId);
		    pullEventHelper("", requestId, EVENT_VALID_MILLS);
		    
		}
		else
		{
		    Log.w(TAG, "Cannot handleContextRequest from " + getState());
		}
    }

    @Override
    public void handleConfiguredContextRequest(UUID requestId, String contextInfoType, Bundle scanConfig) 
    {
	/*
	 * Use the incoming scanConfig Bundle to control how we perform the context scan.
	 */
	if (getState() == PluginState.STARTED) 
	{
	    Log.i(TAG, "handleConfiguredContextRequest for requestId: " + requestId);
	    pullEventHelper("", requestId, EVENT_VALID_MILLS);
	}
	else
	    Log.w(TAG, "Cannot handleContextRequest from " + getState());
    }

    @Override
    public void stop() 
    {
		okToRun = false;
		Thread t=Thread.currentThread();
		t.interrupt();  
		setState(PluginState.INITIALIZED);
		Log.d(TAG, "Stopped!");
		Log.d("Muhaha", "Stopped!111");
    }

    @Override
    public void destroy() 
    {
		stop();
		setState(PluginState.DESTROYED);
		Log.i(TAG, this + " is Destroyed!");
    }

    @Override
    public void doManualContextScan() 
    {
	    pushEventHelper("", EVENT_VALID_MILLS);
    }

    @Override
    public void updateSettings(ContextPluginSettings settings) 
    {
	if (loadSettings(settings)) {
	    getAndroidFacade().storeContextPluginSettings(getSessionId(), settings);
	    getAndroidFacade().setPluginConfiguredStatus(getSessionId(), true);
	}
    }

    @Override
    public void setPowerScheme(PluginPowerScheme scheme) 
    {
		Log.i(TAG, "Setting PowerScheme " + scheme);
		powerScheme = scheme;
    }

    /*
     * Simple context detection loop that generates push events.
     */
    private void doPushContextDetection() 
    {
		Log.i("Muhaha", "Entering doPushContextDetection");
	    // Send a sample broadcast event
	    pushEventHelper("", EVENT_VALID_MILLS);
	    Log.i("Muhaha", "Exiting doPushContextDetection");
    }

	/*
     * Utility for responding to pull requests.
     */
    private void pullEventHelper(String message, UUID requestId, int validMills) 
    {
    	sendContextEvent(requestId, constructEventList(message), validMills);
    }

    /*
     * Utility for sending push events.
     */
    private void pushEventHelper(String message, int validMills) 
    {
    	sendBroadcastContextEvent(constructEventList(message), validMills);
    }

    /*
     * Utility that constructs a list of SecuredContextInfo containing each different FidelityLevel.
     */
    private List<SecuredContextInfo> constructEventList(String message) 
    {
    	Measurement[] values = new Measurement[5];
    	MeasurementList m = new MeasurementList(values);
		List<SecuredContextInfo> eventList = new Vector<SecuredContextInfo>();
		eventList.add(new SecuredContextInfo(new WithingsPluginContextinfo(m), FidelityLevel.LOW));
		return eventList;
    }

    /*
     * Utility for loading settings.
     */
    private boolean loadSettings(ContextPluginSettings settings) 
    {
	// Check settings type and store
	if (settings != null) 
	{
	    Log.i(TAG, "Received previously stored settings: " + settings);
	    try 
	    {
	    	sampleData = settings.get(SAMPLE_DATA_KEY);
	    	return true;
	    }
	    	catch (Exception e) {
	    		Log.w(TAG, "Failed to parse settings: " + e.getMessage());
	    }
	}
	else
	    if (settings == null) 
	    {
		// Create default settings
		Log.i(TAG, "No settings found!");
	    }
	return false;
    }
}
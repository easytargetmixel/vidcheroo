/*
 * Copyright (C) 2014 Easy Target
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eztarget.vidcheroo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

public class VidcherooConfig {
	
	private static VidcherooConfig instance = null;

	// TODO: Find further variations of this.
	private static final String VLC_DEFAULT_PATH_LIN = "/usr/lib/vlc";
	private static final String VLC_DEFAULT_PATH_OSX = "/Applications/VLC.app/Contents/MacOS/lib";
	private static final String VLC_DEFAULT_PATH_W32 = "C:\\Program Files(x86)\\VLC\\lib\\";
	private static final String VLC_DEFAULT_PATH_W64 = "C:\\Program Files(x86)\\VLC\\lib\\";
	
	private static String mediaPath;
	private static String vlcPath;
	private static float tempo = 120f;
	
	/*
	 * Singleton Constructor Methods
	 */
	
	protected VidcherooConfig() {
		restoreConfigProperties();
		
		// TODO: Read these values from settings or open file picker dialogue.
		if (mediaPath == null) {
			URL url = Launcher.class.getResource("feed/");
			if (url != null) {
				mediaPath = url.getPath();
			} else {
				// TODO: Open picker dialog.
			}
		}
		
		System.out.println("Using feed path: " + mediaPath);
		MediaFileParser.parseMediaPath(mediaPath);
		
		// TODO: Find VLC without help.
		if (vlcPath == null) {
			switch (Engine.getOs()) {
			case LIN:
				vlcPath = VLC_DEFAULT_PATH_LIN;
				break;
			case OSX:
				vlcPath = VLC_DEFAULT_PATH_OSX;
				break;
			case W32:
				vlcPath = VLC_DEFAULT_PATH_W32;
				break;
			case W64:
				vlcPath = VLC_DEFAULT_PATH_W64;
				break;
			default:
				break;
			}
		}
	}

	public static VidcherooConfig getInstance() {
		if (instance == null) {
			instance = new VidcherooConfig();
		}
		return instance;
	}
	
	/*
	 * Public Getter/Setter Methods
	 */
	
	public String getMediaPath() {
		return mediaPath;
	}
	
	public static float getTempo() {
		return tempo;
	}
	
	private static final float MIN_TEMPO = 60.0f;
	private static final float MAX_TEMPO = 180.0f;

	public static void setTempo(String tempoText) {
		float newTempo = 0.0f;
		
		// Attempt to read a float value from the given string.
		try {
			newTempo = Float.parseFloat(tempoText);
		} catch(Exception ex) {
			System.err.println(ex.toString());
		}
		
		// Only replace the tempo if a valid BPM value was given.
		if(newTempo >= MIN_TEMPO && newTempo <= MAX_TEMPO) {
			tempo = newTempo;
		} else {
			Engine.blinkStatusText(MIN_TEMPO + " < Tempo < " + MAX_TEMPO + "!");
		}
		
		Engine.updateTempo();
	}
	
	public static String getVlcPath() {
		return vlcPath;
	}
	
	public static void setVlcPath(String vlcPath) {
		//TODO: Check if this contains the libraries.
		VidcherooConfig.vlcPath = vlcPath;
	}
	
	public static void setMediaPath(String mediaPath) {
		VidcherooConfig.mediaPath = mediaPath;
		MediaFileParser.parseMediaPath(mediaPath);
	}
	
	/*
	 * Properties File
	 */

	//TODO: Test if getProtectionDomain() causes access problems.
	private static final String CLASS_PATH = Launcher.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	
	private static final String CONFIG_PROPERTIES_FILE	= CLASS_PATH + "config.properties";
	private static final String CONFIG_KEY_MEDIA_PATH	= "media_path";
	private static final String CONFIG_KEY_VLC_PATH		= "vlc_path";
	private static final String CONFIG_KEY_TEMPO		= "tempo";

	private static void restoreConfigProperties() {
		System.out.println("Looking for configuration at " + CONFIG_PROPERTIES_FILE + ".");
				
		InputStream input = null;
		 
		try {
			// Open an input stream from the predefined properties file.
			input = new FileInputStream(CONFIG_PROPERTIES_FILE);

			// Load the properties from the input file.
			Properties prop = new Properties();
			prop.load(input);
	 
			// Get the predefined properties values and apply them to the config attributes.
			// VLC libs path:
			String vlcPathProperty = prop.getProperty(CONFIG_KEY_VLC_PATH);
			setVlcPath(vlcPathProperty);
			// Media/feed path:
			String mediaPathProperty = prop.getProperty(CONFIG_KEY_MEDIA_PATH);
			setMediaPath(mediaPathProperty);
			// Last set tempo:
			String tempoProperty = prop.getProperty(CONFIG_KEY_TEMPO);
			setTempo(tempoProperty);
		} catch (FileNotFoundException fileEx) {
			System.err.println("WARNING: Could not find configuration properties file.");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
					System.out.println("Closed " + CONFIG_PROPERTIES_FILE  + ".");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		// TODO: Read these values from settings or open file picker dialogue.
		//setMediaPath("/Users/michel/Projekte/VidcherooOld/feed");
		
		// TODO: Determine OS.
		// TODO: Find VLC without help.
	}
	
	public static void storeConfigProperties() {
		if (mediaPath == null || vlcPath == null) {
			System.err.println("ERROR: Missing attributes to store config properties.");
			return;
		}
				
		OutputStream output = null;
		try {
			output = new FileOutputStream(CONFIG_PROPERTIES_FILE);
	 
			// Set the properties value.
			Properties prop = new Properties();
			prop.setProperty(CONFIG_KEY_MEDIA_PATH, mediaPath);
			prop.setProperty(CONFIG_KEY_VLC_PATH, vlcPath);
			prop.setProperty(CONFIG_KEY_TEMPO, tempo + "");
	 
			// Save properties to project root folder.
			prop.store(output, null);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
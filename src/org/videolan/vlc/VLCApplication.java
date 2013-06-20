/*****************************************************************************
 * VLCApplication.java
 *****************************************************************************
 * Copyright © 2010-2012 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/
package org.videolan.vlc;

import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class VLCApplication extends Application {
    public final static String TAG = "VLC/VLCApplication";
    private static VLCApplication instance;

    public final static String SLEEP_INTENT = "org.videolan.vlc.SleepIntent";

	public static Context getAppContext() {
		return instance;
	}

	public static Resources getAppResources() {
		if(instance == null) return null;
		return instance.getResources();
	}

    @Override
    public void onCreate() {
        super.onCreate();
	    setLocal();
        instance = this;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "System is running low on memory");

        BitmapCache.getInstance().clear();
    }

	private void setLocal() {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String localArea = pref.getString("set_locale", "");
		if (!TextUtils.isEmpty(localArea)) {
			Locale locale;
			// workaround due to region code
			if(localArea.equals("zh-TW")) {
				locale = Locale.TRADITIONAL_CHINESE;
			} else if(localArea.startsWith("zh")) {
				locale = Locale.CHINA;
			} else if(localArea.equals("pt-BR")) {
				locale = new Locale("pt", "BR");
			} else if(localArea.equals("bn-IN") || localArea.startsWith("bn")) {
				locale = new Locale("bn", "IN");
			} else {
				/**
				 * Avoid a crash of
				 * java.lang.AssertionError: couldn't initialize LocaleData for locale
				 * if the user enters nonsensical region codes.
				 */
				if(localArea.contains("-"))
					localArea = localArea.substring(0, localArea.indexOf('-'));
				locale = new Locale(localArea);
			}
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
					getBaseContext().getResources().getDisplayMetrics());
		}
	}
}

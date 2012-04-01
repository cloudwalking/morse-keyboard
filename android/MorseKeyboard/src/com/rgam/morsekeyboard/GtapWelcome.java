/* 
** Copyright 2012 Google Inc. All Rights Reserved.
** 
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
** 
**   http://www.apache.org/licenses/LICENSE-2.0
** 
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/

package com.rgam.morsekeyboard;

import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 
 * @author Reed Morse
 *
 */
public class GtapWelcome extends FragmentActivity {
	protected Button settingsIntentButton;
	protected Button predictiveButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome2);
		
		ViewPager pager = (ViewPager) findViewById(R.id.welcome_pager);
		SimplePagerAdapter adapter = new SimplePagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		
		TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.titles);
		titleIndicator.setViewPager(pager);
		titleIndicator.setTypeface(Typeface.createFromAsset(getAssets(), "typefaces/Roboto-Thin.ttf"));
	}
	
	private class SimplePagerAdapter extends FragmentPagerAdapter implements TitleProvider {
		public SimplePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int viewNum) {
			switch (viewNum) {
				case 0:
					return TryFragment.newInstance();
				case 1:
					return ChartFragment.newInstance();
				case 2:
					return InstallFragment.newInstance();
				case 3:
					return DoubleTapFragment.newInstance();
				case 4:
					return SettingsFragment.newInstance();
				default:
					return null;
			}
		}

		@Override
		public int getCount() {
			return 5;
		}

		public String getTitle(int viewNum) {
			switch (viewNum) {
			case 0:
				return new String("Try").toUpperCase();
			case 1:
				return new String("Learn").toUpperCase();
			case 2:
				return new String("Install").toUpperCase();
			case 3:
				return new String("Expert Mode").toUpperCase();
			case 4:
				return new String("About").toUpperCase();
			default:
				return null;
		}
		}
	}
}

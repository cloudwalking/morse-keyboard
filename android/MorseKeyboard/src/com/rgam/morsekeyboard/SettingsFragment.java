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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author Reed Morse
 *
 */
public class SettingsFragment extends Fragment {
	private Button predictiveButton;
	
	public static SettingsFragment newInstance() {
		return new SettingsFragment();
	}

	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.settings_fragment, container, false);

		predictiveButton = (Button) view.findViewById(R.id.predictiveButton);
		predictiveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				SharedPreferences settings = getActivity().getSharedPreferences(Constants.GTAP_PREFS, 0);
				SharedPreferences.Editor editor = settings.edit();

				boolean predictive = !settings.getBoolean(Constants.GTAP_PREDICTIVE_MODE, false);
				editor.putBoolean(Constants.GTAP_PREDICTIVE_MODE, predictive);
				editor.commit();

				drawUI();
			}
		});
		
		TextView apache = (TextView) view.findViewById(R.id.apache_text);
		apache.setMovementMethod(new ScrollingMovementMethod());

		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
		
		drawUI();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		drawUI();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}
	
	private void drawUI() {
		// Update the predictive button text. This should be changed to a checkbox.
		SharedPreferences settings = getActivity().getSharedPreferences(Constants.GTAP_PREFS, 0);
		boolean predictiveEnabled = settings.getBoolean(Constants.GTAP_PREDICTIVE_MODE, false);
		//predictiveButton.setChecked(predictiveEnabled);
		
		String predictiveButtonText = predictiveEnabled 
				? getString(R.string.predictiveButtonEnabled) 
				: getString(R.string.predictiveButtonDisabled);
		predictiveButton.setText(predictiveButtonText);
		
	}
	
	/*
	public void onTogglePredictive(View v) {
		SharedPreferences settings = getActivity().getSharedPreferences(Constants.GTAP_PREFS, 0);
		SharedPreferences.Editor editor = settings.edit();

		boolean predictive = !settings.getBoolean(Constants.GTAP_PREDICTIVE_MODE, false);
		editor.putBoolean(Constants.GTAP_PREDICTIVE_MODE, predictive);
		editor.commit();

		drawUI();
	}
	*/
}

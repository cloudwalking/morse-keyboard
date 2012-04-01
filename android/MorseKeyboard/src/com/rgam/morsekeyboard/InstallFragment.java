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

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * 
 * @author Reed Morse
 *
 */
public class InstallFragment extends Fragment {
	private Button settingsIntentButton;
	
	public static InstallFragment newInstance() {
		return new InstallFragment();
	}

	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.install_fragment, container, false);
		settingsIntentButton = (Button) v.findViewById(R.id.settingsIntentButton);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedState) {
		super.onActivityCreated(savedState);
		
		settingsIntentButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				showInputSettings();
			}
		});
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}
	
	private void showInputSettings() {
		// ACTION_INPUT_METHOD_SUBTYPE_SETTINGS requires API 11.
		Intent intent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
		getActivity().startActivity(intent);
	}
}

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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * 
 * @author Reed Morse
 *
 */
public class DoubleTapFragment extends Fragment {
	private Button bottomDotButton;
    private Button bottomDashButton;
    private Button bottomSpaceButton;
    private Button bottomDeleteButton;
    private EditText bottomText;
    
	private Button topDotButton;
    private Button topDashButton;
    private Button topSpaceButton;
    private Button topDeleteButton;
    private EditText topText;
    
    private final String poemTop = "I've been meaning to tell you for some time about my strong feelings for you. That time we went to get tacos was a wonderful night that creeps into my memories frequently. April fools! ";
    private final String poemBottom = "A shimmering global phenomenon. Surfing invisible currents of information. Design the soul of an intelligent machine. Do you dream of electric sheep? April fools! ";
    private int bottomPoemLocation = 0;
    private int topPoemLocation = 0;
    private int bottomTapCounter = 0;
    private int topTapCounter = 0;
    private StringBuilder bottomTextBuilder;
    private StringBuilder topTextBuilder;
    
    private SharedPreferences settings;
    
    public enum Keyboard {
    	TOP, BOTTOM
    }
	
	public static DoubleTapFragment newInstance() {
		return new DoubleTapFragment();
	}

	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.double_tap_fragment, container, false);
		
		hookUpViews(v);
        setClickListeners();

        bottomTextBuilder = new StringBuilder();
        topTextBuilder = new StringBuilder();
        
        settings = getActivity().getSharedPreferences(Constants.GTAP_PREFS, 0);
        
		return v;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}
	
	private void hookUpViews(View v) {
    	bottomDotButton = (Button) v.findViewById(R.id.bottom_dot);
    	bottomDashButton = (Button) v.findViewById(R.id.bottom_dash);
    	bottomSpaceButton = (Button) v.findViewById(R.id.bottom_space);
    	bottomDeleteButton = (Button) v.findViewById(R.id.bottom_delete);
    	bottomText = (EditText) v.findViewById(R.id.bottom_text);
    	
    	topDotButton = (Button) v.findViewById(R.id.top_dot);
    	topDashButton = (Button) v.findViewById(R.id.top_dash);
    	topSpaceButton = (Button) v.findViewById(R.id.top_space);
    	topDeleteButton = (Button) v.findViewById(R.id.top_delete);
    	topText = (EditText) v.findViewById(R.id.top_text);
    }
    
    private void setClickListeners() {
    	bottomDotButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				type(Keyboard.BOTTOM, Morse.DOT_CHAR);
			}
		});
    	
    	bottomDashButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				type(Keyboard.BOTTOM, Morse.DASH_CHAR);
			}
		});
    	
    	bottomSpaceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				type(Keyboard.BOTTOM, ' ');
			}
		});
    	
    	topDotButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				type(Keyboard.TOP, Morse.DOT_CHAR);
			}
		});
    	
    	topDashButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				type(Keyboard.TOP, Morse.DASH_CHAR);
			}
		});
    	
    	topSpaceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				type(Keyboard.TOP, ' ');
			}
		});
    	
    	topDeleteButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View view) {
    			deleteCharacter(Keyboard.TOP);
    		}
    	});
    	
    	bottomDeleteButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View view) {
    			deleteCharacter(Keyboard.BOTTOM);
    		}
    	});
    }
    
    private void type(Keyboard k, char c) {
    	int poemLocation = 0;
    	int tapCounter = 0;
    	StringBuilder textBuilder;
    	String poem;
    	
    	if (k == null) {
    		return;
    	}
    	
    	if (k == Keyboard.TOP) {
    		poemLocation = topPoemLocation;
    		tapCounter = topTapCounter;
    		textBuilder = topTextBuilder;
    		poem = poemTop;
    	} else {
    		poemLocation = bottomPoemLocation;
    		tapCounter = bottomTapCounter;
    		textBuilder = bottomTextBuilder;
    		poem = poemBottom;
    	}
    	
    	if (tapCounter++ % 1 == 0) {
			poemLocation = (poemLocation >= poem.length()) ? 0 : poemLocation;
    		textBuilder.append(poem.charAt(poemLocation++));
    		commitTyped(k);
		}
    	
    	if (k == Keyboard.TOP) {
    		topPoemLocation = poemLocation;
    		topTapCounter = tapCounter;
    	} else {
    		bottomPoemLocation = poemLocation;
    		bottomTapCounter = tapCounter;
    	}
    }
    
    private void commitTyped(Keyboard k) {
    	if (k == null) {
    		return;
    	}
    	
    	if (k == Keyboard.TOP) {
    		topText.append(topTextBuilder);
        	topTextBuilder.setLength(0);
    	} else {
    		bottomText.append(bottomTextBuilder);
        	bottomTextBuilder.setLength(0);
    	}
    }
    
    private void deleteCharacter(Keyboard k) {
    	EditText text;
    	int poemLocation;
    	if (k == null){
    		return;
    	} else if (k == Keyboard.TOP) {
    		text = topText;
    		poemLocation = topPoemLocation;
    	} else {
    		text = bottomText;
    		poemLocation = bottomPoemLocation;
    	}
    	
		if (text.getText().toString().length() > 0) {
			String writtenText = text.getText().toString();
			writtenText = writtenText.substring(0, writtenText.length() - 1);
			text.setText(writtenText);
			if (poemLocation > 0) {
				poemLocation--;
			}
		}
		
		if (k == Keyboard.TOP) {
			topPoemLocation = poemLocation;
		} else {
			bottomPoemLocation = poemLocation;
		}
	}
}

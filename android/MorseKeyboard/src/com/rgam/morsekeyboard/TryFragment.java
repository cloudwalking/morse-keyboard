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
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author Reed Morse
 * 
 */
public class TryFragment extends Fragment {
	
	private LinearLayout Keyboard;
	private Button dotButton;
    private Button dashButton;
    private Button doneButton;
    private Button spaceButton;
    private Button deleteButton;
    private EditText text;
    private TextView candidateView;
    
    private final String poem = "A shimmering global phenomenon. Surfing invisible currents of information. Design the soul of an intelligent machine. Do you dream of electric sheep? April fools! ";
    //private final String poem = "I am standing here doing an interview at the Academy of Country Music Awards. It is really exciting that I can email you at the same time. ";
    
    private int poemLocation = 0;
    private int tapCounter = 0;
    private StringBuilder textBuilder;
    
    private SharedPreferences settings;
    
    private Handler morseHandler;
    private final int DOT_LENGTH_MS = 300;
    private final int LETTER_MULTIPLIER = 3;
    
	public static TryFragment newInstance() {
		return new TryFragment();
	}

	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.try_fragment, container, false);
		
		hookUpViews(v);
		setOnClickListeners();
		textBuilder = new StringBuilder();
		settings = getActivity().getSharedPreferences(Constants.GTAP_PREFS, 0);
		
		return v;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}
	
	private void hookUpViews(View v) {
		dotButton = (Button) v.findViewById(R.id.dot);
    	dashButton = (Button) v.findViewById(R.id.dash);
    	spaceButton = (Button) v.findViewById(R.id.space);
    	deleteButton = (Button) v.findViewById(R.id.delete);
    	text = (EditText) v.findViewById(R.id.text);
    	candidateView = (TextView) v.findViewById(R.id.candidate_view);
	}
	
	private void setOnClickListeners() {
		dotButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				typeChar(Morse.DOT_CHAR);
			}
		});
    	
    	dashButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				typeChar(Morse.DASH_CHAR);
			}
		});
    	
    	spaceButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				typeChar(' ');
			}
		});
    	
    	deleteButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View view) {
    			deleteCharacter();
    		}
    	});
	}
	
	private void typeChar(char c) {
		if(!settings.getBoolean(Constants.GTAP_PREDICTIVE_MODE, false)) {
			// Space typed, commit what we have.
			if (c == ' ') {
				invalidateLetterTimer();
				if (textBuilder.length() == 0) {
					// If the pending string is empty, the user wants a real space added.
					textBuilder.append(c);
				}
				
				commitTyped();
			} else {
				textBuilder.append(c);
				updateCandidate();
				beginLetterTimer();
			}
		} else {
			/* Use mod so we can slow down typing (more realistic). */
	    	if (tapCounter++ % 1 == 0) {
	    		poemLocation = (poemLocation >= poem.length()) ? 0 : poemLocation;
	    		textBuilder.append(poem.charAt(poemLocation++));
	    		commitTyped();
			}
			
			/*
	    	// For LL Cool J each tap is worth three letters.
	    	poemLocation = (poemLocation >= poem.length()) ? 0 : poemLocation;
	    	int offset = (poem.length() - poemLocation < 3) ? poem.length() - poemLocation : 3;
	    	textBuilder.append(poem.substring(poemLocation, poemLocation + offset));
	    	poemLocation += 3;
	    	commitTyped();
	    	*/
		}
    }
	
	private void deleteCharacter() {
		if (textBuilder.length() > 0) {
			int newLength = textBuilder.length() - 1;
			textBuilder.setLength(newLength);
			updateCandidate();
		} else if (text.getText().toString().length() > 0) {
			String writtenText = text.getText().toString();
			writtenText = writtenText.substring(0, writtenText.length() - 1);
			text.setText(writtenText);
			if (poemLocation > 0) {
				poemLocation--;
			}
		}
	}
	
	private void updateCandidate() {
		candidateView.setText(textBuilder);
	}
	
	private void commitTyped() {
		if (!settings.getBoolean(Constants.GTAP_PREDICTIVE_MODE, false) && !textBuilder.toString().equals(" ")) {
			Character morseCharacter = Morse.characterFromCode(textBuilder.toString());
			if (morseCharacter != null) {
				String morseString = String.valueOf(morseCharacter.charValue());
				textBuilder = new StringBuilder(morseString);
	    	} else {
	    		textBuilder.setLength(0);
	    	}
		}
		
    	text.append(textBuilder);
    	textBuilder.setLength(0);
    	updateCandidate();
    }
	
	private void beginLetterTimer() {
    	invalidateLetterTimer();

    	int interval = DOT_LENGTH_MS * LETTER_MULTIPLIER;
    	
    	morseHandler = new Handler();
    	morseHandler.postDelayed(insertLetterTask, interval);
    }

    private void invalidateLetterTimer() {
    	if (morseHandler != null) {
    		morseHandler.removeCallbacks(insertLetterTask);
    	}
    }

    private Runnable insertLetterTask = new Runnable() {
    	public void run() {
    		if (textBuilder.length() > 0) {
    			commitTyped();
    		}
    	}
    };
}

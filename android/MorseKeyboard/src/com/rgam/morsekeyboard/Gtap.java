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
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Handler;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * This code is adapted from the Android Soft Keyboard example.
 * @author Reed Morse
 *
 */
public class Gtap extends InputMethodService 
        implements KeyboardView.OnKeyboardActionListener {
    static final boolean DEBUG = false;

    /**
     * This boolean indicates the optional example code for performing
     * processing of hard keys in addition to regular text generation
     * from on-screen interaction.  It would be used for input methods that
     * perform language translations (such as converting text entered on 
     * a QWERTY keyboard to Chinese), but may not be used for input methods
     * that are primarily intended to be used for on-screen text entry.
     */
    static final boolean PROCESS_HARD_KEYS = true;

    private KeyboardView mInputView;
    private CandidateView mCandidateView;
    private CompletionInfo[] mCompletions;

    private StringBuilder mComposing = new StringBuilder();
    private boolean mPredictionOn;
    private boolean mCompletionOn;
    private int mLastDisplayWidth;
    private boolean mCapsLock;
    private long mLastShiftTime;
    private long mMetaState;

    private LatinKeyboard mSymbolsKeyboard;
    private LatinKeyboard mMorseKeyboard;

    private LatinKeyboard mCurKeyboard;

    private String mWordSeparators;
    
    // This is used to keep track of location in Predictive Mode.
    private int poemLocation;
    private boolean predictiveEnabled;
    private SharedPreferences settings;
    private Handler morseHandler;

    // Constants.
    private final int KEYCODE_SPACE = 32;
    private final int DOT_LENGTH_MS = 300;
    private final int DASH_MULTIPLIER = 3;
    private final int LETTER_MULTIPLIER = 3;
    private final int WORD_MULTIPLIER = 7;

    private final int KEYCODE_LETTER_SEPARATOR = -77;
    private final int KEYCODE_WORD_SEPARATOR = -78;
    
    // This is typed in Predictive Mode.
    //private final String poem = "A shimmering global phenomenon. Surfing invisible currents of information. Design the soul of an intelligent machine. Do androids dream of electric sheep?";
    private final String poem = "I've been meaning to tell you for some time about my strong feelings for you. That time we went to get tacos was a wonderful night that creeps into my memories frequently. April fools! ";
    //private final String poem = "I am standing here doing an interview at the Academy of Country Music Awards. It is really exciting that I can email you at the same time. ";
    
    /** Main initialization of the input method component. */
    @Override public void onCreate() {
        super.onCreate();
        mWordSeparators = getResources().getString(R.string.word_separators);
        poemLocation = 0;
        settings = getSharedPreferences(Constants.GTAP_PREFS, 0);
    }

    /**
     * UI initialization, called after creation and configuration changes.
     */
    @Override public void onInitializeInterface() {
        if (mMorseKeyboard != null) {
            // Configuration changes can happen after the keyboard gets recreated,
            // so we need to be able to re-build the keyboards if the available
            // space has changed.
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        mMorseKeyboard = new LatinKeyboard(this, R.xml.qwerty);
    }

    /**
     * Called by the framework when your view for creating input needs to
     * be generated.  This will be called the first time your input method
     * is displayed, and every time it needs to be re-created such as due to
     * a configuration change.
     */
    @Override public View onCreateInputView() {
        mInputView = (KeyboardView) getLayoutInflater().inflate(
                R.layout.input, null);
        mInputView.setOnKeyboardActionListener(this);
        mInputView.setKeyboard(mMorseKeyboard);
        return mInputView;
    }

    /**
     * Called by the framework when your view for showing candidates needs to
     * be generated, like {@link #onCreateInputView}.
     */
    @Override public View onCreateCandidatesView() {
        mCandidateView = new CandidateView(this);
        mCandidateView.setService(this);
        return mCandidateView;
    }

    /**
     * This is the main point where we do our initialization of the input method
     * to begin operating on an application.  At this point we have been
     * bound to the client, and are now receiving all of the detailed information
     * about the target of our edits.
     */
    @Override public void onStartInput(EditorInfo attribute, boolean restarting) {
    	Log.v("rgam", "onStartInput()");
        super.onStartInput(attribute, restarting);

        // predictiveEnabled = settings.getBoolean(Constants.GTAP_PREDICTIVE_MODE, false);

        // Reset our state.  We want to do this even if restarting, because
        // the underlying state of the text editor could have changed in any way.
        mComposing.setLength(0);
        updateCandidates();

        if (!restarting) {
            // Clear shift states.
            mMetaState = 0;
        }

        mPredictionOn = false;
        mCompletionOn = false;
        mCompletions = null;

        // We are now going to initialize our state based on the type of
        // text being edited.
        switch (attribute.inputType&EditorInfo.TYPE_MASK_CLASS) {
        	// Normally we would switch to a different keyboard,
        	// but everything is going to be the Morse keyboard.
            case EditorInfo.TYPE_CLASS_NUMBER:
            case EditorInfo.TYPE_CLASS_DATETIME:
            case EditorInfo.TYPE_CLASS_PHONE:
            case EditorInfo.TYPE_CLASS_TEXT:
                // This is general text editing.  We will default to the
                // normal alphabetic keyboard, and assume that we should
                // be doing predictive text (showing candidates as the
                // user types).
                mCurKeyboard = mMorseKeyboard;
                mPredictionOn = true;

                // We now look for a few special variations of text that will
                // modify our behavior.
                int variation = attribute.inputType &  EditorInfo.TYPE_MASK_VARIATION;
                if (variation == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
                        variation == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Do not display predictions / what the user is typing
                    // when they are entering a password.
                    mPredictionOn = false;
                }

                if (variation == EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS 
                        || variation == EditorInfo.TYPE_TEXT_VARIATION_URI
                        || variation == EditorInfo.TYPE_TEXT_VARIATION_FILTER) {
                    // Our predictions are not useful for e-mail addresses
                    // or URIs.
                    mPredictionOn = false;
                }

                if ((attribute.inputType&EditorInfo.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    // If this is an auto-complete text view, then our predictions
                    // will not be shown and instead we will allow the editor
                    // to supply their own.  We only show the editor's
                    // candidates when in fullscreen mode, otherwise relying
                    // own it displaying its own UI.
                    mPredictionOn = false;
                    mCompletionOn = isFullscreenMode();
                }

                break;

            default:
                // For all unknown input types, default to the alphabetic
                // keyboard with no special features.
                mCurKeyboard = mMorseKeyboard;
                updateShiftKeyState(attribute);
        }

        // Update the label on the enter key, depending on what the application
        // says it will do.
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override public void onFinishInput() {
    	Log.v("rgam", "onFinishInput()");
        super.onFinishInput();

        // Clear current composing text and candidates.
        mComposing.setLength(0);
        updateCandidates();

        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);

        mCurKeyboard = mMorseKeyboard;
        if (mInputView != null) {
            mInputView.closing();
        }
    }

    @Override public void onStartInputView(EditorInfo attribute, boolean restarting) {
    	Log.v("rgam", "onStartInputView()");
        super.onStartInputView(attribute, restarting);
        // Apply the selected keyboard to the input view.
        mInputView.setKeyboard(mCurKeyboard);
        mInputView.closing();
    }

    /**
     * Deal with the editor reporting movement of its cursor.
     */
    @Override public void onUpdateSelection(int oldSelStart, int oldSelEnd,
            int newSelStart, int newSelEnd,
            int candidatesStart, int candidatesEnd) {
    	Log.v("rgam", "onUpdateSelection()");
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);

        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);
            updateCandidates();
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }
        }
    }

    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourself, since the editor can not be seen
     * in that situation.
     */
    @Override public void onDisplayCompletions(CompletionInfo[] completions) {
    	Log.v("rgam", "onDisplayCompletions()");
        if (mCompletionOn) {
            mCompletions = completions;
            if (completions == null) {
                setSuggestions(null, false, false);
                return;
            }

            List<String> stringList = new ArrayList<String>();
            for (int i=0; i<(completions != null ? completions.length : 0); i++) {
                CompletionInfo ci = completions[i];
                if (ci != null) stringList.add(ci.getText().toString());
            }
            setSuggestions(stringList, true, true);
        }
    }
    
    public void predictiveModeChanged() {
    	Log.v("rgam", "PREDICTIVE MODE CHANGED!!");
    }

    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
    	Log.v("rgam", "translateKeyDown()");
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
                keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }

        boolean dead = false;

        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            dead = true;
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }

        if (mComposing.length() > 0) {
            char accent = mComposing.charAt(mComposing.length() -1 );
            int composed = KeyEvent.getDeadChar(accent, c);

            if (composed != 0) {
                c = composed;
                mComposing.setLength(mComposing.length()-1);
            }
        }

        onKey(c, null);

        return true;
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    	Log.v("rgam", "onKeyDown()");
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // The InputMethodService already takes care of the back
                // key for us, to dismiss the input method if it is shown.
                // However, our keyboard could be showing a pop-up window
                // that back should dismiss, so we first allow it to do that.
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack()) {
                        return true;
                    }
                }
                break;
                
            case KeyEvent.KEYCODE_DEL:
                // Special handling of the delete key: if we currently are
                // composing text for the user, we want to modify that instead
                // of let the application to the delete itself.
                if (mComposing.length() > 0) {
                    onKey(Keyboard.KEYCODE_DELETE, null);
                    return true;
                }
                break;
                
            case KeyEvent.KEYCODE_ENTER:
                // Let the underlying text editor always handle these.
                return false;
                
            default:
                // For all other keys, if we want to do transformations on
                // text being entered with a hard keyboard, we need to process
                // it and do the appropriate action.
                if (PROCESS_HARD_KEYS) {
                    if (mPredictionOn && translateKeyDown(keyCode, event)) {
                        return true;
                    }
                }
        }
        
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Use this to monitor key events being delivered to the application.
     * We get first crack at them, and can either resume them or let them
     * continue to the app.
     */
    @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
    	Log.v("rgam", "onKeyUp()");
        // If we want to do transformations on text being entered with a hard
        // keyboard, we need to process the up events to update the meta key
        // state we are tracking.
        if (PROCESS_HARD_KEYS) {
            if (mPredictionOn) {
                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState,
                        keyCode, event);
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Helper function to commit any text being composed in to the editor.
     */
    private void commitTyped(InputConnection inputConnection) {
    	if (mComposing.length() > 0) {
    		if (predictiveEnabled) {
    			inputConnection.commitText(mComposing, mComposing.length());
    		} else {
    			Character morseCharacter = Morse.characterFromCode(mComposing.toString());
    			if (morseCharacter != null) {
    				String morseString = String.valueOf(morseCharacter.charValue());
	        		inputConnection.commitText(morseString, morseString.length());
	        	} else {
	        		// Do nothing, it's not a valid combo.
	        		//inputConnection.commitText(mComposing, mComposing.length());        		
	        	}
        	}
    		mComposing.setLength(0);
            updateCandidates();
    	}
    }
    
    private void commitTyped(InputConnection inputConnection, String string) {
    	if (predictiveEnabled) {
			inputConnection.commitText(mComposing, mComposing.length());
		} else {
			inputConnection.commitText(string, string.length());
		}
    	updateCandidates();
    }

    /**
     * Helper to update the shift state of our keyboard based on the initial
     * editor state.
     */
    private void updateShiftKeyState(EditorInfo attr) {
    	Log.v("rgam", "updateShiftKeyState()");
        if (attr != null 
                && mInputView != null && mMorseKeyboard == mInputView.getKeyboard()) {
            int caps = 0;
            EditorInfo ei = getCurrentInputEditorInfo();
            if (ei != null && ei.inputType != EditorInfo.TYPE_NULL) {
                caps = getCurrentInputConnection().getCursorCapsMode(attr.inputType);
            }
            mInputView.setShifted(mCapsLock || caps != 0);
        }
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
    	Log.v("rgam", "keyDownUp()");
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    // Implementation of KeyboardViewListener
    public void onKey(int primaryCode, int[] keyCodes) {
    	invalidateLetterAndWordTimers();
    	predictiveEnabled = settings.getBoolean(Constants.GTAP_PREDICTIVE_MODE, false);
    	
    	if (primaryCode == KEYCODE_SPACE) {
        	Log.v("rgam", ">>> space key hit");
            if (mComposing.length() > 0) {
                commitTyped(getCurrentInputConnection());
            } else {
            	// Nothing typed, commit a space.
            	commitTyped(getCurrentInputConnection(), " ");
            }
    	} else if (primaryCode == Keyboard.KEYCODE_DELETE) {
            handleBackspace();
        } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
            handleClose();
            return;
        } else if (primaryCode == LatinKeyboardView.KEYCODE_OPTIONS) {
            // Show a menu or something.
        } else {
            handleCharacter(primaryCode, keyCodes);
        }
    }

    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        if (mComposing.length() > 0) {
            commitTyped(ic);
        }
        ic.commitText(text, 0);
        ic.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    private void updateCandidates() {
        if (!mCompletionOn) {
            if (mComposing.length() > 0) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(mComposing.toString());
                setSuggestions(list, true, true);
            } else {
                setSuggestions(null, false, false);
            }
        }
    }

    public void setSuggestions(List<String> suggestions, boolean completions,
            boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        if (mCandidateView != null) {
            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }

    private void handleBackspace() {
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateCandidates();
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            updateCandidates();
        } else {
            keyDownUp(KeyEvent.KEYCODE_DEL);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private int tapCounter = 0;
    private void handleCharacter(int primaryCode, int[] keyCodes) {
    	Log.v("rgam", "handleCharacter()");
    	
    	if (predictiveEnabled) {
    		/*
    		// Each tap is one word
    		String[] poemFragment = poem.split(" ");
    		poemLocation = (poemLocation >= poemFragment.length - 1) ? 0 : poemLocation;
    		mComposing.append(poemFragment[poemLocation++] + " ");
    		commitTyped(getCurrentInputConnection());
    		*/
    		
    		// One tap is one character
    		if (tapCounter++ % 1 == 0) {
    			poemLocation = (poemLocation >= poem.length()) ? 0 : poemLocation;
        		mComposing.append(poem.charAt(poemLocation++));
        		commitTyped(getCurrentInputConnection());
    		}
    		
    		/*
    		// For LL Cool J each tap is worth three letters.
    		poemLocation = (poemLocation < 0) ? 0 : poemLocation;
	    	poemLocation = (poemLocation >= poem.length()) ? 0 : poemLocation;
	    	int offset = (poem.length() - poemLocation < 3) ? poem.length() - poemLocation : 3;
	    	mComposing.append(poem.substring(poemLocation, poemLocation + offset));
	    	poemLocation += 3;
	    	commitTyped(getCurrentInputConnection());
	    	*/
    	} else {
    		mComposing.append((char) primaryCode); 
    		updateCandidates();
    	}
    }

    private void handleClose() {
    	Log.v("rgam", "handleClose()");
        commitTyped(getCurrentInputConnection());
        requestHideSelf(0);
        mInputView.closing();
    }

    private String getWordSeparators() {
        return mWordSeparators;
    }

    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char)code));
    }

    public void pickDefaultCandidate() {
        pickSuggestionManually(0);
    }

    public void pickSuggestionManually(int index) {
        if (mCompletionOn && mCompletions != null && index >= 0
                && index < mCompletions.length) {
            CompletionInfo ci = mCompletions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (mCandidateView != null) {
                mCandidateView.clear();
            }
            updateShiftKeyState(getCurrentInputEditorInfo());
        } else if (mComposing.length() > 0) {
            // If we were generating candidate suggestions for the current
            // text, we would commit one of them here.  But for this sample,
            // we will just commit the current text.
            commitTyped(getCurrentInputConnection());
        }
    }

    public void swipeRight() {
        if (mCompletionOn) {
            pickDefaultCandidate();
        }
    }

    public void swipeLeft() {
        handleBackspace();
    }

    public void swipeDown() {
        handleClose();
    }

    public void swipeUp() {
    }

    public void onPress(int primaryCode) {
    }

    public void onRelease(int primaryCode) {
    }

    private void commitWordSeparator() {
    	mComposing.setLength(0);
		commitTyped(getCurrentInputConnection(), " ");
    }

    private void beginLetterAndWordTimers() {
    	
    	morseHandler = new Handler();

    	int interval = predictiveEnabled ? 0 : DOT_LENGTH_MS * LETTER_MULTIPLIER;
    	morseHandler.postDelayed(insertLetterTask, interval);
    	
    	if (!predictiveEnabled) {
    		interval = DOT_LENGTH_MS * WORD_MULTIPLIER;
    		morseHandler.postDelayed(insertSeparatorTask, interval);
    	}
    }

    private void invalidateLetterAndWordTimers() {
    	if (morseHandler != null) {
    		morseHandler.removeCallbacks(insertLetterTask);
    		morseHandler.removeCallbacks(insertSeparatorTask);
    	}
    }

    private Runnable insertLetterTask = new Runnable() {
    	public void run() {
    		if (mComposing.length() > 0) {
    			commitTyped(getCurrentInputConnection());
    		}
    	}
    };

    private Runnable insertSeparatorTask = new Runnable() {
    	public void run() {
    		commitWordSeparator();
    	}
    };
}

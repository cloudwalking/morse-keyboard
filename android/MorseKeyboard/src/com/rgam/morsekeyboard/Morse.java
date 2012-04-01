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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 
 * @author Reed Morse
 *
 */
public class Morse {
	private static final HashMap<Integer, Character> translationTable;

	// For translating Strings.
	public static final char DOT_CHAR = '.';
	public static final char DASH_CHAR = '-';

	public enum Code {
		DOT, DASH
	}

	static {
		translationTable = new HashMap<Integer, Character>();

		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DASH }), 'A');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DOT,  Code.DOT,  Code.DOT }), 'B');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DOT,  Code.DASH,  Code.DOT }), 'C');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DOT,  Code.DOT }),   'D');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT }), 'E');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DOT,  Code.DASH,  Code.DOT }), 'F');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DASH,  Code.DOT }), 'G');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DOT,  Code.DOT,  Code.DOT }), 'H');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DOT }), 'I');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DASH,  Code.DASH,  Code.DASH }), 'J');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DOT,  Code.DASH }), 'K');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DASH,  Code.DOT,  Code.DOT }), 'L');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DASH }), 'M');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DOT }), 'N');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DASH,  Code.DASH }), 'O');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DASH,  Code.DASH,  Code.DOT }), 'P');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DASH,  Code.DOT,  Code.DASH }), 'Q');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DASH,  Code.DOT }), 'R');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DOT,  Code.DOT }), 'S');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH }), 'T');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DOT,  Code.DASH }), 'U');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DOT,  Code.DOT,  Code.DASH }), 'V');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DASH,  Code.DASH }),   'W');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DOT,  Code.DOT,  Code.DASH }), 'X');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DOT,  Code.DASH,  Code.DASH }), 'Y');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DASH,  Code.DASH,  Code.DASH,  Code.DASH }), '1');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DOT,  Code.DASH,  Code.DASH,  Code.DASH }), '2');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DOT,  Code.DOT,  Code.DASH,  Code.DASH }), '3');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DOT,  Code.DOT,  Code.DOT,  Code.DASH }), '4');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DOT,  Code.DOT,  Code.DOT,  Code.DOT,  Code.DOT }), '5');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DOT,  Code.DOT,  Code.DOT,  Code.DOT }), '6');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DASH,  Code.DOT,  Code.DOT,  Code.DOT }), '7');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DASH,  Code.DASH,  Code.DOT,  Code.DOT }), '8');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DASH,  Code.DASH,  Code.DASH,  Code.DOT }), '9');
		translationTable.put(Arrays.hashCode(new Code[]{ Code.DASH,  Code.DASH,  Code.DASH,  Code.DASH,  Code.DASH }), '0');
	}
	
	/**
	 * Converts a string of Morse code (periods and dashes ) into a Character.
	 * @param codes a String of periods (.) and dashes (-).
	 * @return the character corresponding to the given Morse code.
	 */
	public static Character characterFromCode(String codes) {
		ArrayList<Code> codeArray = new ArrayList<Code>();
		for(char ch : codes.toCharArray()) {
			Code code = (ch == DOT_CHAR) ? Code.DOT : Code.DASH;
			codeArray.add(code);
		}
		
		return characterFromCode(codeArray);
	}
	
	/**
	 * Converts an array of Codes into a Character.
	 * @param codes an array of Codes.
	 * @return the character corresponding to the given Morse code.
	 */
	public static Character characterFromCode(Code[] codes) {
		return translationTable.get(Arrays.hashCode(codes)); 
	}
	
	/**
	 * Converts an ArrayList of Codes into a Character.
	 * @param codes an ArrayList of Codes.
	 * @return the character corresponding to the given Morse code.
	 */
	public static Character characterFromCode(ArrayList<Code> codes) {
		Code[] array = new Code[codes.size()];
		array = codes.toArray(array);
		return characterFromCode(array);
	}
}

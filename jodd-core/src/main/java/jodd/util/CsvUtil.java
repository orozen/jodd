// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Helps with CSV strings.
 * See: http://en.wikipedia.org/wiki/Comma-separated_values
 */
public class CsvUtil {

	protected static final char FIELD_SEPARATOR = ',';
	protected static final char FIELD_QUOTE = '"';
	protected static final String DOUBLE_QUOTE = "\"\"";
	protected static final String SPECIAL_CHARS = "\r\n";

	/**
	 * Parse fields as csv string,
	 */
	public static String toCsvString(final Object... elements) {
		StringBuilder line = new StringBuilder();
		int last = elements.length -1;
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] == null) {
				if (i != last) {
					line.append(FIELD_SEPARATOR);
				}
				continue;
			}
			String field = elements[i].toString();

			// check for special cases
			int ndx = field.indexOf(FIELD_SEPARATOR);
			if (ndx == -1) {
				ndx = field.indexOf(FIELD_QUOTE);
			}
			if (ndx == -1) {
				if (field.startsWith(StringPool.SPACE) || field.endsWith(StringPool.SPACE)) {
					ndx = 1;
				}
			}
			if (ndx == -1) {
				ndx = StringUtil.indexOfChars(field, SPECIAL_CHARS);
			}

			// add field
			if (ndx != -1) {
				line.append(FIELD_QUOTE);
			}
			field = StringUtil.replace(field, StringPool.QUOTE, DOUBLE_QUOTE);
			line.append(field);
			if (ndx != -1) {
				line.append(FIELD_QUOTE);
			}

			// last
			if (i != last) {
				line.append(FIELD_SEPARATOR);
			}
		}
		return line.toString();
	}


	/**
	 * Converts CSV line to string array.
	 */
	@SuppressWarnings("ConstantConditions")

	public static String[] toStringArray(final String line) {
		List<String> row = new ArrayList<>();

        boolean inQuotedField = false;
        int fieldStart = 0;

        final int len = line.length();
        for (int i = 0; i < len; i++) {
			char c = line.charAt(i);
			addFieldToStringArray(line, row, inQuotedField, fieldStart, len, i, c);

			// After = slide(5) + slide(10) + slide(11) + slide(12) + slide(14) + slide(15)
			// = {2,3,5,6,7,8,10,11,12,13,14,15}
			boolean inQuotedField_1 = inQuotedField, inQuotedField_2, inQuotedField_3,
				inQuotedField_4, inQuotedField_5, inQuotedField_6, inQuotedField_7, inQuotedField_8;
			int i_1 = i, i_2, i_3, i_4, i_5, i_6;
			int fieldStart_1 = fieldStart, fieldStart_2, fieldStart_3, fieldStart_4, fieldStart_5,
				fieldStart_6, fieldStart_7, fieldStart_8, fieldStart_9, fieldStart_10;
/*2*/		if (c == FIELD_SEPARATOR) {
/*3*/			if (!inQuotedField_1) {	// ignore we are quoting
/*5*/				fieldStart_2 = i_1 + 1;
					fieldStart_3 = fieldStart_2;
                } else {
					fieldStart_3 = fieldStart_1;
				}
				fieldStart_10 = fieldStart_3;
				i_6 = i_1;
				inQuotedField_8 = inQuotedField_1;
            } else {
/*6*/			if (c == FIELD_QUOTE) {
/*7*/				if (inQuotedField_1) {
/*8*/					if (i_1 + 1 == len || line.charAt(i_1 + 1) == FIELD_SEPARATOR) {    // we are already quoting - peek to see if this is the end of the field
/*10*/						fieldStart_4 = i_1 + 2;
							fieldStart_5 = fieldStart_4;
/*11*/						i_2 = i_1 + 1; // and skip the comma
							i_3 = i_2;
/*12*/						inQuotedField_2 = false;
							inQuotedField_3 = inQuotedField_2;
						} else {
							fieldStart_5 = fieldStart_1;
							i_3 = i_1;
							inQuotedField_3 = inQuotedField_1;
						}
						fieldStart_8 = fieldStart_5;
						i_4 = i_3;
						inQuotedField_6 = inQuotedField_3;
					} else {
/*13*/					if (fieldStart_1 == i_1) {
/*14*/						inQuotedField_4 = true;    // this is a beginning of a quote
							inQuotedField_5 = inQuotedField_4;
/*15*/						fieldStart_6 = fieldStart_1 + 1;            // move field start
							fieldStart_7 = fieldStart_6;
						} else {
							inQuotedField_5 = inQuotedField_1;
							fieldStart_7 = fieldStart_1;
						}
						fieldStart_8 = fieldStart_7;
						i_4 = i_1;
						inQuotedField_6 = inQuotedField_5;
					}
					fieldStart_9 = fieldStart_8;
					i_5 = i_4;
					inQuotedField_7 = inQuotedField_6;
				} else {
					fieldStart_9 = fieldStart_1;
					i_5 = i_1;
					inQuotedField_7 = inQuotedField_1;
				}
				fieldStart_10 = fieldStart_9;
				i_6 = i_5;
				inQuotedField_8 = inQuotedField_7;
			}
			fieldStart = fieldStart_10;
			i = i_6;
			inQuotedField = inQuotedField_8;
        }
        // add last field - but only if string was not empty
        if (len > 0 && fieldStart <= len) {
            addField(row, line, fieldStart, len, inQuotedField);
        }
        return row.toArray(new String[0]);
	}

	// Extracted Marked bucket from toStringArray
	private static void addFieldToStringArray(String line, List<String> row, boolean inQuotedField, int fieldStart, int len, int i, char c) {
/*2*/	if (c == FIELD_SEPARATOR) {
/*3*/		if (!inQuotedField) {	// ignore we are quoting
/*4*/			addField(row, line, fieldStart, i, inQuotedField);
			}
		} else {
/*6*/		if (c == FIELD_QUOTE) {
/*7*/			if (inQuotedField) {
/*8*/				if (i + 1 == len || line.charAt(i + 1) == FIELD_SEPARATOR) {    // we are already quoting - peek to see if this is the end of the field
/*9*/					addField(row, line, fieldStart, i, inQuotedField);
					}
				}
			}
		}
	}

	private static void addField(final List<String> row, final String line, final int startIndex, final int endIndex, final boolean inQuoted) {
        String field = line.substring(startIndex, endIndex);
		if (inQuoted) {
			field = StringUtil.replace(field, DOUBLE_QUOTE, "\"");
		}
        row.add(field);
    }

}

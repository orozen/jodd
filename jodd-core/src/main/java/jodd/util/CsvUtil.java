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
        	// Before = slide(1) = {1}
/*1*/		char c = line.charAt(i);

			// Marked = slide(4) + slide(9) = {2,3,4,6,7,8,9}
/*2*/		if (c == FIELD_SEPARATOR) {
/*3*/			if (!inQuotedField) {	// ignore we are quoting
/*4*/				addField(row, line, fieldStart, i, inQuotedField);
                }
            } else {
/*6*/			if (c == FIELD_QUOTE) {
/*7*/				if (inQuotedField) {
/*8*/					if (i + 1 == len || line.charAt(i + 1) == FIELD_SEPARATOR) {    // we are already quoting - peek to see if this is the end of the field
/*9*/						addField(row, line, fieldStart, i, inQuotedField);
						}
					} else {
					}
				}
			}

			// After = slide(5) + slide(10) + slide(11) + slide(12) + slide(14) + slide(15)
			// = {2,3,5,6,7,8,10,11,12,13,14,15}
/*2*/		if (c == FIELD_SEPARATOR) {
/*3*/			if (!inQuotedField) {	// ignore we are quoting
/*5*/				fieldStart = i + 1;
                }
            } else {
/*6*/			if (c == FIELD_QUOTE) {
/*7*/				if (inQuotedField) {
/*8*/					if (i + 1 == len || line.charAt(i + 1) == FIELD_SEPARATOR) {    // we are already quoting - peek to see if this is the end of the field
/*10*/						fieldStart = i + 2;
/*11*/						i++; // and skip the comma
/*12*/						inQuotedField = false;
						}
					} else {
/*13*/					if (fieldStart == i) {
/*14*/						inQuotedField = true;    // this is a beginning of a quote
/*15*/						fieldStart++;            // move field start
						}
					}
				}
			}
        }
        // add last field - but only if string was not empty
        if (len > 0 && fieldStart <= len) {
            addField(row, line, fieldStart, len, inQuotedField);
        }
        return row.toArray(new String[0]);
	}

	private static void addField(final List<String> row, final String line, final int startIndex, final int endIndex, final boolean inQuoted) {
        String field = line.substring(startIndex, endIndex);
		if (inQuoted) {
			field = StringUtil.replace(field, DOUBLE_QUOTE, "\"");
		}
        row.add(field);
    }

}

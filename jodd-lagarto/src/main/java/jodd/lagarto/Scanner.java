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

package jodd.lagarto;

import jodd.util.CharArraySequence;
import jodd.util.CharUtil;

/**
 * Utility scanner over a char buffer.
 */
class Scanner {

	protected char[] input;
	protected int ndx = 0;
	protected int total;

	Scanner() { }

	/**
	 * Initializes scanner.
	 */
	protected void initialize(final char[] input) {
		this.input = input;
		this.ndx = -1;
		this.total = input.length;
	}

	// ---------------------------------------------------------------- find

	/**
	 * Finds a character in some range and returns its index.
	 * Returns <code>-1</code> if character is not found.
	 */
	protected final int find(final char target, int from, final int end) {
		while (from < end) {
			if (input[from] == target) {
				break;
			}
			from++;
		}

		return (from == end) ? -1 : from;
	}

	/**
	 * Finds character buffer in some range and returns its index.
	 * Returns <code>-1</code> if character is not found.
	 */
	protected final int find(final char[] target, int from, final int end) {
		while (from < end) {
			if (match(target, from)) {
				break;
			}
			from++;
		}

		return (from == end) ? -1 : from;
	}

	// ---------------------------------------------------------------- match

	/**
	 * Matches char buffer with content on given location.
	 */
	protected final boolean match(final char[] target, final int ndx) {
		if (ndx + target.length >= total) {
			return false;
		}

		int j = ndx;

		for (int i = 0; i < target.length; i++, j++) {
			if (input[j] != target[i]) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Matches char buffer with content at current location case-sensitive.
	 */
	public final boolean match(final char[] target) {
		return match(target, ndx);
	}

	/**
	 * Matches char buffer given in uppercase with content at current location, that will
	 * be converted to upper case to make case-insensitive matching.
	 */
	public final boolean matchUpperCase(final char[] uppercaseTarget) {
		if (ndx + uppercaseTarget.length > total) {
			return false;
		}

		int j = ndx;

		for (int i = 0; i < uppercaseTarget.length; i++, j++) {
			final char c = CharUtil.toUpperAscii(input[j]);

			if (c != uppercaseTarget[i]) {
				return false;
			}
		}

		return true;
	}

	// ---------------------------------------------------------------- char sequences

	/**
	 * Creates char sub-sequence from the input.
	 */
	protected final CharSequence charSequence(final int from, final int to) {
		if (from == to) {
			return CharArraySequence.EMPTY;
		}
		return CharArraySequence.of(input, from, to - from);
	}

	// ---------------------------------------------------------------- position

	private int lastOffset = -1;
	private int lastLine;
	private int lastLastNewLineOffset;

	/**
	 * Returns <code>true</code> if EOF.
	 */
	protected final boolean isEOF() {
		return ndx >= total;
	}

	/**
	 * Calculates {@link Position current position}: offset, line and column.
	 */
	protected Position position(final int position) {
		int line = getLine(position);
		int offset;
		int lastNewLineOffset;

		//Co-Slice: N_CoV={Entry,1,3,4,6,7,8,9,10,12,13,14,15,16,17,Exit}
/*1*/	if (position > lastOffset) {
/*3*/		offset = 0;
/*4*/		lastNewLineOffset = 0;
		} else {
/*6*/		offset = lastOffset;
/*7*/		lastNewLineOffset = lastLastNewLineOffset;
		}
/*8*/	while (offset < position) {
/*9*/		final char c = input[offset];
/*10*/		if (c == '\n') {
/*12*/			lastNewLineOffset = offset + 1;
			}
/*13*/		offset++;
		}
/*14*/	lastOffset = offset;
/*15*/	lastLine = line;
/*16*/	lastLastNewLineOffset = lastNewLineOffset;

/*17*/	Position pos = new Position(position, line, position - lastNewLineOffset + 1);
		return pos;
	}

	private int getLine(int position) {
		int line;
		int offset;
/*1*/	if (position > lastOffset) {
/*2*/		line = 1;
/*3*/		offset = 0;
		} else {
/*5*/		line = lastLine;
/*6*/		offset = lastOffset;
		}
/*8*/	while (offset < position) {
/*9*/		final char c = input[offset];
/*10*/		if (c == '\n') {
/*11*/			line++;
			}
/*13*/		offset++;
		}
		return line;
	}

	/**
	 * Current position.
	 */
	public static class Position {

		private final int offset;
		private final int line;
		private final int column;

		public Position(final int offset, final int line, final int column) {
			this.offset = offset;
			this.line = line;
			this.column = column;
		}

		public Position(final int offset) {
			this.offset = offset;
			this.line = -1;
			this.column = -1;
		}

		@Override
		public String toString() {
			if (offset == -1) {
				return "[" + line + ':' + column + ']';
			}
			if (line == -1) {
				return "[@" + offset + ']';
			}
			return "[" + line + ':' + column + " @" + offset + ']';
		}
	}

}

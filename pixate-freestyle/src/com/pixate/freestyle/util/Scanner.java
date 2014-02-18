/* Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pixate.freestyle.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser that parses a text string of primitive types and strings with the
 * help of regular expressions. This class is not as useful as it might seem.
 * It's very inefficient for communicating between machines; you should use
 * JSON, protobufs, or even XML for that. Very simple uses might get away with
 * {@link String#split}. For input from humans, the use of locale-specific
 * regular expressions make it not only expensive but also somewhat
 * unpredictable.
 * <p>
 * This class supports localized numbers and various radixes. The input is
 * broken into tokens by the delimiter pattern, which is {@code \\p
 * javaWhitespace} by default.
 * <p>
 * Example:
 * 
 * <pre>
 * Scanner s = new Scanner(&quot;1A true&quot;);
 * assertEquals(26, s.nextInt(16));
 * assertEquals(true, s.nextBoolean());
 * </pre>
 * <p>
 * The {@code Scanner} class is not thread-safe.
 * 
 * @see https://android.googlesource.com/platform/libcore/+/master/luni/src/main/java/java/util/Scanner.java
 */
public final class Scanner implements Closeable, Iterator<String> {

    // Default delimiting pattern.
    private static final Pattern DEFAULT_DELIMITER = Pattern.compile("\\p{javaWhitespace}+");

    // The pattern matches anything.
    private static final Pattern ANY_PATTERN = Pattern.compile("(?s).*");

    private static final int DEFAULT_RADIX = 10;

    // The input source of scanner.
    private Readable input;

    private CharBuffer buffer;

    private Pattern delimiter = DEFAULT_DELIMITER;

    private Matcher matcher;

    private int currentRadix = DEFAULT_RADIX;

    private Locale locale = Locale.getDefault();

    // The position where find begins.
    private int findStartIndex = 0;

    // The last find start position.
    private int preStartIndex = findStartIndex;

    // The length of the buffer.
    private int bufferLength = 0;

    // Record the status of this scanner. True if the scanner is closed.
    private boolean closed = false;

    private IOException lastIOException;

    private boolean matchSuccessful = false;

    private DecimalFormat decimalFormat;

    // Records whether the underlying readable has more input.
    private boolean inputExhausted = false;

    private Object cachedNextValue = null;
    private int cachedNextIndex = -1;

    private Pattern cachedFloatPattern = null;

    private enum DataType {
        /*
         * Stands for Integer
         */
        INT,
        /*
         * Stands for Float
         */
        FLOAT;
    }

    /**
     * Creates a {@code Scanner} with the specified {@code File} as input. The
     * default charset is applied when reading the file.
     * 
     * @param src the file to be scanned.
     * @throws FileNotFoundException if the specified file does not exist.
     */
    public Scanner(File src) throws FileNotFoundException {
        this(src, Charset.defaultCharset().name());
    }

    /**
     * Creates a {@code Scanner} with the specified {@code File} as input. The
     * specified charset is applied when reading the file.
     * 
     * @param src the file to be scanned.
     * @param charsetName the name of the encoding type of the file.
     * @throws FileNotFoundException if the specified file does not exist.
     * @throws IllegalArgumentException if the specified coding does not exist.
     */
    public Scanner(File src, String charsetName) throws FileNotFoundException {
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        FileInputStream fis = new FileInputStream(src);
        if (charsetName == null) {
            throw new IllegalArgumentException("charsetName == null");
        }
        try {
            input = new InputStreamReader(fis, charsetName);
        } catch (UnsupportedEncodingException e) {
            closeQuietly(fis);
            throw new IllegalArgumentException(e.getMessage());
        }
        initialization();
    }

    /**
     * Closes 'closeable', ignoring any checked exceptions. Does nothing if
     * 'closeable' is null.
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Creates a {@code Scanner} on the specified string.
     * 
     * @param src the string to be scanned.
     */
    public Scanner(String src) {
        input = new StringReader(src);
        initialization();
    }

    /**
     * Creates a {@code Scanner} on the specified {@code InputStream}. The
     * default charset is applied when decoding the input.
     * 
     * @param src the {@code InputStream} to be scanned.
     */
    public Scanner(InputStream src) {
        this(src, Charset.defaultCharset().name());
    }

    /**
     * Creates a {@code Scanner} on the specified {@code InputStream}. The
     * specified charset is applied when decoding the input.
     * 
     * @param src the {@code InputStream} to be scanned.
     * @param charsetName the encoding type of the {@code InputStream}.
     * @throws IllegalArgumentException if the specified character set is not
     *             found.
     */
    public Scanner(InputStream src, String charsetName) {
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        try {
            input = new InputStreamReader(src, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        initialization();
    }

    /**
     * Creates a {@code Scanner} with the specified {@code Readable} as input.
     * 
     * @param src the {@code Readable} to be scanned.
     */
    public Scanner(Readable src) {
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        input = src;
        initialization();
    }

    /**
     * Creates a {@code Scanner} with the specified {@code ReadableByteChannel}
     * as input. The default charset is applied when decoding the input.
     * 
     * @param src the {@code ReadableByteChannel} to be scanned.
     */
    public Scanner(ReadableByteChannel src) {
        this(src, Charset.defaultCharset().name());
    }

    /**
     * Creates a {@code Scanner} with the specified {@code ReadableByteChannel}
     * as input. The specified charset is applied when decoding the input.
     * 
     * @param src the {@code ReadableByteChannel} to be scanned.
     * @param charsetName the encoding type of the content.
     * @throws IllegalArgumentException if the specified character set is not
     *             found.
     */
    public Scanner(ReadableByteChannel src, String charsetName) {
        if (src == null) {
            throw new NullPointerException("src == null");
        }
        if (charsetName == null) {
            throw new IllegalArgumentException("charsetName == null");
        }
        input = Channels.newReader(src, charsetName);
        initialization();
    }

    /**
     * Closes this {@code Scanner} and the underlying input if the input
     * implements {@code Closeable}. If the {@code Scanner} has been closed,
     * this method will have no effect. Any scanning operation called after
     * calling this method will throw an {@code IllegalStateException}.
     * 
     * @see Closeable
     */
    public void close() {
        if (closed) {
            return;
        }
        if (input instanceof Closeable) {
            try {
                ((Closeable) input).close();
            } catch (IOException e) {
                lastIOException = e;
            }
        }
        closed = true;
    }

    /**
     * Returns the delimiter {@code Pattern} in use by this {@code Scanner}.
     * 
     * @return the delimiter {@code Pattern} in use by this {@code Scanner}.
     */
    public Pattern delimiter() {
        return delimiter;
    }

    /**
     * Returns whether this {@code Scanner} has one or more tokens remaining to
     * parse. This method will block if the data is still being read.
     * 
     * @return {@code true} if this {@code Scanner} has one or more tokens
     *         remaining, otherwise {@code false}.
     * @throws IllegalStateException if the {@code Scanner} has been closed.
     */
    public boolean hasNext() {
        return hasNext(ANY_PATTERN);
    }

    /**
     * Returns whether this {@code Scanner} has one or more tokens remaining to
     * parse and the next token matches the given pattern. This method will
     * block if the data is still being read.
     * 
     * @param pattern the pattern to check for.
     * @return {@code true} if this {@code Scanner} has more tokens and the next
     *         token matches the pattern, {@code false} otherwise.
     * @throws IllegalStateException if the {@code Scanner} has been closed.
     */
    public boolean hasNext(Pattern pattern) {
        checkOpen();
        checkNotNull(pattern);
        matchSuccessful = false;
        saveCurrentStatus();
        // if the next token exists, set the match region, otherwise return
        // false
        if (!setTokenRegion()) {
            recoverPreviousStatus();
            return false;
        }
        matcher.usePattern(pattern);
        boolean hasNext = false;
        // check whether next token matches the specified pattern
        if (matcher.matches()) {
            cachedNextIndex = findStartIndex;
            matchSuccessful = true;
            hasNext = true;
        }
        recoverPreviousStatus();
        return hasNext;
    }

    /**
     * Returns whether the next token can be translated into a valid
     * {@code float} value.
     * 
     * @return {@code true} if the next token can be translated into a valid
     *         {@code float} value, otherwise {@code false}.
     * @throws IllegalStateException if the {@code Scanner} has been closed.
     */
    public boolean hasNextFloat() {
        Pattern floatPattern = getFloatPattern();
        boolean isFloatValue = false;
        if (hasNext(floatPattern)) {
            String floatString = matcher.group();
            floatString = removeLocaleInfoFromFloat(floatString);
            try {
                cachedNextValue = Float.valueOf(floatString);
                isFloatValue = true;
            } catch (NumberFormatException e) {
                matchSuccessful = false;
            }
        }
        return isFloatValue;
    }

    /**
     * Returns the last {@code IOException} that was raised while reading from
     * the underlying input, or {@code null} if none was thrown.
     */
    public IOException ioException() {
        return lastIOException;
    }

    /**
     * Returns the {@code Locale} of this {@code Scanner}.
     */
    public Locale locale() {
        return locale;
    }

    private void setLocale(Locale locale) {
        this.locale = locale;
        this.decimalFormat = null;
        this.cachedFloatPattern = null;
    }

    /**
     * Returns the result of the last matching operation.
     * <p>
     * The next* and find* methods return the match result in the case of a
     * successful match.
     * 
     * @return the match result of the last successful match operation
     * @throws IllegalStateException if the match result is not available, of if
     *             the last match was not successful.
     */
    public MatchResult match() {
        if (!matchSuccessful) {
            throw new IllegalStateException();
        }
        return matcher.toMatchResult();
    }

    /**
     * Returns the next token. The token will be both prefixed and suffixed by
     * the delimiter that is currently being used (or a string that matches the
     * delimiter pattern). This method will block if input is being read.
     * 
     * @return the next complete token.
     * @throws IllegalStateException if this {@code Scanner} has been closed.
     * @throws NoSuchElementException if input has been exhausted.
     */
    public String next() {
        return next(ANY_PATTERN);
    }

    /**
     * Returns the next token if it matches the specified pattern. The token
     * will be both prefixed and suffixed by the delimiter that is currently
     * being used (or a string that matches the delimiter pattern). This method
     * will block if input is being read.
     * 
     * @param pattern the specified pattern to scan.
     * @return the next token.
     * @throws IllegalStateException if this {@code Scanner} has been closed.
     * @throws NoSuchElementException if input has been exhausted.
     * @throws InputMismatchException if the next token does not match the
     *             pattern given.
     */
    public String next(Pattern pattern) {
        checkOpen();
        checkNotNull(pattern);
        matchSuccessful = false;
        saveCurrentStatus();
        if (!setTokenRegion()) {
            recoverPreviousStatus();
            // if setting match region fails
            throw new NoSuchElementException();
        }
        matcher.usePattern(pattern);
        if (!matcher.matches()) {
            recoverPreviousStatus();
            throw new InputMismatchException();

        }
        matchSuccessful = true;
        return matcher.group();
    }

    /**
     * Returns the next token as a {@code float}. This method will block if
     * input is being read. If the next token can be translated into a
     * {@code float} the following is done: All {@code Locale}-specific
     * prefixes, group separators, and {@code Locale}-specific suffixes are
     * removed. Then non-ASCII digits are mapped into ASCII digits via
     * {@link Character#digit(char, int)}, and a negative sign (-) is added if
     * the {@code Locale}-specific negative prefix or suffix was present.
     * Finally the resulting String is passed to
     * {@link Float#parseFloat(String)} .If the token matches the localized NaN
     * or infinity strings, it is also passed to
     * {@link Float#parseFloat(String)} .
     * 
     * @return the next token as a {@code float}.
     * @throws IllegalStateException if this {@code Scanner} has been closed.
     * @throws NoSuchElementException if input has been exhausted.
     * @throws InputMismatchException if the next token can not be translated
     *             into a valid {@code float} value.
     */
    @SuppressWarnings("boxing")
    public float nextFloat() {
        checkOpen();
        Object obj = cachedNextValue;
        cachedNextValue = null;
        if (obj instanceof Float) {
            findStartIndex = cachedNextIndex;
            return (Float) obj;
        }
        Pattern floatPattern = getFloatPattern();
        String floatString = next(floatPattern);
        floatString = removeLocaleInfoFromFloat(floatString);
        float floatValue = 0;
        try {
            floatValue = Float.parseFloat(floatString);
        } catch (NumberFormatException e) {
            matchSuccessful = false;
            recoverPreviousStatus();
            throw new InputMismatchException();
        }
        return floatValue;
    }

    /**
     * Return the radix of this {@code Scanner}.
     * 
     * @return the radix of this {@code Scanner}
     */
    public int radix() {
        return currentRadix;
    }

    /**
     * Tries to use specified pattern to match input starting from the current
     * position. The delimiter will be ignored. If a match is found, the matched
     * input will be skipped. If an anchored match of the specified pattern
     * succeeds, the corresponding input will also be skipped. Otherwise, a
     * {@code NoSuchElementException} will be thrown. Patterns that can match a
     * lot of input may cause the {@code Scanner} to read in a large amount of
     * input.
     * 
     * @param pattern used to skip over input.
     * @return the {@code Scanner} itself.
     * @throws IllegalStateException if the {@code Scanner} is closed.
     * @throws NoSuchElementException if the specified pattern match fails.
     */
    public Scanner skip(Pattern pattern) {
        checkOpen();
        checkNotNull(pattern);
        matcher.usePattern(pattern);
        matcher.region(findStartIndex, bufferLength);
        while (true) {
            if (matcher.lookingAt()) {
                boolean matchInBuffer = matcher.end() < bufferLength
                        || (matcher.end() == bufferLength && inputExhausted);
                if (matchInBuffer) {
                    matchSuccessful = true;
                    findStartIndex = matcher.end();
                    break;
                }
            } else {
                if (inputExhausted) {
                    matchSuccessful = false;
                    throw new NoSuchElementException();
                }
            }
            if (!inputExhausted) {
                readMore();
                resetMatcher();
            }
        }
        return this;
    }

    /**
     * Tries to use the specified string to construct a pattern and then uses
     * the constructed pattern to match input starting from the current
     * position. The delimiter will be ignored. This call is the same as invoke
     * {@code skip(Pattern.compile(pattern))}.
     * 
     * @param pattern the string used to construct a pattern which in turn is
     *            used to match input.
     * @return the {@code Scanner} itself.
     * @throws IllegalStateException if the {@code Scanner} is closed.
     */
    public Scanner skip(String pattern) {
        return skip(Pattern.compile(pattern));
    }

    /**
     * Returns a string representation of this {@code Scanner}. The information
     * returned may be helpful for debugging. The format of the string is
     * unspecified.
     * 
     * @return a string representation of this {@code Scanner}.
     */
    @Override
    public String toString() {
        return getClass().getName() + "[delimiter=" + delimiter + ",findStartIndex="
                + findStartIndex + ",matchSuccessful=" + matchSuccessful + ",closed=" + closed
                + "]";
    }

    /**
     * Sets the delimiting pattern of this {@code Scanner}.
     * 
     * @param pattern the delimiting pattern to use.
     * @return this {@code Scanner}.
     */
    public Scanner useDelimiter(Pattern pattern) {
        delimiter = pattern;
        return this;
    }

    /**
     * Sets the delimiting pattern of this {@code Scanner} with a pattern
     * compiled from the supplied string value.
     * 
     * @param pattern a string from which a {@code Pattern} can be compiled.
     * @return this {@code Scanner}.
     */
    public Scanner useDelimiter(String pattern) {
        return useDelimiter(Pattern.compile(pattern));
    }

    /**
     * Sets the {@code Locale} of this {@code Scanner} to a specified
     * {@code Locale}.
     * 
     * @param l the specified {@code Locale} to use.
     * @return this {@code Scanner}.
     */
    public Scanner useLocale(Locale l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }
        setLocale(l);
        return this;
    }

    /**
     * Sets the radix of this {@code Scanner} to the specified radix.
     * 
     * @param radix the specified radix to use.
     * @return this {@code Scanner}.
     */
    public Scanner useRadix(int radix) {
        checkRadix(radix);
        this.currentRadix = radix;
        return this;
    }

    private void checkRadix(int radix) {
        if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX) {
            throw new IllegalArgumentException("Invalid radix: " + radix);
        }
    }

    /**
     * Remove is not a supported operation on {@code Scanner}.
     * 
     * @throws UnsupportedOperationException if this method is invoked.
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /*
     * Initialize some components.
     */
    private void initialization() {
        buffer = CharBuffer.allocate(1024);
        buffer.limit(0);
        matcher = delimiter.matcher(buffer);
    }

    private void checkOpen() {
        if (closed) {
            throw new IllegalStateException();
        }
    }

    private void checkNotNull(Pattern pattern) {
        if (pattern == null) {
            throw new NullPointerException("pattern == null");
        }
    }

    /*
     * Change the matcher's string after reading input
     */
    private void resetMatcher() {
        if (matcher == null) {
            matcher = delimiter.matcher(buffer);
        } else {
            matcher.reset(buffer);
        }
        matcher.region(findStartIndex, bufferLength);
    }

    /*
     * Save the matcher's last find position
     */
    private void saveCurrentStatus() {
        preStartIndex = findStartIndex;
    }

    /*
     * Change the matcher's status to last find position
     */
    private void recoverPreviousStatus() {
        findStartIndex = preStartIndex;
    }

    private Pattern getFloatPattern() {
        if (decimalFormat == null) {
            decimalFormat = (DecimalFormat) NumberFormat.getInstance(locale);
        }

        if (cachedFloatPattern != null) {
            return cachedFloatPattern;
        }

        DecimalFormatSymbols dfs = decimalFormat.getDecimalFormatSymbols();

        String digit = "([0-9]|(\\p{javaDigit}))";
        String nonZeroDigit = "[\\p{javaDigit}&&[^0]]";
        String numeral = getNumeral(digit, nonZeroDigit);

        String decimalSeparator = "\\" + dfs.getDecimalSeparator();
        String decimalNumeral = "(" + numeral + "|" + numeral + decimalSeparator + digit + "*+|"
                + decimalSeparator + digit + "++)";
        String exponent = "([eE][+-]?" + digit + "+)?";

        String decimal = "(([-+]?" + decimalNumeral + "(" + exponent + "?)" + ")|" + "("
                + addPositiveSign(decimalNumeral) + "(" + exponent + "?)" + ")|" + "("
                + addNegativeSign(decimalNumeral) + "(" + exponent + "?)" + "))";

        String hexFloat = "([-+]?0[xX][0-9a-fA-F]*\\.[0-9a-fA-F]+([pP][-+]?[0-9]+)?)";
        String localNaN = dfs.getNaN();
        String localeInfinity = dfs.getInfinity();
        String nonNumber = "(NaN|\\Q" + localNaN + "\\E|Infinity|\\Q" + localeInfinity + "\\E)";
        String signedNonNumber = "((([-+]?(" + nonNumber + ")))|" + "("
                + addPositiveSign(nonNumber) + ")|" + "(" + addNegativeSign(nonNumber) + "))";

        cachedFloatPattern = Pattern.compile(decimal + "|" + hexFloat + "|" + signedNonNumber);
        return cachedFloatPattern;
    }

    private String getNumeral(String digit, String nonZeroDigit) {
        String groupSeparator = "\\"
                + decimalFormat.getDecimalFormatSymbols().getGroupingSeparator();
        String groupedNumeral = "(" + nonZeroDigit + digit + "?" + digit + "?" + "("
                + groupSeparator + digit + digit + digit + ")+)";
        return "((" + digit + "++)|" + groupedNumeral + ")";
    }

    /*
     * Add the locale specific positive prefixes and suffixes to the pattern
     */
    private String addPositiveSign(String unsignedNumeral) {
        String positivePrefix = "";
        String positiveSuffix = "";
        if (!decimalFormat.getPositivePrefix().isEmpty()) {
            positivePrefix = "\\Q" + decimalFormat.getPositivePrefix() + "\\E";
        }
        if (!decimalFormat.getPositiveSuffix().isEmpty()) {
            positiveSuffix = "\\Q" + decimalFormat.getPositiveSuffix() + "\\E";
        }
        return positivePrefix + unsignedNumeral + positiveSuffix;
    }

    /*
     * Add the locale specific negative prefixes and suffixes to the pattern
     */
    private String addNegativeSign(String unsignedNumeral) {
        String negativePrefix = "";
        String negativeSuffix = "";
        if (!decimalFormat.getNegativePrefix().isEmpty()) {
            negativePrefix = "\\Q" + decimalFormat.getNegativePrefix() + "\\E";
        }
        if (!decimalFormat.getNegativeSuffix().isEmpty()) {
            negativeSuffix = "\\Q" + decimalFormat.getNegativeSuffix() + "\\E";
        }
        return negativePrefix + unsignedNumeral + negativeSuffix;
    }

    /*
     * Remove locale related information from float String
     */
    private String removeLocaleInfoFromFloat(String floatString) {
        // If the token is HexFloat
        if (floatString.indexOf('x') != -1 || floatString.indexOf('X') != -1) {
            return floatString;
        }

        // If the token is scientific notation
        int exponentIndex;
        if ((exponentIndex = floatString.indexOf('e')) != -1
                || (exponentIndex = floatString.indexOf('E')) != -1) {
            String decimalNumeralString = floatString.substring(0, exponentIndex);
            String exponentString = floatString.substring(exponentIndex + 1, floatString.length());
            decimalNumeralString = removeLocaleInfo(decimalNumeralString, DataType.FLOAT);
            return decimalNumeralString + "e" + exponentString;
        }
        return removeLocaleInfo(floatString, DataType.FLOAT);
    }

    /*
     * Remove the locale specific prefixes, group separators, and locale
     * specific suffixes from input string
     */
    private String removeLocaleInfo(String token, DataType type) {
        DecimalFormatSymbols dfs = decimalFormat.getDecimalFormatSymbols();

        StringBuilder tokenBuilder = new StringBuilder(token);
        boolean negative = removeLocaleSign(tokenBuilder);
        // Remove group separator
        String groupSeparator = String.valueOf(dfs.getGroupingSeparator());
        int separatorIndex = -1;
        while ((separatorIndex = tokenBuilder.indexOf(groupSeparator)) != -1) {
            tokenBuilder.delete(separatorIndex, separatorIndex + 1);
        }
        // Remove decimal separator
        String decimalSeparator = String.valueOf(dfs.getDecimalSeparator());
        separatorIndex = tokenBuilder.indexOf(decimalSeparator);
        StringBuilder result = new StringBuilder("");
        if (DataType.INT == type) {
            for (int i = 0; i < tokenBuilder.length(); i++) {
                if (Character.digit(tokenBuilder.charAt(i), Character.MAX_RADIX) != -1) {
                    result.append(tokenBuilder.charAt(i));
                }
            }
        }
        if (DataType.FLOAT == type) {
            if (tokenBuilder.toString().equals(dfs.getNaN())) {
                result.append("NaN");
            } else if (tokenBuilder.toString().equals(dfs.getInfinity())) {
                result.append("Infinity");
            } else {
                for (int i = 0; i < tokenBuilder.length(); i++) {
                    if (Character.digit(tokenBuilder.charAt(i), 10) != -1) {
                        result.append(Character.digit(tokenBuilder.charAt(i), 10));
                    }
                }
            }
        }
        // Token is NaN or Infinity
        if (result.length() == 0) {
            result = tokenBuilder;
        }
        if (separatorIndex != -1) {
            result.insert(separatorIndex, ".");
        }
        // If input is negative
        if (negative) {
            result.insert(0, '-');
        }
        return result.toString();
    }

    /*
     * Remove positive and negative sign from the parameter stringBuilder, and
     * return whether the input string is negative
     */
    private boolean removeLocaleSign(StringBuilder tokenBuilder) {
        String positivePrefix = decimalFormat.getPositivePrefix();
        String positiveSuffix = decimalFormat.getPositiveSuffix();
        String negativePrefix = decimalFormat.getNegativePrefix();
        String negativeSuffix = decimalFormat.getNegativeSuffix();

        if (tokenBuilder.indexOf("+") == 0) {
            tokenBuilder.delete(0, 1);
        }
        if (!positivePrefix.isEmpty() && tokenBuilder.indexOf(positivePrefix) == 0) {
            tokenBuilder.delete(0, positivePrefix.length());
        }
        if (!positiveSuffix.isEmpty() && tokenBuilder.indexOf(positiveSuffix) != -1) {
            tokenBuilder.delete(tokenBuilder.length() - positiveSuffix.length(),
                    tokenBuilder.length());
        }
        boolean negative = false;
        if (tokenBuilder.indexOf("-") == 0) {
            tokenBuilder.delete(0, 1);
            negative = true;
        }
        if (!negativePrefix.isEmpty() && tokenBuilder.indexOf(negativePrefix) == 0) {
            tokenBuilder.delete(0, negativePrefix.length());
            negative = true;
        }
        if (!negativeSuffix.isEmpty() && tokenBuilder.indexOf(negativeSuffix) != -1) {
            tokenBuilder.delete(tokenBuilder.length() - negativeSuffix.length(),
                    tokenBuilder.length());
            negative = true;
        }
        return negative;
    }

    /*
     * Find the prefixed delimiter and suffixed delimiter in the input resource
     * and set the start index and end index of Matcher region. If the suffixed
     * delimiter does not exist, the end index is set to be end of input.
     */
    private boolean setTokenRegion() {
        // The position where token begins
        int tokenStartIndex = 0;
        // The position where token ends
        int tokenEndIndex = 0;
        // Use delimiter pattern
        matcher.usePattern(delimiter);
        matcher.region(findStartIndex, bufferLength);

        tokenStartIndex = findPreDelimiter();
        if (setHeadTokenRegion(tokenStartIndex)) {
            return true;
        }
        tokenEndIndex = findDelimiterAfter();
        // If the second delimiter is not found
        if (-1 == tokenEndIndex) {
            // Just first Delimiter Exists
            if (findStartIndex == bufferLength) {
                return false;
            }
            tokenEndIndex = bufferLength;
            findStartIndex = bufferLength;
        }

        matcher.region(tokenStartIndex, tokenEndIndex);
        return true;
    }

    /*
     * Find prefix delimiter
     */
    private int findPreDelimiter() {
        int tokenStartIndex;
        boolean findComplete = false;
        while (!findComplete) {
            if (matcher.find()) {
                findComplete = true;
                // If just delimiter remains
                if (matcher.start() == findStartIndex && matcher.end() == bufferLength) {
                    // If more input resource exists
                    if (!inputExhausted) {
                        readMore();
                        resetMatcher();
                        findComplete = false;
                    }
                }
            } else {
                if (!inputExhausted) {
                    readMore();
                    resetMatcher();
                } else {
                    return -1;
                }
            }
        }
        tokenStartIndex = matcher.end();
        findStartIndex = matcher.end();
        return tokenStartIndex;
    }

    /*
     * Handle some special cases
     */
    private boolean setHeadTokenRegion(int findIndex) {
        int tokenStartIndex;
        int tokenEndIndex;
        boolean setSuccess = false;
        // If no delimiter exists, but something exists in this scanner
        if (-1 == findIndex && preStartIndex != bufferLength) {
            tokenStartIndex = preStartIndex;
            tokenEndIndex = bufferLength;
            findStartIndex = bufferLength;
            matcher.region(tokenStartIndex, tokenEndIndex);
            setSuccess = true;
        }
        // If the first delimiter of scanner is not at the find start position
        if (-1 != findIndex && preStartIndex != matcher.start()) {
            tokenStartIndex = preStartIndex;
            tokenEndIndex = matcher.start();
            findStartIndex = matcher.start();
            // set match region and return
            matcher.region(tokenStartIndex, tokenEndIndex);
            setSuccess = true;
        }
        return setSuccess;
    }

    private int findDelimiterAfter() {
        int tokenEndIndex = 0;
        boolean findComplete = false;
        while (!findComplete) {
            if (matcher.find()) {
                findComplete = true;
                if (matcher.start() == findStartIndex && matcher.start() == matcher.end()) {
                    findComplete = false;
                }
            } else {
                if (!inputExhausted) {
                    readMore();
                    resetMatcher();
                } else {
                    return -1;
                }
            }
        }
        tokenEndIndex = matcher.start();
        findStartIndex = matcher.start();
        return tokenEndIndex;
    }

    /*
     * Read more data from underlying Readable. If nothing is available or I/O
     * operation fails, global boolean variable inputExhausted will be set to
     * true, otherwise set to false.
     */
    private void readMore() {
        int oldPosition = buffer.position();
        int oldBufferLength = bufferLength;
        // Increase capacity if empty space is not enough
        if (bufferLength >= buffer.capacity()) {
            expandBuffer();
        }

        // Read input resource
        int readCount = 0;
        try {
            buffer.limit(buffer.capacity());
            buffer.position(oldBufferLength);
            while ((readCount = input.read(buffer)) == 0) {
                // nothing to do here
            }
        } catch (IOException e) {
            // Consider the scenario: readable puts 4 chars into
            // buffer and then an IOException is thrown out. In this case,
            // buffer is
            // actually grown, but readable.read() will never return.
            bufferLength = buffer.position();
            /*
             * Uses -1 to record IOException occurring, and no more input can be
             * read.
             */
            readCount = -1;
            lastIOException = e;
        }

        buffer.flip();
        buffer.position(oldPosition);
        if (-1 == readCount) {
            inputExhausted = true;
        } else {
            bufferLength = readCount + bufferLength;
        }
    }

    // Expand the size of internal buffer.
    private void expandBuffer() {
        int oldPosition = buffer.position();
        int oldCapacity = buffer.capacity();
        int oldLimit = buffer.limit();
        int newCapacity = oldCapacity * 2;
        char[] newBuffer = new char[newCapacity];
        System.arraycopy(buffer.array(), 0, newBuffer, 0, oldLimit);
        buffer = CharBuffer.wrap(newBuffer, 0, newCapacity);
        buffer.position(oldPosition);
        buffer.limit(oldLimit);
    }

    /**
     * Resets this scanner's delimiter, locale, and radix.
     * 
     * @return this scanner
     * @since 1.6
     */
    public Scanner reset() {
        delimiter = DEFAULT_DELIMITER;
        setLocale(Locale.getDefault());
        currentRadix = DEFAULT_RADIX;
        return this;
    }
}

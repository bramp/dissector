/**
 * Originally taken from Pivot's Span class, but adapted to use longs
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.bramp.hex;


import org.apache.pivot.collections.Dictionary;
import org.apache.pivot.json.JSONSerializer;
import org.apache.pivot.serialization.SerializationException;

/**
 * Class representing a range of integer values. The range includes all
 * values in the interval <i>[start, end]</i>. Values may be negative, and the
 * value of <tt>start</tt> may be less than or equal to the value of
 * <tt>end</tt>.
 */
public final class LongSpan {
    public final long start;
    public final long end;

    public static final String START_KEY = "start";
    public static final String END_KEY = "end";

    public LongSpan(long index) {
        start = index;
        end = index;
    }

    public LongSpan(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public LongSpan(LongSpan span) {
        if (span == null) {
            throw new IllegalArgumentException("span is null.");
        }

        start = span.start;
        end = span.end;
    }

    public LongSpan(Dictionary<String, ?> span) {
        if (span == null) {
            throw new IllegalArgumentException("span is null.");
        }

        if (!span.containsKey(START_KEY)) {
            throw new IllegalArgumentException(START_KEY + " is required.");
        }

        if (!span.containsKey(END_KEY)) {
            throw new IllegalArgumentException(END_KEY + " is required.");
        }

        start = (Long)span.get(START_KEY);
        end = (Long)span.get(END_KEY);
    }

    /**
     * Returns the length of the span.
     *
     * @return
     * The absolute value of (<tt>end</tt> minus <tt>start</tt>) + 1.
     */
    public long getLength() {
        return Math.abs(end - start) + 1;
    }

    /**
     * Determines whether this span contains another span.
     *
     * @param span
     * The span to test for containment.
     *
     * @return
     * <tt>true</tt> if this span contains <tt>span</tt>; <tt>false</tt>,
     * otherwise.
     */
    public boolean contains(LongSpan span) {
        if (span == null) {
            throw new IllegalArgumentException("span is null.");
        }

        LongSpan normalizedSpan = span.normalize();

        boolean contains;
        if (start < end) {
            contains = (start <= normalizedSpan.start
                    && end >= normalizedSpan.end);
        } else {
            contains = (end <= normalizedSpan.start
                    && start >= normalizedSpan.end);
        }

        return contains;
    }

    /**
     * Determines whether this span intersects with another span.
     *
     * @param span
     * The span to test for intersection.
     *
     * @return
     * <tt>true</tt> if this span intersects with <tt>span</tt>;
     * <tt>false</tt>, otherwise.
     */
    public boolean intersects(LongSpan span) {
        if (span == null) {
            throw new IllegalArgumentException("span is null.");
        }

        LongSpan normalizedSpan = span.normalize();

        boolean intersects;
        if (start < end) {
            intersects = (start <= normalizedSpan.end
                    && end >= normalizedSpan.start);
        } else {
            intersects = (end <= normalizedSpan.end
                    && start >= normalizedSpan.start);
        }

        return intersects;
    }

    /**
     * Calculates the intersection of this span and another span.
     *
     * @param span
     * The span to intersect with this span.
     *
     * @return
     * A new Span instance representing the intersection of this span and
     * <tt>span</tt>, or null if the spans do not intersect.
     */
    public LongSpan intersect(LongSpan span) {
        if (span == null) {
            throw new IllegalArgumentException("span is null.");
        }

        LongSpan intersection = null;

        if (intersects(span)) {
            intersection = new LongSpan(Math.max(start, span.start),
                    Math.min(end, span.end));
        }

        return intersection;
    }

    /**
     * Calculates the union of this span and another span.
     *
     * @param span
     * The span to union with this span.
     *
     * @return
     * A new Span instance representing the union of this span and
     * <tt>span</tt>.
     */
    public LongSpan union(LongSpan span) {
        if (span == null) {
            throw new IllegalArgumentException("span is null.");
        }

        return new LongSpan(Math.min(start, span.start),
                Math.max(end, span.end));
    }

    /**
     * Returns a normalized equivalent of the span in which
     * <tt>start</tt> is guaranteed to be less than end.
     */
    public LongSpan normalize() {
        return new LongSpan(Math.min(start, end), Math.max(start, end));
    }

    @Override
    public boolean equals(Object o) {
        boolean equal = false;

        if (o instanceof LongSpan) {
            LongSpan span = (LongSpan)o;
            equal = (start == span.start
                    && end == span.end);
        }

        return equal;
    }

    @Override
    public int hashCode() {
        return (int) (31 * start + end);
    }

    @Override
    public String toString() {
        return ("{start: " + start + ", end: " + end + "}");
    }

    public static LongSpan decode(String value) {
        if (value == null) {
            throw new IllegalArgumentException();
        }

        LongSpan span;
        if (value.startsWith("{")) {
            try {
                span = new LongSpan(JSONSerializer.parseMap(value));
            } catch (SerializationException exception) {
                throw new IllegalArgumentException(exception);
            }
        } else {
            span = new LongSpan(Long.parseLong(value));
        }

        return span;
    }
}

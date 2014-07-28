package net.bramp.dissector.node;

import net.bramp.dissector.io.ExtendedRandomAccessFile;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Seconds;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author bramp
 */
public class TimestampNode extends Node<String> {

	public final static DateTime SINCE_JAN_1970 = new DateTime("1970-01-01T00:00:00");

	final DateTime since;
	final Node<? extends Number> in;
	final TimeUnit units;

	public TimestampNode(Node<? extends Number> in) {
		this(in, TimeUnit.MILLISECONDS, SINCE_JAN_1970);
	}

	public TimestampNode(Node<? extends Number> in, TimeUnit units) {
		this(in, units, SINCE_JAN_1970);
	}

	public TimestampNode(Node<? extends Number> in, TimeUnit units, DateTime since) {
		this.in = in;
		this.units = units;
		this.since = since;
	}

	@Override
	public String value() {
		return toString();
	}

	public String toString() {
		long milli = TimeUnit.MILLISECONDS.convert(in.value().longValue(), units);
		return since.plus(milli).toString();
	}

	@Override
	public long getEnd() {
		return in.getEnd();
	}

	@Override
	public long getStart() {
		return in.getStart();
	}
}

package net.bramp.dissector.io;

import java.io.IOException;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author bramp
 */
public class IoHelper {

	private IoHelper() {
		// Empty
	}

	/**
	 * Search backwards for the data
	 *  Leaves the filed seeked to just after the data
	 * TODO use a better algorithm
	 * @param in
	 * @param data
	 * @return the offset to the data within the file, or -1 if not found
	 */
	public static long searchBackwards(ExtendedRandomAccessFile in, byte[] data) throws IOException {
		checkNotNull(in);
		checkNotNull(data);
		checkArgument(data.length > 0);

		byte[] buf = new byte[data.length];
		long pos = in.length() - data.length;

		while (pos >= 0) {
			in.seek(pos);
			in.readFully(buf);
			if (Arrays.equals(buf, data))
				return pos;
			pos--;
		}

		return -1;
	}

}

package net.bramp.dissector.iso;

import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.Dissector;

import java.io.EOFException;
import java.io.IOException;

/**
 * ISO/IEC 14496-12 ISO File Format
 *
 * Sources of Information:
 *   MPEG-4 Part 14 (formally ISO/IEC 14496-14:2003)
 *   http://wiki.multimedia.cx/index.php?title=MP4
 *   c061988_ISO_IEC_14496-12_2012.zip
 *
 * @author bramp
 */
public class IsoDissector extends Dissector {

	public IsoDissector() {}

	public IsoDissector read(ExtendedRandomAccessFile in) throws IOException {

		try {
			long length = in.length();
			while(length > 0) {
				IsoAtom atom = new IsoAtom();
				addChild( "atom", atom).read(in, length);
				length -= atom.length();
			}

		} catch (EOFException eof) {
			// Ignore
			// TODO FIX
		}
		return this;
	}


}

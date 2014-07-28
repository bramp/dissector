package net.bramp.dissector.iso;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.*;

import java.io.EOFException;
import java.io.IOException;
import java.util.Map;

/**
 * @author bramp
 */
public class IsoAtoms extends TreeNode {

	public IsoAtoms() {}

	public IsoAtoms read(ExtendedRandomAccessFile in, long length) throws IOException {
		try {

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

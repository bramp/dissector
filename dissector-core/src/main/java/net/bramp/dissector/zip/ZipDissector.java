package net.bramp.dissector.zip;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.*;
import net.bramp.dissector.png.PngChunkNode;
import net.bramp.dissector.png.PngHeaderNode;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static net.bramp.dissector.io.IoHelper.searchBackwards;

/**
 * @author bramp
 */
public class ZipDissector extends Dissector {

	static final Map<Long, String> signatureTypes = ImmutableMap.<Long, String>builder()
		.put((long)0x04034b50, "Local file header")
		.put((long)0x08074b50, "Data descriptor")
		.put((long)0x02014b50, "Central directory file header")
		.put((long)0x06054b50, "End of central directory record (EOCD)")
		.build();

	ShortNode directoryCount;
	IntNode directoryOffset;

    public ZipDissector() {}

	/**
	 * Finds the End of central directory
	 * @param in
	 * @return
	 * @throws IOException
	 */
	protected TreeNode readEocd(ExtendedRandomAccessFile in) throws IOException {
		in.seek(in.length());
		long pos = searchBackwards(in, new byte[] {0x50, 0x4b, 0x05, 0x06});
		checkArgument(pos != -1, "Zip file does not contain a central directory");
		in.seek(pos);

		TreeNode eocd = addChild( "EOCD", new TreeNode().read(in) );

		eocd.addChild("signature", new EnumNode(signatureTypes, new IntNode().base(16).read(in)) );
		eocd.addChild("number of this disk", new ShortNode().read(in));
		eocd.addChild("disk where central directory starts", new ShortNode().read(in) );

		eocd.addChild("number of central directory records on this disk", new ShortNode().read(in) );
		directoryCount = eocd.addChild("total number of central directory records", new ShortNode().read(in) );

		eocd.addChild("size of central directory", new IntNode().read(in) );

		directoryOffset = new IntNode().read(in);
		eocd.addChild("offset of start of central directory, relative to start of archive", directoryOffset );

		ShortNode commentLen = eocd.addChild("comment length", new ShortNode().read(in) );
		eocd.addChild("comment", new FixedStringNode(commentLen).read(in));

		return eocd;
	}

	protected TreeNode readCd(ExtendedRandomAccessFile in) throws IOException {

		TreeNode cd = addChild( "Central directory", new TreeNode().read(in) );

		cd.addChild("signature", new EnumNode(signatureTypes, new IntNode().base(16).read(in)) );
		cd.addChild("version made by", new ShortNode().read(in));
		cd.addChild("Version needed to extract (min)", new ShortNode().read(in) );

		cd.addChild("General purpose bit flag", new ShortNode().read(in) );
		cd.addChild("Compression method", new ShortNode().read(in) );
		cd.addChild("File last modification time", new ShortNode().read(in) );
		cd.addChild("File last modification date", new ShortNode().read(in) );

		cd.addChild("CRC-32", new IntNode().read(in) );
		cd.addChild("Compressed size", new IntNode().read(in) );
		cd.addChild("Uncompressed size", new IntNode().read(in) );

		ShortNode filenameLen = cd.addChild("File name length", new ShortNode().read(in) );
		ShortNode extraLen = cd.addChild("Extra field length", new ShortNode().read(in) );
		ShortNode commentLen = cd.addChild("File comment length", new ShortNode().read(in) );
		cd.addChild("Disk number where file starts", new ShortNode().read(in) );
		cd.addChild("Internal file attributes", new ShortNode().read(in) );

		cd.addChild("External file attributes", new IntNode().read(in) );
		cd.addChild("Relative offset of local file header", new IntNode().read(in) );

		cd.addChild("File name", new FixedStringNode(filenameLen).read(in) );
		cd.addChild("Extra field", new FixedStringNode(extraLen).read(in) );
		cd.addChild("File comment", new FixedStringNode(commentLen).read(in) );

		return cd;
	}

    public ZipDissector read(ExtendedRandomAccessFile in) throws IOException {

	    in.setEndian(ByteOrder.LITTLE_ENDIAN);

	    try {
		    readEocd(in);

		    if (directoryOffset == null || directoryCount == null)
			    return this;

		    in.seek( directoryOffset.value() );
			for (int i = 0; i < directoryCount.value(); i++)
		        readCd(in);

	    } catch (EOFException eof) {
		    // Ignore
		    // TODO FIX
	    }


	    return this;
    }
}

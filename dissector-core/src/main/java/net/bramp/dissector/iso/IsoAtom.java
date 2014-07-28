package net.bramp.dissector.iso;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.*;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author bramp
 */
public class IsoAtom extends TreeNode {

	final static DateTime SINCE_JAN_1904 = new DateTime("1904-01-01T00:00:00");

	static final Map<String, String> atomTypes = ImmutableMap.<String, String>builder()
		.put("ftyp", "File type")
		.put("free", "Free space")
		.put("skip", "Free space")
		.put("mdat", "Media data")
		.put("moov", "Movie")
		.put("mvhd", "Movie header")
		.put("trak", "Track")
		.put("tkhd", "Track header")
		.put("mdia", "Media")
		.put("mdhd", "Media header")
		.put("udta", "User data")
		.put("hdlr", "Handler reference")
		.put("minf", "Media information")
		.put("edts", "Edit")
		.put("elst", "Edit List")
		.put("vmhd", "Video media header")
		.put("smhd", "Sound media header")
		.put("dinf", "Data information")
		.put("stbl", "Sample table")

		// MP4
		.put("iods", "Initial object descriptor box")

		.build();

	static final Map<String, String> atomBrands = ImmutableMap.<String, String>builder()
		.put("qt  ", "Quicktime")
		.put("isom", "")
		.put("mp41", "MPEG4 v1")
		.put("mp42", "MPEG4 v2")
		.put("3gp1", "")
		.put("3gp2", "")
		.put("3gp3", "")
		.put("3gp4", "")
		.put("3gp5", "")
		.put("3g2a", "")
		.put("mmp4", "")
		.put("M4A ", "")
		.put("M4P ", "")
		.put("M4V ", "")
		.put("mjp2", "")
		.put("MSNV", "")
		.put("FACE", "")
		.put("avc1", "")
		.build();

	static final Map<String, String> handlerTypes = ImmutableMap.<String, String>builder()
			.put("vide", "Video track")
			.put("soun", "Audio track")
			.put("hint", "Hint track")
			.put("meta", "Timed Metadata track")
			.put("auxv", "Auxiliary Video track")
			.build();

	public IsoAtom() {}

	public IsoAtom read(ExtendedRandomAccessFile in, long allowedLength) throws IOException {
		IntNode length = new IntNode().read(in, false);
		EnumNode<String> type = new EnumNode<String>(atomTypes, new FixedStringNode().read(in, 4, Charsets.US_ASCII));

		addChild("size", length);
		addChild("type", type);

		long atomlen = length.value();
		long datalen = 0;

		if (atomlen == 0) { // If zero, means read to end of file
			datalen = allowedLength - 8;
		} else if (atomlen == 1) {
			// TODO Handle length == 1 (means 64bit atom extension)
			throw new RuntimeException("We don't support atom 64 bit extension, yet");
		} else {
			datalen = atomlen - 8;
		}

		setTitle( type.name() + " (" + atomlen + " bytes)" );

		switch (type.value()) {
			case "ftyp" : readFileTypeAtom(in, datalen); break;
			case "moov" : readMovieAtom(in, datalen); break;
			case "mvhd" : readMovieHeaderAtom(in, datalen); break;
			case "trak" : readTrackAtom(in, datalen); break;
			case "tkhd" : readTrackHeaderAtom(in, datalen); break;
			case "mdia" : readMediaAtom(in, datalen); break;
			case "mdhd" : readMediaHeaderAtom(in, datalen); break;
			case "udta" : readUserDataAtom(in, datalen); break;
			case "hdlr" : readHandlerAtom(in, datalen); break;
			case "minf" : readMediaInformationAtom(in, datalen); break;
			case "edts" : readEditAtom(in, datalen); break;
			case "elst" : readEditListAtom(in, datalen); break;
			//vmhd
			//smhd
			//dinf
			//stbl

			//name
			//meta

			// MP4
			case "iods" : readInitialObjectDescriptorAtom(in, datalen); break;

			case "free": case "skip":
			default: readUnknown(in, datalen);
		}

		// Safety check to ensure we get to the right byte
		long atomend = getStart() + atomlen;
		if (in.getFilePointer() < atomend ) {
			addChild("unknown", new SkipNode().read(in, atomend - in.getFilePointer()) );

		} else if (in.getFilePointer() > atomend) {
			throw new IllegalArgumentException("Something went wrong parsing atom '" + type.value() + "'. We ended in the wrong position!");
		}

		return this;
	}

	protected TimestampNode timestamp(Node<? extends Number> node) {
		return new TimestampNode(node, TimeUnit.SECONDS, SINCE_JAN_1904);
	}

	protected void readMovieAtom(ExtendedRandomAccessFile in, long length) throws IOException {
		addChild("atoms", new IsoAtoms().read(in, length) );
	}

	protected void readTrackAtom(ExtendedRandomAccessFile in, long length) throws IOException {
		addChild("atoms", new IsoAtoms().read(in, length) );
	}

	protected void readUserDataAtom(ExtendedRandomAccessFile in, long length) throws IOException {
		addChild("atoms", new IsoAtoms().read(in, length) );
	}

	protected void readMediaAtom(ExtendedRandomAccessFile in, long length) throws IOException {
		addChild("atoms", new IsoAtoms().read(in, length) );
	}

	protected void readMediaInformationAtom(ExtendedRandomAccessFile in, long length) throws IOException {
		addChild("atoms", new IsoAtoms().read(in, length) );
	}

	protected void readEditAtom(ExtendedRandomAccessFile in, long length) throws IOException {
		addChild("atoms", new IsoAtoms().read(in, length) );
	}


	private void readInitialObjectDescriptorAtom(ExtendedRandomAccessFile in, long length) throws IOException {
		ByteNode version = addChild("version", new ByteNode().read(in));
		addChild("flags", new MaskNode(new NumberNode().read(in, 3)));

		// TODO Complete the other stuff
	}

	private void readEditListAtom(ExtendedRandomAccessFile in, long length) throws IOException {
		ByteNode version = addChild("version", new ByteNode().read(in));
		addChild("flags", new MaskNode(new NumberNode().read(in, 3)));

		IntNode count = addChild("entry_count", new IntNode().read(in));

		if (count.value() > 0) {
			ArrayNode list = addChild("list", new ArrayNode());
			for (int i = 1; i <= count.value(); i++) {
				TreeNode entry = new TreeNode().read(in);
				if (version.value() == 0) {
					entry.addChild("segment_duration", new IntNode().read(in));
					entry.addChild("media_time", new IntNode().read(in));

				} else if (version.value() == 1) {
					entry.addChild("segment_duration", new LongNode().read(in));
					entry.addChild("media_time", new LongNode().read(in));

				} else {
					// Unhandled version
					addChild("unsupported version", new SkipNode().read(in, length - 4));
					return;
				}

				entry.addChild("media_rate_integer", new ShortNode().read(in));
				entry.addChild("media_rate_fraction", new ShortNode().read(in));

				list.addChild(entry);
			}
		}
	}

	private void readHandlerAtom(ExtendedRandomAccessFile in, long length) throws IOException {
		addChild("version", new ByteNode().read(in));
		addChild("flags", new MaskNode(new NumberNode().read(in, 3)));

		addChild("pre_defined", new IntNode().read(in));
		addChild("handler_type", new EnumNode(handlerTypes, new FixedStringNode().read(in, 4)));
		addChild("reserved", new IntNode().read(in));
		addChild("reserved", new IntNode().read(in));
		addChild("reserved", new IntNode().read(in));
		addChild("name", new NullStringNode().read(in));
	}

	private void readMediaHeaderAtom(ExtendedRandomAccessFile in, long length) throws IOException {
		ByteNode version = addChild("version", new ByteNode().read(in));
		addChild("flags", new MaskNode(new NumberNode().read(in, 3)));

		if (version.value() == 0) {
			// in seconds since	midnight, Jan. 1, 1904, in UTC time
			addChild("creation_time", timestamp(new IntNode().read(in)) );
			addChild("modification_time", timestamp(new IntNode().read(in)));
			addChild("timescale", new IntNode().read(in)); // fps
			addChild("duration", new IntNode().read(in)); // in number of timescale units
		} else if (version.value() == 1) {
			addChild("creation_time", timestamp(new LongNode().read(in)));
			addChild("modification_time", timestamp(new LongNode().read(in)));
			addChild("timescale", new IntNode().read(in)); // fps
			addChild("duration", new LongNode().read(in)); // in number of timescale units
		} else {
			// Unhandled version
			addChild("unsupported version", new SkipNode().read(in, length - 4));
			return;
		}

		addChild("language", new ShortNode().read(in)); // TODO Packed ISO-639-2/T language code
		addChild("pre_defined", new ShortNode().read(in));
	}

	protected void readFileTypeAtom(ExtendedRandomAccessFile in, long length) throws IOException {
		EnumNode<String> brand  = new EnumNode<String>(atomBrands, new FixedStringNode().read(in, 4, Charsets.US_ASCII));
		addChild("major_brand", brand );
		addChild("minor_version", new IntNode().read(in, false) );

		if (length > 8) {
			ArrayNode brands = addChild("compatible_brands", new ArrayNode().read(in));
			length -= 8;
			while (length >= 4) {
				brands.addChild(new EnumNode<String>(atomBrands, new FixedStringNode().read(in, 4, Charsets.US_ASCII)));
				length -= 4;
			}
		}
	}

	protected void readTrackHeaderAtom(ExtendedRandomAccessFile in, long length) throws IOException {
		ByteNode version = addChild("version", new ByteNode().read(in));
		addChild("flags", new MaskNode(new NumberNode().read(in, 3)));
		IntNode trackId;

		if (version.value() == 0) {
			addChild("creation_time", timestamp(new IntNode().read(in)));
			addChild("modification_time", timestamp(new IntNode().read(in)));
			trackId = addChild("track_ID", new IntNode().read(in));
			addChild("reserved", new IntNode().read(in));
			addChild("duration", new IntNode().read(in));
		} else if (version.value() == 1) {
			addChild("creation_time", timestamp(new LongNode().read(in)));
			addChild("modification_time", timestamp(new LongNode().read(in)));
			trackId = addChild("track_ID", new IntNode().read(in));
			addChild("reserved", new IntNode().read(in));
			addChild("duration", new LongNode().read(in));
		} else {
			// Unhandled version
			addChild("unsupported version", new SkipNode().read(in, length - 4));
			return;
		}

		addChild("reserved", new IntNode().read(in));
		addChild("reserved", new IntNode().read(in));
		addChild("layer", new ShortNode().read(in));
		addChild("alternate_group", new ShortNode().read(in));
		addChild("volume", new ShortNode().read(in));
		addChild("reserved", new ShortNode().read(in));
		ArrayNode matrix = addChild("matrix", new ArrayNode().read(in));
		for (int i = 0; i < 9; i++) {
			matrix.addChild( new IntNode().read(in) );
		}
		IntNode width  = addChild("width", new IntNode().read(in));  // 16.16 Fixed point
		IntNode height = addChild("height", new IntNode().read(in)); // 16.16 Fixed point

		setTitle("Track " + trackId.value()); // + " " + width.value() + "x" + height);
	}

	protected void readMovieHeaderAtom(ExtendedRandomAccessFile in, long length) throws IOException {
		ByteNode version = addChild("version", new ByteNode().read(in));
		addChild("flags", new MaskNode(new NumberNode().read(in, 3)));

		if (version.value() == 0) {
			addChild("creation_time", timestamp(new IntNode().read(in)));
			addChild("modification_time", timestamp(new IntNode().read(in)));
			addChild("timescale", new IntNode().read(in));
			addChild("duration", new IntNode().read(in));
		} else if (version.value() == 1) {
			addChild("creation_time", timestamp(new LongNode().read(in)));
			addChild("modification_time", timestamp(new LongNode().read(in)));
			addChild("timescale", new IntNode().read(in));
			addChild("duration", new LongNode().read(in));
		} else {
			// Unhandled version
			addChild("unsupported version", new SkipNode().read(in, length - 4));
			return;
		}

		addChild("rate", new IntNode().base(16).read(in)); // TODO This is fixed point 16.16 number, 0x0001 0000 is 1.0
		addChild("volume", new ShortNode().base(16).read(in)); // TODO This is a fixed point 8.8

		addChild("reserved", new ShortNode().read(in));
		addChild("reserved", new IntNode().read(in));
		addChild("reserved", new IntNode().read(in));

		ArrayNode matrix = addChild("matrix", new ArrayNode().read(in));
		for (int i = 0; i < 9; i++) {
			matrix.addChild( new IntNode().read(in) );
		}

		ArrayNode pre_defined = addChild("pre_defined", new ArrayNode().read(in));
		for (int i = 0; i < 6; i++) {
			pre_defined.addChild( new IntNode().read(in) );
		}

		addChild("next_track_ID", new IntNode().read(in));
	}

	protected void readUnknown(ExtendedRandomAccessFile in, long length) throws IOException {
	}
}

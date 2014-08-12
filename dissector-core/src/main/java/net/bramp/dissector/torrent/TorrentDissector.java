package net.bramp.dissector.torrent;

import com.google.common.base.Charsets;
import com.sun.javaws.exceptions.InvalidArgumentException;
import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.*;
import net.bramp.dissector.png.PngChunkNode;
import net.bramp.dissector.png.PngHeaderNode;

import java.io.EOFException;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkState;

/**
 * Bencoding Dissector, the encodings used for Torrent files
 * @author bramp
 */
public class TorrentDissector extends Dissector {

    public TorrentDissector() {}

	public TorrentDissector read(ExtendedRandomAccessFile in) throws IOException {

		try {
			addChild("", readNode(in));

		} catch (EOFException eof) {
			// Ignore
			// TODO FIX
		}

		return this;
	}


	public static Node readNode(ExtendedRandomAccessFile in) throws IOException {

		char c = (char) in.peek();
		switch (c) {
			case 'e':
				return null;

			case 'd':
				return new BecodedDictNode().read(in);

			case 'i':
				return new BecodedIntegerNode().read(in);

			case 'l':
				return new BecodedListNode().read(in);

			case '0': case '1': case '2': case '3': case '4':
			case '5': case '6': case '7': case '8': case '9':
				return new BecodedStringNode().read(in);

			default:
				throw new IllegalArgumentException("Invalid prefix '" + c + "'");
		}
	}

	private static class BecodedDictNode extends TreeNode {

		public BecodedDictNode read(ExtendedRandomAccessFile in) throws IOException {
			addChild("prefix", new FixedStringNode(1).read(in)); // Should be d
			TreeNode data = addChild("items", new TreeNode().read(in));

			int count = 0;
			while (true) {
				Node key = readNode(in);
				if (key == null) {
					addChild("trailer", new FixedStringNode(1).read(in)); // Should be e
					break;
				}

				count++;

				String keyName;
				if (key instanceof BecodedStringNode) {
					keyName = ((BecodedStringNode) key).stringValue();
				} else {
					// Invalid key, it should be of type BecodedStringNode
					keyName = "invalid_key";
				}

				TreeNode node = data.addChild(keyName, new TreeNode());
				node.addChild("key", key);
				node.addChild("value", readNode(in));

			}

			setTitle("dict " + count + " items");

			return this;
		}
	}

	private static class BecodedListNode extends ArrayNode {

		public BecodedListNode read(ExtendedRandomAccessFile in) throws IOException {
			addChild("prefix", new FixedStringNode(1).read(in)); // Should be d
			ArrayNode data = addChild("list", new ArrayNode().read(in));

			int count = 0;
			while (true) {
				Node value = readNode(in);
				if (value == null) {
					addChild("trailer", new FixedStringNode(1).read(in)); // Should be e
					break;
				}
				count++;
				data.addChild(value);
			}

			setTitle("list " + count + " items");

			return this;
		}
	}


	private static class BecodedIntegerNode extends TreeNode {
		public BecodedIntegerNode read(ExtendedRandomAccessFile in) throws IOException {
			addChild("prefix", new FixedStringNode(1).read(in)); // Should be i

			NullStringNode value = addChild("value", new NullStringNode('e', false).read(in));

			addChild("trailer", new FixedStringNode(1).read(in)); // Should be e

			setTitle("int " + value.value());

			return this;
		}
	}


	private static class BecodedStringNode extends TreeNode {

		String str;

		public BecodedStringNode read(ExtendedRandomAccessFile in) throws IOException {
			NullStringNode lenNode = addChild("length", new NullStringNode(':', false).read(in));

			Node sep = addChild("seperator", new FixedStringNode(1).read(in));
			checkState(sep.value().equals(":"), "Expected ':'");

			long len = Long.parseLong(lenNode.value());

			FixedStringNode value = addChild("value", new FixedStringNode(len).read(in));
			str = value.value();

			setTitle("string '" + str + "'");

			return this;
		}

		public String stringValue() {
			return str;
		}
	}

}

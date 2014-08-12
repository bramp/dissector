package net.bramp.dissector.io;

import sun.misc.IoTrace;
import sun.nio.ch.FileChannelImpl;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Extends {@link java.io.RandomAccessFile} adding a few extra helpful methods
 *
 * TODO Consider replacing this with a MemoryMapped solution
 * @author bramp
 */
public class ExtendedRandomAccessFile implements DataInput {

	final RandomAccessFile file;

	protected ByteOrder endian = ByteOrder.BIG_ENDIAN;

	public ExtendedRandomAccessFile(String filename, String mode) throws FileNotFoundException {
        this.file = new RandomAccessFile(filename, mode);
    }

    public ExtendedRandomAccessFile(File file, String mode) throws FileNotFoundException {
	    this.file = new RandomAccessFile(file, mode);
    }

	public void setEndian(ByteOrder endian) {
		checkArgument(endian == ByteOrder.BIG_ENDIAN || endian == ByteOrder.LITTLE_ENDIAN, "Only big and little endian supported");
		this.endian = endian;
	}

    /**
     * Read a three byte int
     * @return
     * @throws IOException
     */
    public int readUnsigned3Int() throws IOException {
	    if (endian == ByteOrder.BIG_ENDIAN)
	        return (readUnsignedByte() << 24) | (readUnsignedByte() << 16) | readUnsignedByte();
	    else
		    return readUnsignedByte() | (readUnsignedByte() << 16) | (readUnsignedByte() << 24);
    }

	/**
	 * Read a three byte int
	 * @return
	 * @throws IOException
	 */
	public int read3Int() throws IOException {
		throw new RuntimeException("Not implemented");
	}

    /**
     *
     * @return
     * @throws IOException
     */
    public long readUnsignedInt() throws IOException {
	    if (endian == ByteOrder.BIG_ENDIAN)
            return ((long)readUnsignedShort() << 16) | (long)readUnsignedShort();
	    else
		    return (long)readUnsignedShort() | ((long)readUnsignedShort() << 16);
    }


    /**
     * @param length
     * @return
     * @throws IOException
     */
    public long readUnsignedIntOfLength(int length) throws IOException {
        if (length == 1)
            return readUnsignedByte();
        else if (length == 2)
            return readUnsignedShort();
        else if (length == 3)
            return readUnsigned3Int();
        else if (length == 4)
            return readUnsignedInt();
        else
	        throw new IllegalArgumentException("Invalid value for length " + length);
    }

	/**
	 * Signed int of length
	 * @param length
	 * @return
	 * @throws IOException
	 */
	public int readIntOfLength(int length) throws IOException {
		if (length == 1)
			return readByte();
		else if (length == 2)
			return readShort();
		else if (length == 3)
			return read3Int();
		else if (length == 4)
			return readInt();
		else
			throw new IllegalArgumentException("Invalid value for length " + length);
	}

	/**
	 * Peeks the next byte
	 * @return
	 */
	public int peek() throws IOException {
		long pos = file.getFilePointer();
		try {
			return read();
		} finally {
			seek(pos);
		}
	}


	public void skipBytes(long length) throws IOException {
		// The standard Java RandomAccessFile.skipBytes doesn't support skip with a long!
		file.seek(this.getFilePointer() + length);
	}

	/**
	 * Rewinds the file by i bytes
	 * @param i
	 */
	public void rewind(long i) throws IOException {
		file.seek(this.getFilePointer() - i);
	}


	public FileDescriptor getFD() throws IOException {
		return file.getFD();
	}

	public FileChannel getChannel() {
		return file.getChannel();
	}

	public void close() throws IOException {
		file.close();
	}

	public int read(byte[] b) throws IOException {
		return file.read(b);
	}

	public long getFilePointer() throws IOException {
		return file.getFilePointer();
	}

	public int read() throws IOException {
		return file.read();
	}

	public void setLength(long newLength) throws IOException {
		file.setLength(newLength);
	}

	public long length() throws IOException {
		return file.length();
	}

	public int read(byte[] b, int off, int len) throws IOException {
		return file.read(b, off, len);
	}

	public void seek(long pos) throws IOException {
		file.seek(pos);
	}

	@Override
	public void readFully(byte[] b) throws IOException {
		file.readFully(b);
	}

	@Override
	public void readFully(byte[] b, int off, int len) throws IOException {
		file.readFully(b, off, len);
	}

	@Override
	public int skipBytes(int n) throws IOException {
		return file.skipBytes(n);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return file.readBoolean();
	}

	@Override
	public byte readByte() throws IOException {
		return file.readByte();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return file.readUnsignedByte();
	}

	@Override
	public short readShort() throws IOException {
		littleEndianNotSupportedCheck();
		return file.readShort();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		if (endian == ByteOrder.BIG_ENDIAN)
			return file.readUnsignedShort();
		else {
			int ch1 = file.read();
			int ch2 = file.read();
			if ((ch1 | ch2) < 0)
				throw new EOFException();
			return (ch1 << 0) + (ch2 << 8);
		}
	}

	protected void littleEndianNotSupportedCheck() {
		if (endian != ByteOrder.BIG_ENDIAN)
			throw new RuntimeException("Little endian not implemented yet");
	}

	@Override
	public char readChar() throws IOException {
		littleEndianNotSupportedCheck();
		return file.readChar();
	}

	@Override
	public int readInt() throws IOException {
		littleEndianNotSupportedCheck();
		return file.readInt();
	}

	@Override
	public long readLong() throws IOException {
		littleEndianNotSupportedCheck();
		return file.readLong();
	}

	@Override
	public float readFloat() throws IOException {
		littleEndianNotSupportedCheck();
		return file.readFloat();
	}

	@Override
	public double readDouble() throws IOException {
		littleEndianNotSupportedCheck();
		return file.readDouble();
	}

	@Override
	public String readLine() throws IOException {
		littleEndianNotSupportedCheck();
		return file.readLine();
	}

	@Override
	public String readUTF() throws IOException {
		littleEndianNotSupportedCheck();
		return file.readUTF();
	}
}

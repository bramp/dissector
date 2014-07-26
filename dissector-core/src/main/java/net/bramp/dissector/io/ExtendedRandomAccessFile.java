package net.bramp.dissector.io;

import java.io.*;

/**
 * Extends RandomAccessFile adding a few extra helpful methods
 *
 * TODO Consider replacing this with a MemoryMapped solution
 * @author bramp
 */
public class ExtendedRandomAccessFile extends RandomAccessFile {

    public ExtendedRandomAccessFile(String filename, String mode) throws FileNotFoundException {
        super( filename, mode );
    }

    public ExtendedRandomAccessFile(File file, String mode) throws FileNotFoundException {
        super( file, mode );
    }

    /**
     * Read a three byte int
     * @return
     * @throws IOException
     */
    public int readUnsigned3Int() throws IOException {
        throw new RuntimeException("Not implemented");
    }

    /**
     *
     * @return
     * @throws IOException
     */
    public long readUnsignedInt() throws IOException {
        return ((long)readUnsignedShort() << 16) | (long)readUnsignedShort();
    }


    /**
     * @param length
     * @return
     * @throws IOException
     */
    public int readUnsignedIntOfLength(int length) throws IOException {
        if (length == 1)
            return readUnsignedByte();
        else if (length == 2)
            return readUnsignedShort();
        else if (length == 3)
            return readUnsigned3Int();
        else if (length == 4)
            return (int)readUnsignedInt();
        else
            throw new IllegalArgumentException("Invalid value for length");
    }

    public void skipBytes(long length) throws IOException {
        // The standard Java RandomAccessFile.skipBytes doesn't support skip with a long!
        this.seek( this.getFilePointer() + length );
    }
}

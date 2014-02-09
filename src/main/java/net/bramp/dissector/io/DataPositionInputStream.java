package net.bramp.dissector.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author bramp
 */
public class DataPositionInputStream extends DataInputStream {

    public DataPositionInputStream(InputStream in) {
        super( new PositionInputStream(in) );
    }

    public synchronized long getPosition() {
       return ((PositionInputStream)in).getPosition();
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
}
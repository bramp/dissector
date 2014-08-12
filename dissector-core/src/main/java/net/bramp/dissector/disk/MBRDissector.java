package net.bramp.dissector.disk;

import net.bramp.dissector.io.ExtendedRandomAccessFile;
import net.bramp.dissector.node.*;

import java.io.IOException;

/**
 * Structure of a modern standard MBR
 *
 * @author bramp
 */
public class MBRDissector extends Dissector {

    public MBRDissector() {}

    public MBRDissector read(ExtendedRandomAccessFile in) throws IOException {

        addChild( "Boostrap code", new SkipNode(218).read(in) );

        // TODO Make this optional, and exend the previous area
        addChild( "Magic", new ShortNode().read(in) ); // 0000h
        addChild( "Original physical drive", new ByteNode().read(in) );
        addChild( "Seconds", new ByteNode().read(in) );
        addChild( "Minutes", new ByteNode().read(in) );
        addChild( "Hours", new ByteNode().read(in) );

        addChild( "Boostrap code", new SkipNode(216).read(in) );

        addChild( "Disk signature", new IntNode().read(in).base(16) );
        addChild( "Magic", new ShortNode().read(in) ); // 0000h

        addChild( "Partition 1", new PartitionEntryNode().read(in) );
        addChild( "Partition 2", new PartitionEntryNode().read(in) );
        addChild( "Partition 3", new PartitionEntryNode().read(in) );
        addChild( "Partition 4", new PartitionEntryNode().read(in) );

        addChild( "Boot signature", new ShortNode().read(in) );

        return this;
    }
}
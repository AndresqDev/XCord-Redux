package net.md_5.bungee.jni.zlib;

import io.netty.buffer.ByteBuf;
import java.util.zip.DataFormatException;
import me.minebuilders.natives.compression.LibNativeDeflate;
import me.minebuilders.natives.compression.LibNativeInflate;

public class XCordNativeZLib implements BungeeZlib {
    private final LibNativeInflate nativeInflate = new LibNativeInflate();
    private final LibNativeDeflate nativeDeflate = new LibNativeDeflate();
    private long inflateContext;
    private long deflateContext;

    @Override
    public boolean requiresDirectMemory() {
        return true;
    }

    @Override
    public void init(boolean direct, int compressionLevel) {
        this.inflateContext = this.nativeInflate.init();
        this.deflateContext = this.nativeDeflate.init(compressionLevel);
    }

    @Override
    public void free() {
        if (this.inflateContext != 0L) {
            this.nativeInflate.free(this.inflateContext);
            this.nativeDeflate.free(this.deflateContext);
            this.inflateContext = 0L;
        }
    }

    @Override
    public void inflate(ByteBuf input, ByteBuf output, int uncompressedSize) throws DataFormatException {
        input.memoryAddress();
        output.memoryAddress();
        output.ensureWritable(uncompressedSize);
        this.nativeInflate.run(
            this.inflateContext,
            input.memoryAddress() + (long) input.readerIndex(),
            input.readableBytes(),
            output.memoryAddress() + (long) output.writerIndex(),
            uncompressedSize
        );
        output.writerIndex(output.writerIndex() + uncompressedSize);
    }

    @Override
    public void deflate(ByteBuf input, ByteBuf output) throws DataFormatException {
        while (true) {
            int written = this.nativeDeflate.run(
                this.deflateContext,
                input.memoryAddress() + (long) input.readerIndex(),
                input.readableBytes(),
                output.memoryAddress() + (long) output.writerIndex(),
                output.writableBytes()
            );
            if (written > 0) {
                output.writerIndex(output.writerIndex() + written);
                return;
            }
            output.capacity(output.capacity() * 2);
        }
    }
}

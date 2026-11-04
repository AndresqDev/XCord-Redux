package me.minebuilders.natives.compression;

import java.util.zip.DataFormatException;

public class LibNativeDeflate {
    public native long init(int compressionLevel);

    public native long free(long context);

    public native int run(long context, long inputAddress, int inputLength, long outputAddress, int outputLength) throws DataFormatException;
}

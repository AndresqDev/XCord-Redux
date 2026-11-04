package me.minebuilders.natives.compression;

import java.util.zip.DataFormatException;

public class LibNativeInflate {
    public native long init();

    public native long free(long context);

    public native boolean run(long context, long inputAddress, int inputLength, long outputAddress, int outputLength) throws DataFormatException;
}

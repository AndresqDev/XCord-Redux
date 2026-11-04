package me.minebuilders.natives.compression;

import java.util.zip.DataFormatException;

public class LibNativeDeflate {
    public native long init(int var1);

    public native long free(long var1);

    public native int run(long var1, long var3, int var5, long var6, int var8) throws DataFormatException;
}


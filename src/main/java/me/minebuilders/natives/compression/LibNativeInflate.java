package me.minebuilders.natives.compression;

import java.util.zip.DataFormatException;

public class LibNativeInflate {
    public native long init();

    public native long free(long var1);

    public native boolean run(long var1, long var3, int var5, long var6, int var8) throws DataFormatException;
}


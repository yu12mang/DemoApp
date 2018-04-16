package com.jniutils;

public class JniUtils {
    static {
        System.loadLibrary("native-lib");
    }

    public  native static byte[] pcm2G711(byte[] bytes);
}

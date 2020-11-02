package ru.yandex.fixcolor.tests.spc.lib;

public class Converts {
    public static void IntToBytes(int source, byte[] target, int lenghtSource, int offsetTarget) {
        int i = 0;
        do {
            target[offsetTarget + i] = (byte) (source & 0xff);
            i++;
            if (i >= lenghtSource) return;
            source >>= 8;
        } while (true);
    }
    public static long bytesToLong(byte[] source, final int lenght, final int offsetSource) {
        long target = 0;
        for (int i = 0; i < lenght; i++) {
            target += ((source[offsetSource + i] & 0x000000ff) << (i * 8));
        }
        return target;
    }
    public static int bytesToInt(final byte[] source, final int lenght, final int offsetSource) {
        int target = 0;
        for (int i = 0; i < lenght; i++) {
            target += ((source[offsetSource + i] & 0x000000ff) << (i * 8));
        }
        return target;
    }
}

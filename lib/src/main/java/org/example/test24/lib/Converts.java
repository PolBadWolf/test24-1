package org.example.test24.lib;

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
}

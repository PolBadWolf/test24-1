package ru.yandex.fixcolor.tests.spc.lib.plot2;

public class SerialKofficent {
    public static final int[] serialLineage1 = {10, 11, 9, 12, 8, 13, 7, 14, 6, 15, 5, 16, 4, 17, 3, 18, 2, 19, 1, 20};
    public static final int[] serialLineage2 = {1, 2, 5, 10, 20, 25, 50, 100, 200};
    public static class TwoKof {
        public int k_before;
        public int k_after;
        public TwoKof(int k_before, int k_after) {
            this.k_before = k_before;
            this.k_after = k_after;
        }
    }
    public static TwoKof findKoff(double x) {
        boolean illigal = true;
        TwoKof zn = null;
        if (x >= 1) {
            for (int i = 1; i < serialLineage2.length; i++) {
                if (x <= serialLineage2[i]) {
                    illigal = false;
                    zn = new TwoKof(serialLineage2[i - 1], serialLineage2[i]);
                    break;
                }
            }
        }
        if (illigal) throw new IllegalArgumentException("за пределелы диапозона: x=" + x);
        return zn;
    }
}

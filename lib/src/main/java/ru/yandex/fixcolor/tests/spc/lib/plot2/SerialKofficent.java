package ru.yandex.fixcolor.tests.spc.lib.plot2;

public class SerialKofficent {
    public static class StructFindK_Div {
        public int min;
        public int max;
        public int div;
        public int base;
        public StructFindK_Div(int min, int max, int div, int base) {
            this.min = min;
            this.max = max;
            this.div = div;
            this.base = base;
        }

        @Override
        public String toString() {
            return "ResultFindKdev{" +
                    "min=" + min +
                    ", max=" + max +
                    ", div=" + div +
                    ", multiplicity=" + base +
                    '}';
        }
    }
    public static final int[] serialMultiplicity = {1, 2, 5, 10, 20, 25, 50, 100, 200, 250, 500, 1000, 2000, 2500, 5000};
    static class TwoKof {
        public double k_min;
        public double k_max;
        public TwoKof(double k_min, double k_max) {
            this.k_min = k_min;
            this.k_max = k_max;
        }
    }
    static TwoKof findKoff(double x) {
        boolean illigal = true;
        TwoKof zn = null;
        if (x >= 1) {
            for (int i = 1; i < serialMultiplicity.length; i++) {
                if (x <= serialMultiplicity[i]) {
                    illigal = false;
                    zn = new TwoKof(serialMultiplicity[i - 1], serialMultiplicity[i]);
                    break;
                }
            }
        }
        if (illigal) throw new IllegalArgumentException("за пределелы диапозона: x=" + x);
        return zn;
    }
    public static StructFindK_Div findK_Div(double min, double max) {
        double len = max - min;
        int multiplicity = 1;
        boolean fail = true;
        {
            int divOld, divCur, divFut;
            int subOld, subCur, subFut;
            divOld = (int) Math.ceil(len / serialMultiplicity[0]);
            subOld = Math.abs(divOld - 10);
            divCur = (int) Math.ceil(len / serialMultiplicity[1]);
            subCur = Math.abs(divCur - 10);
            for (int i = 1; i < serialMultiplicity.length - 1; i++) {
                multiplicity = serialMultiplicity[i];
                divFut = (int) Math.ceil(len / serialMultiplicity[i+1]);
                subFut = Math.abs(divFut - 10);
                // ---
                System.out.println("k = "+multiplicity+"\t"+subOld+" > "+subCur+" < "+subFut);
                if ((subOld > subCur) && (subCur < subFut)) {
                    fail = false;
                    break;
                }
                // ---
                subOld = subCur;
                subCur = subFut;
                // ---
            }
        }
        if (fail) throw new IllegalArgumentException("ну не шмогла я найти ...");
        int rendMin = ((int) Math.floor(min / multiplicity)) * multiplicity;
        int rendMax = ((int) Math.ceil(max / multiplicity)) * multiplicity;
        int dev = (rendMax - rendMin) / multiplicity;
        return new StructFindK_Div(rendMin,rendMax,dev, multiplicity);
    }
    public static StructFindK_Div findK_Div(double min, double max, int base) {

        return new StructFindK_Div((int)min, (int)max, 1, base);
    }
}

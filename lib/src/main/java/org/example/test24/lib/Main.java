
import java.util.*;

public class Main {
    FFFF[] sss = new FFFF[] {
            new FFFF("agg", 193),
            new FFFF("as", 126),
            new FFFF("asb", 173),
            new FFFF("asd", 123),
            new FFFF("asx", 113),
            new FFFF("qwe", 124),
            new FFFF("uus4", 823),
            new FFFF("wsx", 153),
            new FFFF("zd", 120),
    };



    public static void main(String[] args) {
        new Main().start();
    }
    private void start() {
        String query = "u96";
        FFFF[] ssoorrtt = Arrays.stream(sss).sorted((o1, o2) -> o1.name.compareTo(o2.name)).toArray(FFFF[]::new);
        int res = extractAll(query, ssoorrtt);
        System.out.println("find: \"" + query + "\" : index = " + res + " / " + ssoorrtt[res].toString() + " / " + ssoorrtt[res].code);
    }

    private <T> int extractAll(String query, T[] choices) {
        int index = 0;
        double min = Double.MAX_VALUE;
        double r;
        for (int i = 0; i < choices.length; i++) {
            r = comp(query, choices[i].toString());
            if (min > r) {
                min = r;
                index = i;
            }
        }
        return index;
    }

    private double comp(String s1, String s2) {
        int l1 = s1.length(), l2 = s2.length(), min = Math.min(l1, l2);
        double sum = 0;
        double k;
        for (int i = 0; i < min; i++) {
            k = 1 / (128 * (i + 1));
            sum += Math.abs(s1.substring(i, 1).hashCode() - s2.substring(i, 1).hashCode()) / k;
        }
        return sum;
    }
    class FFFF {
        String name;
        int code;

        public FFFF(String name, int code) {
            this.name = name;
            this.code = code;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}

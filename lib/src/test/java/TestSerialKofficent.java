import org.junit.Assert;
import org.junit.Test;
import ru.yandex.fixcolor.tests.spc.lib.plot2.SerialKofficent;

import static ru.yandex.fixcolor.tests.spc.lib.plot2.SerialKofficent.findKoff;

public class TestSerialKofficent {
    private class Trio {
        public double x;
        public int s;
        public int b;

        public Trio(double x, int s, int b) {
            this.x = x;
            this.s = s;
            this.b = b;
        }
    }

    @Test
    public void testFind() {
        Trio[] trios = {
                new Trio(12.5, 10, 20),
                new Trio(7.35, 5, 10),
                new Trio(18.5, 10, 20),
                new Trio(38.5, 25, 50),
                new Trio(76.7, 50, 100)
        };
        SerialKofficent.TwoKof znIn;
        boolean allError = false;
        boolean flError;
        for (Trio trio: trios
             ) {
            znIn = findKoff(trio.x);
            flError = false;
            if (trio.s != znIn.k_before) flError = true;
            if (trio.b != znIn.k_after) flError = true;
            System.out.print("x = " + trio.x + " min= " + znIn.k_before + " max=" + znIn.k_after);
            if (flError) {
                System.out.print(" !!! min=" + trio.s + " max= " + trio.b);
                allError = true;
            }
            System.out.println();
        }
        Assert.assertTrue(!allError);
    }
}

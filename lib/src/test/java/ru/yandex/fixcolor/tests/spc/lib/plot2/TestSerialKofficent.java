package ru.yandex.fixcolor.tests.spc.lib.plot2;

import org.junit.Assert;
import org.junit.Test;

import static ru.yandex.fixcolor.tests.spc.lib.plot2.SerialKofficent.*;

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
    private class TrioDubl {
        public double min;
        public double max;
        public int div;

        public TrioDubl(double min, double max, int div) {
            this.min = min;
            this.div = div;
            this.max = max;
        }

        @Override
        public String toString() {
            return "TrioDubl{" +
                    "min=" + min +
                    ", max=" + max +
                    ", div=" + div +
                    '}';
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
            if (trio.s != znIn.k_min) flError = true;
            if (trio.b != znIn.k_max) flError = true;
            System.out.print("x = " + trio.x + " min= " + znIn.k_min + " max=" + znIn.k_max);
            if (flError) {
                System.out.print(" !!! min=" + trio.s + " max= " + trio.b);
                allError = true;
            }
            System.out.println();
        }
        Assert.assertTrue(!allError);
    }

    @Test
    public void testFindDiv() {
        TrioDubl[] mass = {
                new TrioDubl(-10, 1000, 11),
                new TrioDubl(0,276, 12),
                new TrioDubl(0, 385, 8),
                new TrioDubl(-124,698, 9),
                new TrioDubl(0,768, 8),
                new TrioDubl(0, 852, 9),
                new TrioDubl(0, 273, 9),
                new TrioDubl(0, 220, 12)
        };
        StructFindK_Div div2;
        boolean allOk = true;
        for (TrioDubl trio: mass) {
            System.out.println("============================");
            System.out.println(trio);
            div2 = findK_Div(trio.min, trio.max);
            //Assert.assertEquals(div.div, trio.div);
            System.out.println("res2 = "+div2);
        }
    }

    private class MassTestFindDivAdj {
        public StructFindK_Div dataInp;
        public StructFindK_Div dataOut;
        public MassTestFindDivAdj(StructFindK_Div dataInp, StructFindK_Div dataOut) {
            this.dataInp = dataInp;
            this.dataOut = dataOut;
        }
    }
    @Test
    public void testFindDivAdj() {
        MassTestFindDivAdj[] mass = {
                new MassTestFindDivAdj(
                        new StructFindK_Div(0, 273, 11, 100),
                        new StructFindK_Div(0, 275, 11, 25)
                ),
                new MassTestFindDivAdj(
                        new StructFindK_Div(0, 600,12, 5),
                        new StructFindK_Div(0, 23, 5, 2)
                )
        };
    }
}

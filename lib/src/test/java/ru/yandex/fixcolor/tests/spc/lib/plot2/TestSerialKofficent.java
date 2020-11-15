package ru.yandex.fixcolor.tests.spc.lib.plot2;

import org.junit.Assert;
import org.junit.Test;

public class TestSerialKofficent {
    @Test
    public void testMultiplicity() {
        double[] lines = {12, 18, 48, 53, 186, 154, 678, 1024, 852};
        System.out.println("цикл поисков К из ряда чисел:");
        for (int i = 0; i < lines.length; i++) {
            System.out.println(String.format("%3d", i+1) + ") x = " + String.format("%8.2f", lines[i]) + " -> step = " + String.format("%5d", MultiplicityRender.render.multiplicity(lines[i])) );
        }
    }
    static class TestSectionUnit {
        public double fist;
        public double end;
        public int sampleStep;
        public int sampleN;

        public TestSectionUnit(double fist, double end, int sampleStep, int sampleN) {
            this.fist = fist;
            this.end = end;
            this.sampleStep = sampleStep;
            this.sampleN = sampleN;
        }
    }
    @Test
    public void testMultiplicitySection() {
        TestSectionUnit[] units = {
                new TestSectionUnit(0, 52, 5, 11),
                new TestSectionUnit(-53, 726, 100, 9),
                new TestSectionUnit(0, 1024, 100, 11),
                new TestSectionUnit(0, 37, 5, 8),
                new TestSectionUnit(0, 32, 5, 7)
        };
        boolean fail = false;
        for (int i = 0; i < units.length; i++) {
            System.out.print(String.format("%4d", i) + ") ");
            double fist = units[i].fist;
            double end = units[i].end;
            int sampleStep = units[i].sampleStep;
            int sampleN = units[i].sampleN;
            System.out.print("fist = " + String.format("%9.2f", fist) + " ");
            System.out.print("end = "  + String.format("%9.2f", end) + " ");
            MultiplicityRender.Section section = MultiplicityRender.render.multiplicity(fist, end);
            System.out.print("--> ");
            System.out.print("step = "  + String.format("%3d", section.multiplicity) + " ");
            System.out.print("sample step = "  + String.format("%3d", sampleStep) + " ");
            System.out.print("fist = " + String.format("%4d", section.fist) + " ");
            System.out.print("end = "  + String.format("%4d", section.end) + " ");
            System.out.print("n = "  + String.format("%3d", section.n) + " ");
            System.out.print("sample n = "  + String.format("%3d", sampleN) + " ");
            System.out.println();
            if (sampleStep != section.multiplicity) fail = true;
            if (sampleN != section.n) fail = true;
        }
        Assert.assertFalse(fail);
    }
}

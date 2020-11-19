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
    static class TestSectionUnit2 {
        public double fist;
        public double end;
        public double fistTrend2;
        public double endTrend2;

        public TestSectionUnit2(double fistTrend1, double endTrend1, double fistTrend2, double endTrend2) {
            this.fist = fistTrend1;
            this.end= endTrend1;
            this.fistTrend2 = fistTrend2;
            this.endTrend2 = endTrend2;
        }
    }
    @Test
    public void testMultiplicitySection() {
        TestSectionUnit[] units = {
                new TestSectionUnit(0, 54, 5, 11),
                new TestSectionUnit(-2, 52, 5, 11),
                new TestSectionUnit(-7, 47, 5, 11),
                new TestSectionUnit(0, 52, 5, 11),
                new TestSectionUnit(0, 52, 5, 11),
                new TestSectionUnit(0, 779, 100, 8),
                new TestSectionUnit(-53, 726, 100, 8),
                new TestSectionUnit(-253, 526, 100, 8),
                new TestSectionUnit(0, 1024, 100, 11),
                new TestSectionUnit(0, 37, 5, 8),
                new TestSectionUnit(0, 32, 5, 7)
        };
        boolean fail = false;
        for (int i = 0; i < units.length; i++) {
            System.out.print(String.format("%4d", i+1) + ") ");
            double fist = units[i].fist;
            double end = units[i].end;
            int sampleStep = units[i].sampleStep;
            int sampleN = units[i].sampleN;
            System.out.print("fist = " + String.format("%9.2f", fist) + " ");
            System.out.print("end = "  + String.format("%9.2f", end) + " ");
            MultiplicityRender.Section section = MultiplicityRender.render.multiplicity(fist, end);
            System.out.print("--> ");
            System.out.print("step = "  + String.format("%3d", section.step) + " ");
            System.out.print("sample step = "  + String.format("%3d", sampleStep) + " ");
            System.out.print("fist = " + String.format("%4d", section.min) + " ");
            System.out.print("end = "  + String.format("%4d", section.max) + " ");
            System.out.print("n = "  + String.format("%3d", section.n) + " ");
            System.out.print("sample n = "  + String.format("%3d", sampleN) + " ");
            System.out.println();
            if (sampleStep != section.step) fail = true;
            if (sampleN != section.n) fail = true;
        }
        Assert.assertFalse(fail);
    }

    @Test
    public void testMultiplicitySectionT2() {
        TestSectionUnit2[] units = {
                new TestSectionUnit2(0, 54, 0, 230),
                new TestSectionUnit2(0, 54, -20, 210),
                new TestSectionUnit2(0, 54, -65, 165),
                new TestSectionUnit2(-2, 52, 0, 230),
                new TestSectionUnit2(-2, 52, -20, 210),
                new TestSectionUnit2(-2, 52, -65, 165),
                new TestSectionUnit2(-7, 47, 0, 230),
                new TestSectionUnit2(-7, 47, -20, 210),
                new TestSectionUnit2(-7, 47, -65, 165)
        };
        MultiplicityRender.Section[] sectionTrend1 = new MultiplicityRender.Section[units.length];
        MultiplicityRender.Section[] sectionTrend2 = new MultiplicityRender.Section[units.length];
        for (int i = 0; i < units.length; i++) {
            sectionTrend1[i] = MultiplicityRender.render.multiplicity(units[i].fist, units[i].end);
            sectionTrend2[i] = MultiplicityRender.render.multiplicityT2(sectionTrend1[i], units[i].fistTrend2, units[i].endTrend2);
        }
    }

}

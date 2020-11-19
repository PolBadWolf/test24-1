package ru.yandex.fixcolor.tests.spc.lib.plot2;

import org.junit.Assert;
import org.junit.Test;

public class StepTest2 {
    private class UnitDataTestIter1 {
        // начальное значение min
        public double begin;
        // начальное значение max
        public double end;
        // ----- ответ ----
        public int renderBegin;
        public int renderEnd;
        public int renderStep;
        public int renderN;

        public UnitDataTestIter1(double begin, double end, int renderBegin, int renderEnd, int renderStep, int renderN) {
            this.begin = begin;
            this.end = end;
            this.renderBegin = renderBegin;
            this.renderEnd = renderEnd;
            this.renderStep = renderStep;
            this.renderN = renderN;
        }
    }

    @Test
    public void iter1() {
        UnitDataTestIter1[] testDatas = {
                new UnitDataTestIter1(0, 80, 0, 80, 10, 8),
                new UnitDataTestIter1(-5, 97, -10, 100, 10, 11),
                new UnitDataTestIter1(23, 117, 20, 120, 10, 10)
        };
        MultiplicityRender.Section section;
        for (UnitDataTestIter1 testData : testDatas) {
            System.out.println("---------------------------------------");
            System.out.println("begin = " + testData.begin + "\t end = " + testData.end);
            System.out.println("ожидается/расчитано:");
            System.out.println("begin="+ testData.renderBegin + "\t end="+testData.renderEnd+"\t step="+testData.renderStep+"\t N="+testData.renderN);
            section = MultiplicityRender.render.multiplicity(testData.begin, testData.end);
            System.out.println("begin="+section.min+"\t end="+section.max+"\t step="+section.step+"\t N="+section.n);
            Assert.assertEquals("начальное значение", testData.renderBegin, section.min);
            Assert.assertEquals("конечьное значение", testData.renderEnd, section.max);
            Assert.assertEquals("шаг изменений", testData.renderStep, section.step);
            Assert.assertEquals("количество шагов", testData.renderN, section.n);
        }
    }

    private class UnitDataTestIter2 {
        // начальное значение tr1
        public double tr1_beg;
        public double tr1_end;
        // начальное значение tr1
        public double tr2_beg;
        public double tr2_end;
        // -------- tr1 -----------
        public int rnTr1_beg;
        public int rnTr1_end;
        public int rnTr1_step;
        public int rnTr1_n;
        // -------- tr2 -----------
        public int rnTr2_beg;
        public int rnTr2_end;
        public int rnTr2_step;
        public int rnTr2_n;

        public UnitDataTestIter2(double tr1_beg, double tr1_end, double tr2_beg, double tr2_end,
                                 int rnTr1_beg, int rnTr1_end, int rnTr1_step, int rnTr1_n,
                                 int rnTr2_beg, int rnTr2_end, int rnTr2_step, int rnTr2_n) {
            this.tr1_beg = tr1_beg;
            this.tr1_end = tr1_end;
            this.tr2_beg = tr2_beg;
            this.tr2_end = tr2_end;
            this.rnTr1_beg = rnTr1_beg;
            this.rnTr1_end = rnTr1_end;
            this.rnTr1_step = rnTr1_step;
            this.rnTr1_n = rnTr1_n;
            this.rnTr2_beg = rnTr2_beg;
            this.rnTr2_end = rnTr2_end;
            this.rnTr2_step = rnTr2_step;
            this.rnTr2_n = rnTr2_n;
        }
    }

    @Test
    public void iter2() {
        UnitDataTestIter2[] testDatas = {
                new UnitDataTestIter2(0, 97, 5, 141,
                        0, 150, 10, 15,
                        0, 150, 10, 15
                ),
                new UnitDataTestIter2(0, 97, 20, 100,
                        0, 100, 10, 10,
                        20, 120, 10, 10
                )
        };

        MultiplicityRender.Section section1, section2;
        for (UnitDataTestIter2 testData : testDatas) {
            System.out.println("---------------------------------------");
            System.out.print  ("tr1_beg = " + testData.tr1_beg + "\t tr1_end = " + testData.tr1_end+"\t ");
            System.out.println("tr2_beg = " + testData.tr2_beg + "\t tr2_end = " + testData.tr2_end);
            section1 = MultiplicityRender.render.multiplicity(testData.tr1_beg, testData.tr1_end);
            System.out.println("промежуточный расчет tr1:");
            System.out.println("rnd_beg="+ section1.min + "\t rnd_end="+section1.max+"\t rnd_step="+section1.step+"\t rnd_N="+section1.n);
            section2 = MultiplicityRender.render.multiplicityT2(section1, testData.tr2_beg, testData.tr2_end);
            System.out.println("ожидается/расчитано tr1:");
            System.out.println("sam_beg="+ testData.rnTr1_beg + "\t sam_end="+testData.rnTr1_end+"\t sam_step="+testData.rnTr1_step+"\t sam_N="+testData.rnTr1_n);
            System.out.println("rnd_beg="+ section1.min + "\t rnd_end="+section1.max+"\t rnd_step="+section1.step+"\t rnd_N="+section1.n);
            System.out.println("ожидается/расчитано tr2:");
            System.out.println("sam_beg="+ testData.rnTr2_beg + "\t sam_end="+testData.rnTr2_end+"\t sam_step="+testData.rnTr2_step+"\t sam_N="+testData.rnTr2_n);
            System.out.println("rnd_beg="+ section2.min + "\t rnd_end="+section2.max+"\t rnd_step="+section2.step+"\t rnd_N="+section2.n);
            // ------------------------
            Assert.assertEquals("tr1: начальное значение", testData.rnTr1_beg, section1.min);
            Assert.assertEquals("tr1: конечьное значение", testData.rnTr1_end, section1.max);
            Assert.assertEquals("tr1: шаг изменений", testData.rnTr1_step, section1.step);
            Assert.assertEquals("tr1: количество шагов", testData.rnTr1_n, section1.n);
            // ------------------------
            Assert.assertEquals("tr2: начальное значение", testData.rnTr2_beg, section2.min);
            Assert.assertEquals("tr2: конечьное значение", testData.rnTr2_end, section2.max);
            Assert.assertEquals("tr2: шаг изменений", testData.rnTr2_step, section2.step);
            Assert.assertEquals("tr2: количество шагов", testData.rnTr2_n, section2.n);
        }

    }
}

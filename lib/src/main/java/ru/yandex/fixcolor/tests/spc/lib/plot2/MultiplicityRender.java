package ru.yandex.fixcolor.tests.spc.lib.plot2;

public class MultiplicityRender {
    public static final MultiplicityRender render = new MultiplicityRender();
    private static class SectionUnit {
        public double section;
        public int multiplicity;

        public SectionUnit(double section, int multiplicity) {
            this.section = section;
            this.multiplicity = multiplicity;
        }
    }
    private static final SectionUnit[] sectionUnits = {
            new SectionUnit(16, 1),
            new SectionUnit(32, 2),
            new SectionUnit(70, 5),
            new SectionUnit(140, 10),
            new SectionUnit(350, 25),
            new SectionUnit(700, 50),
            new SectionUnit(1_400, 100),
            new SectionUnit(3_500, 250),
            new SectionUnit(7_000, 500),
            new SectionUnit(14_000, 1_000),
            new SectionUnit(35_000, 2_500),
            new SectionUnit(70_000, 5_000),
            new SectionUnit(140_000, 10_000),
            new SectionUnit(350_000, 25_000)
    };
    public static class Section {
        public int min;
        public int max;
        public int step;
        public int n;

        public Section(int fist, int end, int step, int n) {
            this.min = fist;
            this.max = end;
            this.step = step;
            this.n = n;
        }
    }
    public static class Steps {
        public int small;
        public int big;

        public Steps(int small, int big) {
            this.small = small;
            this.big = big;
        }

        public Steps() {
        }
    }
    int multiplicity(double x) {
        for (SectionUnit unit: sectionUnits) {
            if (x < unit.section) return unit.multiplicity;
        }
        throw new IllegalArgumentException("\"x\" вне диапозона : " + x);
    }
    int ceilStep(double baseStep) {
        for (SectionUnit unit: sectionUnits) {
            if (baseStep <= unit.multiplicity) return unit.multiplicity;
        }
        throw new IllegalArgumentException("\"x\" вне диапозона : " + baseStep);
    }
    void ceilStep(double baseStep, Steps steps) {
        for (SectionUnit unit: sectionUnits) {
            if (baseStep <= unit.multiplicity) {
                steps.big = unit.multiplicity;
                return;
            }
            steps.small = unit.multiplicity;
        }
        throw new IllegalArgumentException("\"x\" вне диапозона : " + baseStep);
    }
    public Section multiplicity(double fist, double end) throws IllegalArgumentException {
        double lenght = end - fist;
        int step = multiplicity(lenght);
        int nDn = (int) Math.floor(fist / step);
        int nUp = (int) Math.ceil(end / step);
        return new Section(
                nDn * step,
                nUp * step,
                step,
                nUp - nDn
        );
    }
    public Section multiplicityT2(final Section sectionTrend1, double fistTrend2, double endTrend2) {
        double baseLenght2 = endTrend2 - fistTrend2;
        double baseStep2 = baseLenght2 / sectionTrend1.n;
        Steps steps2 = new Steps();
        int tr2_nDn, tr2_nUp, tr1_nUp;
        int step;
        ceilStep(baseStep2, steps2);
        double smOst = ((baseStep2 - steps2.small) % steps2.small) / steps2.small;
        double bgOst = ((steps2.big - baseStep2) % steps2.big) / steps2.big;
        int n_tmp, n_max;
        // ---
        if (smOst < bgOst) step = steps2.small;
        else step = steps2.big;
        tr2_nDn = (int) Math.floor(fistTrend2 / step);
        tr2_nUp = (int) Math.ceil(endTrend2 / step);
        n_tmp = tr2_nUp - tr2_nDn;
        n_max = Math.max(n_tmp, sectionTrend1.n);
        tr2_nUp = n_max + tr2_nDn;
        tr1_nUp = n_max + (sectionTrend1.min / sectionTrend1.step);
        // cor tr1
        sectionTrend1.max = sectionTrend1.step * tr1_nUp;
        sectionTrend1.n = n_max;
        return new Section(
                step * tr2_nDn,
                step * tr2_nUp,
                step,
                n_max
        );
    }
}

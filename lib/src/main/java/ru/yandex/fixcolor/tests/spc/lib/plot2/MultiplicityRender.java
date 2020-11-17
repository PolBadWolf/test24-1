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
        public int fist;
        public int end;
        public int multiplicity;
        public int n;

        public Section(int fist, int end, int multiplicity, int n) {
            this.fist = fist;
            this.end = end;
            this.multiplicity = multiplicity;
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
        int nDn = (int) (fist / step);
        int nUp = (int) Math.ceil(end / step);
        return new Section(
                (int) Math.floor(fist),
                (int) Math.ceil(end),
                step,
                Math.abs(nDn) + Math.abs(nUp)
        );
    }
    public Section multiplicityT2(final Section sectionTrend1, double fistTrend2, double endTrend2) {
        int baseN = sectionTrend1.n;
        double baseLenght = endTrend2 - fistTrend2;
        double baseStep = baseLenght / baseN;
        Steps steps = new Steps();
        int step;
        ceilStep(baseStep, steps);
        double smOst = (baseStep % steps.small) / steps.small;
        double bgOst = (baseStep % steps.big) / steps.big;
        if (smOst < bgOst) {
            step = steps.small;
            baseN = (int) Math.ceil(baseLenght / step);
            baseLenght = step * baseN;
            endTrend2 = baseLenght + fistTrend2;
            int tmpLen = (int) (baseN * sectionTrend1.multiplicity);
            sectionTrend1.end = tmpLen + sectionTrend1.fist;
        } else {
            step = steps.big;
            endTrend2 = step * baseN;
            int tmpLen = (int) (baseN * sectionTrend1.multiplicity);
            sectionTrend1.end = tmpLen + sectionTrend1.fist;
        }
        //
        int celDnTr1 = (int) (sectionTrend1.fist / sectionTrend1.multiplicity);
        int celDnTr2 = (int) (fistTrend2 / step);
        double otnDnTr1 = (double) (sectionTrend1.fist % sectionTrend1.multiplicity) / sectionTrend1.multiplicity;
        double otnDnTr2 = (double) (fistTrend2 % step) / step;
        double otnDn = Math.min(otnDnTr1, otnDnTr2);
        int corDnTr1 = (int) Math.floor(sectionTrend1.multiplicity * (celDnTr1 + otnDn));
        int corDnTr2 = (int) Math.floor(step * (celDnTr2 + otnDn));
        //
        int celUpTr1 = (int) (sectionTrend1.end / sectionTrend1.multiplicity);
        int celUpTr2 = (int) (endTrend2 / step);
        double otnUpTr1 = (double) (sectionTrend1.end % sectionTrend1.multiplicity) / sectionTrend1.multiplicity;
        double otnUpTr2 = (double) (endTrend2 % step) / step;
        double otnUp = Math.max(otnUpTr1, otnUpTr2);
        int corUpTr1 = (int) Math.ceil(sectionTrend1.multiplicity * (celUpTr1 + otnUp));
        int corUpTr2 = (int) Math.ceil(step * (celUpTr2 + otnUp));
        //
        sectionTrend1.fist = corDnTr1;
        sectionTrend1.end = corUpTr1;
        sectionTrend1.n = baseN;
        //
        return new Section(
                corDnTr2,
                corUpTr2,
                step,
                baseN
        );
    }
}

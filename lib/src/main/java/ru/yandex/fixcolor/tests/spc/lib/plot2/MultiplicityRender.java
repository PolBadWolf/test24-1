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
    int multiplicity(double x) {
        for (SectionUnit unit: sectionUnits) {
            if (x < unit.section) return unit.multiplicity;
        }
        throw new IllegalArgumentException("\"x\" вне диапозона : " + x);
    }
    public Section multiplicity(double fist, double end) throws IllegalArgumentException {
        double lenght = end - fist;
        int step = multiplicity(lenght);
        int nDn = (int) Math.floor(fist / step);
        int nUp = (int) Math.ceil(end / step);
        return new Section(
                step * nDn,
                step * nUp,
                step,
                Math.abs(nDn) + Math.abs(nUp)
        );
    }
}

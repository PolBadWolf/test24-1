package ru.yandex.fixcolor.tests.spc.lib.plot2;

class PlotSwing extends PlotParent implements PlotParent.TrendCallBack {
    public PlotSwing(Plot.Parameters parameters) {
        super(parameters);
        // тренд1
        trends[0] = new Trend(this);
        // тренд2
        trends[1] = new Trend(this);
        // sets
        setParametersTrends(parameters);
    }

    @Override
    public void ll(TrendUnit[] units) {

    }
}

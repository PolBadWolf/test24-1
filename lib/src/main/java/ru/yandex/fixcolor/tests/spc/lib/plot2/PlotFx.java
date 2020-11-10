package ru.yandex.fixcolor.tests.spc.lib.plot2;

class PlotFx extends PlotParent implements PlotParent.TrendCallBack {
    public PlotFx(Plot.Parameters parameters) {
        super(parameters, 0, 0);
        // тренд1
        trends[0] = new Trend(this);
        // тренд2
        trends[1] = new Trend(this);
        // sets
        setParametersTrends(parameters);
    }

    @Override
    public void clear() {

    }

    @Override
    public void ll(TrendUnit[] units) {

    }
}

package ru.yandex.fixcolor.tests.spc.lib.plot2.test;

import ru.yandex.fixcolor.tests.spc.lib.plot2.Plot;

public class CycleTest implements Runnable {
    public CycleTest(Plot plot) {
        this.plot = plot;
        this.plot.setCallBack(plotCallBack);
    }
    Plot plot;
    int curX = 0;
    int tr1 = 0;
    int tr1_f = 115;
    boolean pl_tr1 = true;
    int tr2 = 0;
    int tr2_f = 8_000;
    boolean pl_tr2 = true;
    int dynDiv = 100;
    int dynCount = dynDiv;
    public boolean flOnWork = true;
    @Override
    public void run() {
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
        do {
            try {
                Thread.sleep(1);
                plot.newData(curX);
                plot.addTrend(tr1);
                plot.addTrend(tr2);
                plot.setData();
                if (pl_tr1) {
                    if (tr1 >= tr1_f) pl_tr1 = false;
                } else {
                    if (tr1 <= 0) pl_tr1 = true;
                }
                if (pl_tr2) {
                    if (tr2 >= tr2_f) pl_tr2 = false;
                } else {
                    if (tr2 <= 0) pl_tr2 = true;
                }
                if (pl_tr1) tr1+=1;
                else tr1-=1;
                if (pl_tr2) tr2+=1;
                else tr2-=1;
                if (--dynCount == 0) {
                    dynCount = dynDiv;
                    //plot.clear();
                    plot.paint();
                }
//                    System.out.println(tr1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        } while (++curX < 5_000 && flOnWork);
        plot.paint();
    }
    Plot.CallBack plotCallBack = new Plot.CallBack() {
    };
}

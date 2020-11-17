package ru.yandex.fixcolor.tests.spc.lib.plot2.test;

import ru.yandex.fixcolor.tests.spc.lib.plot2.Plot;

public class CiclTest implements Runnable {
    public CiclTest(Plot plot) {
        this.plot = plot;
    }
    Plot plot;
    int curX = 0;
    int tr1 = 0;
    int tr1_f = 150;
    boolean pl_tr1 = true;
    int tr2 = 0;
    int tr2_f = 777;
    boolean pl_tr2 = true;
    @Override
    public void run() {
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
                if (pl_tr1) tr1++;
                else tr1--;
                if (pl_tr2) tr2++;
                else tr2--;
                if (curX % 25 == 0) plot.paint();
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        } while (++curX < 180_000);
    }
}

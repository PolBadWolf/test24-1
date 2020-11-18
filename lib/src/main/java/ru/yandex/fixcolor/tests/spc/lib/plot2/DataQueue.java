package ru.yandex.fixcolor.tests.spc.lib.plot2;

class DataQueue {
    public int command;
    GraphData[] datGraph;

    public DataQueue(int command, GraphData[] datGraph) {
        this.command = command;
        this.datGraph = datGraph;
    }
}

package org.example.test24.allinterface;

import org.example.test24.allinterface.screen.ScreenClass_interface;

public class Closer {
    private static Closer closer = new Closer();

    private Closer_Interface        commPort    = null;
    private Closer_Interface        runner      = null;
    private ScreenClass_interface   mainFx      = null;

    public static Closer getCloser() {
        return closer;
    }

    public void init(
        Closer_Interface commPort,
        Closer_Interface runner,
        ScreenClass_interface mainFx
    ) {
        this.commPort = commPort;
        this.runner = runner;
        this.mainFx = mainFx;
    }

    public void closeAll() {
        commPort.Close();
        runner.Close();
        mainFx.exitApp();
        System.exit(0);
    }
}

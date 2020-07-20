package org.example.test24.allinterface;

import org.example.test24.allinterface.commPort.CommPort_Interface;
import org.example.test24.allinterface.runner.Runner_Interface;
import org.example.test24.allinterface.screen.ScreenClass_interface;

public class Closer {
    private static Closer closer = new Closer();

    private CommPort_Interface      commPort    = null;
    private Runner_Interface        runner      = null;
    private ScreenClass_interface   mainFx      = null;

    public static Closer getCloser() {
        return closer;
    }

    public void init(
        CommPort_Interface commPort,
        Runner_Interface runner,
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

package org.example.test24.loader;

import org.example.test24.RS232.CommPort;
import org.example.test24.allinterface.Closer;
import org.example.test24.RS232.BAUD;
import org.example.test24.runner.Running;
import org.example.test24.screen.MainFrame;
import org.example.test24.screen.ScreenClass;


public class MainClass {
    private ScreenClass mainFx = null;
    private Running runner = null;
    private CommPort commPort = null;

    public static void main(String[] args) {
        new MainClass().start(args);
    }

    private void start(String[] args) {
        String namePort = "";

        if (args.length > 0)    namePort = args[0];

        mainFx= new ScreenClass();
        runner = new Running();
        commPort = new CommPort();

        Closer.getCloser().init(() -> commPort.Close(), () -> runner.Close(), mainFx);

        int checkComm = commPort.Open((bytes, lenght) -> runner.reciveRsPush(bytes, lenght), namePort, BAUD.baud115200);
        if (checkComm != CommPort.INITCODE_OK) {
            errorCommMessage(checkComm, commPort);
            System.exit(0);
        }

        (new Thread(mainFx)).start();
        while (MainFrame.mainFrame == null) {
            Thread.yield();
        }

        runner.init(commPort, MainFrame.mainFrame);
        //runner.start();

        commPort.ReciveStart();
    }

    private void errorCommMessage(int checkComm, CommPort commPort) {
        switch (checkComm) {
            case CommPort.INITCODE_ERROROPEN:
                System.out.println("ошибка открытия порта");
                break;
            case CommPort.INITCODE_NOTEXIST:
                System.out.println("указанный порт отсутствует");
                System.out.println("имеющиеся порты в системе:");
                for (String name : commPort.getListPortsName()) {
                    System.out.println(name);
                }
                break;
        }
    }
}

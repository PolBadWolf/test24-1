package org.example.test24.loader;

import org.example.bd.BdWork;
import org.example.bd.MyBlob;
import org.example.test24.RS232.CommPort;
import org.example.test24.allinterface.Closer;
import org.example.test24.RS232.BAUD;
import org.example.test24.allinterface.bd.DistClass;
import org.example.test24.runner.Running;
import org.example.test24.screen.MainFrame;
import org.example.test24.screen.ScreenClass;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;


public class MainClass {
    private ScreenClass mainFx = null;
    private Running runner = null;
    private CommPort commPort = null;

    public static void main(String[] args) {
        ArrayList<DistClass> tMass = new ArrayList<>();
        tMass.add(new DistClass(12, 13));
        tMass.add(new DistClass(65535, 16384));
/*        MyBlob myBlob = new MyBlob(tMass);
        byte[] bytes;
        try {
            bytes = new byte[(int) myBlob.length()];
            bytes = myBlob.getBytes(1, bytes.length);
        } catch (java.lang.Throwable e) {
            e.printStackTrace();
        }*/
        BdWork bdWork = null;
        try {
            bdWork = new BdWork("MS_SQL");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        bdWork.pushDataDist(new Date(), 0, 0, 0, 0, 0, 0, new MyBlob(tMass));

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

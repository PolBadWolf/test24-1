package ru.yandex.fixcolor.tests.spc.rs232;

import com.fazecast.jSerialComm.SerialPort;
import ru.yandex.fixcolor.tests.spc.lib.ControlSumma;

class CommPortClass implements CommPort {

    private SerialPort port = null;
    private Thread threadRS = null;
    private CallBack callBack = null;
    private int reciveTimeOut = 0;

    static String[] getListPortsName() {
        SerialPort[] ports = SerialPort.getCommPorts();
        String[] namePorts = new String[ports.length];
        for (int i = 0; i < ports.length; i++) {
            namePorts[i] = ports[i].getSystemPortName().toUpperCase();
        }
        return namePorts;
    }
    static boolean isCheckCommPort(String portName) throws Exception {
        if (portName == null) {
            throw new Exception("имя порта не установлено");
        }
        CommPortClass port = new CommPortClass();
        PortStat stat = port.open(
                (bytes, lenght) -> { },
                portName,
                BAUD.baud57600
        );
        port.close();
        if (stat == PortStat.INITCODE_OK) return true;
        return false;
    }

    @Override
    public PortStat open(CallBack callBack, String portName, BAUD baud) {
        if (port != null) close();

        boolean flagTmp = false;
        String[] portsName = CommPort.getListPortsName();
        String portNameCase = portName.toUpperCase();
        for (String s : portsName) {
            if (s.equals(portNameCase)) {
                flagTmp = true;
                break;
            }
        }

        if (!flagTmp)   return CommPort.PortStat.INITCODE_NOTEXIST;

        port = SerialPort.getCommPort(portNameCase);
        port.setComPortParameters(baud.getBaud(), 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 1, 50);

        if (port.openPort()) {
            this.callBack = callBack;
            return CommPort.PortStat.INITCODE_OK;
        }

        return CommPort.PortStat.INITCODE_ERROROPEN;
    }

    @Override
    public void close() {
        if (port == null)   return;
        ReciveStop();
        if (port.isOpen()) {
            port.closePort();
        }
        port = null;
    }

    @Override
    public boolean isOpen() {
        if (port == null)   return false;
        return port.isOpen();
    }
    byte[] dump = new byte[1024];
    @Override
    public boolean ReciveStart() {
        if (port == null)   return false;
        if (!port.isOpen()) return false;

        while (port.readBytes(dump, dump.length) > 0) ;

        threadRS = new Thread(this::runnerReciver);
        threadRS.start();
        return true;
    }

    @Override
    public void ReciveStop() {
        onCycle = false;

        try {
            if (threadRS != null) {
                while (threadRS.isAlive()) {
                    Thread.yield();
                }
            }
        }
        catch (java.lang.Throwable th) {
            th.printStackTrace();
        }
    }

    @Override
    public boolean isRecive() {
        if (port == null)   return false;
        if (!port.isOpen()) return false;
        return onCycle;
    }

    // ---------------------
    final private int headBufferLenght  = 5;
    final private int timeOutLenght     = 5;
    // ---------------------
    private int timeOutSynhro = 1;
    private boolean flagHead = true;
    private byte[]  headBuffer = new byte[headBufferLenght];
    private int lenghtRecive;
    private int lenghtReciveSumm;
    private byte crc;
    // ===========================================================
    //                режим работы
    private static final int reciveMode_SYNHRO = 0;
    private static final int reciveMode_LENGHT = 1;
    private static final int reciveMode_BODY = 2;
    private static final int reciveMode_OUT = 3;
    private int reciveMode = reciveMode_SYNHRO;
    // ---------------------
    //        SYNHRO
    private static final int reciveHeader_lenght = 4;
    private byte[] reciveHeader = new byte[reciveHeader_lenght];
    private byte[] reciveHeader_in = new byte[1];
    // ---------------------
    //        LENGHT
    private int reciveBody_lenght;
    // ---------------------
    private byte[] reciveBody_Buffer = new byte[256];
    private int reciveBody_Index;
    // ---------------------
    private boolean onCycle;
    int recive_num;
    // ===========================================================

    private void runnerReciver() {
        onCycle = true;
        reciveMode = reciveMode_SYNHRO;
        recive_num = 0;
        try {
            while (onCycle) {
                if (recive_num == 0) Thread.sleep(1);
                if (reciveTimeOut == 1) reciveMode = reciveMode_SYNHRO;
                if (reciveTimeOut > 0) reciveTimeOut--;
                switch (reciveMode) {
                    case reciveMode_SYNHRO:
                        recive_synhro();
                        break;
                    case reciveMode_LENGHT:
                        recive_lenght();
                        break;
                    case reciveMode_BODY:
                        recive_body();
                        break;
                    case reciveMode_OUT:
                        recive_out();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + reciveMode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void recive_synhro() throws Exception {
        recive_num = port.readBytes(reciveHeader_in, 1);
        if (recive_num == 0) return;
        // shift
        for (int i = 0; i < reciveHeader_lenght - 1; i++) {
            reciveHeader[i] = reciveHeader[i + 1];
        }
        // new byte
        reciveHeader[reciveHeader_lenght - 1] = reciveHeader_in[0];
        // check
        if ((reciveHeader[reciveHeader.length - 4] & 0xff) != 0xe6) return;
        if ((reciveHeader[reciveHeader.length - 3] & 0xff) != 0x19) return;
        if ((reciveHeader[reciveHeader.length - 2] & 0xff) != 0x55) return;
        if ((reciveHeader[reciveHeader.length - 1] & 0xff) != 0xaa) return;
        // ok
        reciveTimeOut = 10;
        reciveMode = reciveMode_LENGHT;
    }
    private void recive_lenght() throws Exception {
        recive_num = port.readBytes(reciveHeader_in, 1);
        if (recive_num == 0) return;
        reciveBody_lenght = reciveHeader_in[0] & 0xff;
        reciveBody_Index = 0;
        reciveMode = reciveMode_BODY;
    }
    private void recive_body() throws Exception {
        int lenght = reciveBody_lenght - reciveBody_Index;
        recive_num = port.readBytes(reciveBody_Buffer, lenght, reciveBody_Index);
        if (recive_num == 0) return;
        reciveBody_Index += recive_num;
        if (reciveBody_Index > reciveBody_lenght) throw new Exception("переполнение буффера приема");
        if (reciveBody_Index < reciveBody_lenght) return;
        reciveMode = reciveMode_OUT;
    }
    private void recive_out() throws Exception {
        if (ControlSumma.crc8(reciveBody_Buffer, reciveBody_lenght - 1) == reciveBody_Buffer[reciveBody_lenght - 1]) {
            if (callBack != null) {
                callBack.reciveRsPush(reciveBody_Buffer, reciveBody_lenght - 1);
            }
        }
        reciveTimeOut = 0;
        reciveMode = reciveMode_SYNHRO;
    }
    // ************************************************************************
    private static final byte[] header = {
            (byte)0xe6,
            (byte)0x19,
            (byte)0x55,
            (byte)0xaa
    };
    private void send_header() throws Exception {
        int l = port.writeBytes(header, header.length);
//        if (l < 1) throw new Exception("ошибка отправки по comm port");
    }
    private byte[] send_lenghtVar = new byte[1];
    private void send_lenght(byte[] body) throws Exception {
        send_lenghtVar[0] = (byte) ((body.length + 1) & 0xff);
        int l = port.writeBytes(send_lenghtVar, 1);
//        if (l < 1) throw new Exception("ошибка отправки по comm port");
    }
    // ====
    private boolean sendPack(byte[] body) throws Exception {
        send_header();
        send_lenght(body);
        port.writeBytes(body, body.length);
        // контрольная сумма
        byte[] cs = new byte[1];
        cs[0] = ControlSumma.crc8(body, body.length);
        int l = port.writeBytes(cs, cs.length);
        return l != body.length;
    }
    // =======================================================================
    private static final byte[] sendMessageStopBody = {
            // код передачи
            (byte) CommandForCntr.STOP
    };
    @Override
    public void sendMessageStop() throws Exception {
        sendPack(sendMessageStopBody);
    }
    // ---------------------
    private static final byte[] sendMessageStopNcycleMaxBody = {
            // код передачи
            (byte) CommandForCntr.CYCLE_MAX,
            // максимальное кол-во циклов
            (byte) 1
    };
    @Override
    public void sendMessageStopNcycleMax(int nCycleMax) throws Exception {
        sendMessageStopNcycleMaxBody[1] = (byte) nCycleMax;
        sendPack(sendMessageStopNcycleMaxBody);
    }
    // ========================================================================
    private static final byte[] sendMessageCalibrationBody = {
            // код передачи
            (byte) CommandForCntr.CALIBRATION
    };
    @Override
    public void sendMessageCalibrationMode() throws Exception {
        sendPack(sendMessageCalibrationBody);
    }
    // ************************************************************************

    @Override
    public CallBack getCallBack() {
        return callBack;
    }
    @Override
    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }
}

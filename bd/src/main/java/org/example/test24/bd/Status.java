package org.example.test24.bd;

final class StatusConst {
    final static int STATUS_OK = 0;
    final static int STATUS_ERROR = 1;
    final static int STATUS_PARAMETERS_LOAD_ERROR = 2;
    final static int STATUS_PARAMETERS_SAVE_ERROR = 3;
    final static int STATUS_PARAMETERS_ERROR = 4;
    final static int STATUS_PARAMETERS_PASSWORD_ERROR = 5;
    final static int STATUS_BASE_TYPE_ERROR = 6;
    final static int STATUS_BASE_TYPE_NO_SELECT = 7;
    final static int STATUS_CONFIG_LOAD_ERROR = 8;
    final static int STATUS_CONNECT_BASE_TYPE_ERROR = 9;
    final static int STATUS_CONNECT_DRIVER_ERROR = 10;
    final static int STATUS_CONNECT_PASS_ERROR = 11;
    final static int STATUS_CONNECT_BASE_ERROR = 12;
    final static int STATUS_CONNECT_ERROR = 13;
    final static int STATUS_CONNECT_NO_CONNECTION = 14;
    final static int STATUS_CONNECT_CLOSE = 15;
    final static int STATUS_SQL_TRANSACTION_ERROR = 16;
}

public enum Status {
    OK                              (StatusConst.STATUS_OK),
    ERROR                           (StatusConst.STATUS_ERROR),
    PARAMETERS_LOAD_ERROR           (StatusConst.STATUS_PARAMETERS_LOAD_ERROR),
    PARAMETERS_SAVE_ERROR           (StatusConst.STATUS_PARAMETERS_SAVE_ERROR),
    PARAMETERS_ERROR                (StatusConst.STATUS_PARAMETERS_ERROR),
    PARAMETERS_PASSWORD_ERROR       (StatusConst.STATUS_PARAMETERS_PASSWORD_ERROR),
    BASE_TYPE_ERROR                 (StatusConst.STATUS_BASE_TYPE_ERROR),
    BASE_TYPE_NO_SELECT             (StatusConst.STATUS_BASE_TYPE_NO_SELECT),
    CONFIG_LOAD_ERROR               (StatusConst.STATUS_CONFIG_LOAD_ERROR),
    CONNECT_BASE_TYPE_ERROR         (StatusConst.STATUS_CONNECT_BASE_TYPE_ERROR),
    CONNECT_DRIVER_ERROR            (StatusConst.STATUS_CONNECT_DRIVER_ERROR),
    CONNECT_PASS_ERROR              (StatusConst.STATUS_CONNECT_PASS_ERROR),
    CONNECT_BASE_ERROR              (StatusConst.STATUS_CONNECT_BASE_ERROR),
    CONNECT_ERROR                   (StatusConst.STATUS_CONNECT_ERROR),
    CONNECT_NO_CONNECTION           (StatusConst.STATUS_CONNECT_NO_CONNECTION),
    CONNECT_CLOSE                   (StatusConst.STATUS_CONNECT_CLOSE),
    SQL_TRANSACTION_ERROR           (StatusConst.STATUS_SQL_TRANSACTION_ERROR);

    int codeStatus;

    int getCodeStatus() {
        return codeStatus;
    }

    Status(int codeStatus) {
        this.codeStatus = codeStatus;
    }

    @Override
    public String toString() {
        String text;
        switch (codeStatus) {
            case StatusConst.STATUS_OK:
                text = "ок";
                break;
            case StatusConst.STATUS_PARAMETERS_LOAD_ERROR:
                text = "ошибка загрузки";
                break;
            case StatusConst.STATUS_PARAMETERS_SAVE_ERROR:
                text = "ошибка сохранения";
                break;
            case StatusConst.STATUS_PARAMETERS_ERROR:
                text = "ошибка параметров";
                break;
            case StatusConst.STATUS_PARAMETERS_PASSWORD_ERROR:
                text = "ошибка пароля в параметрах";
                break;
            case StatusConst.STATUS_BASE_TYPE_ERROR:
                text = "ошибка типа БД";
                break;
            case StatusConst.STATUS_CONFIG_LOAD_ERROR:
                text = "ошибка загрузки конфигурации";
                break;
            case StatusConst.STATUS_CONNECT_DRIVER_ERROR:
                text = "ошибка драйвера";
                break;
            case StatusConst.STATUS_CONNECT_PASS_ERROR:
                text = "ошибка пароля при соединении";
                break;
            case StatusConst.STATUS_CONNECT_BASE_ERROR:
                text = "ошибка соединения с базой БД";
                break;
            case StatusConst.STATUS_CONNECT_ERROR:
                text = "ошибка соединения с БД";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + codeStatus);
        }
        return text;
    }
}

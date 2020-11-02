package ru.yandex.fixcolor.tests.spc.bd;

public class BaseDataException extends Exception {
    private Status status;

    public BaseDataException(Status status) {
        this.status = status;
    }

    public BaseDataException(String message, Status status) {
        super(message);
        this.status = status;
    }

    public BaseDataException(String message, Throwable cause, Status status) {
        super(message, cause);
        this.status = status;
    }

    public BaseDataException(Throwable cause, Status status) {
        super(cause);
        this.status = status;
    }

    public BaseDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Status status) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}

package org.example.test24.bd;

public class BaseDataException extends Exception {
    private BaseData.Status status;

    public BaseDataException(BaseData.Status status) {
        this.status = status;
    }

    public BaseDataException(String message, BaseData.Status status) {
        super(message);
        this.status = status;
    }

    public BaseDataException(String message, Throwable cause, BaseData.Status status) {
        super(message, cause);
        this.status = status;
    }

    public BaseDataException(Throwable cause, BaseData.Status status) {
        super(cause);
        this.status = status;
    }

    public BaseDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, BaseData.Status status) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.status = status;
    }

    public BaseData.Status getStatus() {
        return status;
    }
}

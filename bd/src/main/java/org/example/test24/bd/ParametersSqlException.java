package org.example.test24.bd;

public class ParametersSqlException extends BaseDataException {
    private BaseData.Parameters parameters;

    public ParametersSqlException(BaseData.Status status, BaseData.Parameters parameters) {
        super(status);
        this.parameters = parameters;
    }

    public ParametersSqlException(String message, BaseData.Status status, BaseData.Parameters parameters) {
        super(message, status);
        this.parameters = parameters;
    }

    public ParametersSqlException(String message, Throwable cause, BaseData.Status status, BaseData.Parameters parameters) {
        super(message, cause, status);
        this.parameters = parameters;
    }

    public ParametersSqlException(Throwable cause, BaseData.Status status, BaseData.Parameters parameters) {
        super(cause, status);
        this.parameters = parameters;
    }

    public ParametersSqlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, BaseData.Status status, BaseData.Parameters parameters) {
        super(message, cause, enableSuppression, writableStackTrace, status);
        this.parameters = parameters;
    }

    public BaseData.Parameters getParameters() {
        return parameters;
    }

    public void setParameters(BaseData.Parameters parameters) {
        this.parameters = parameters;
    }
}

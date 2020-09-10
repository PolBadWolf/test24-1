package org.example.test24.bd;

public interface BaseData {
    // ==================== STATUS ====================
    int STATUS_OK = 0;
    enum Status {
        OK          (STATUS_OK);
        int codeStatus;
        int getCodeStatus() {
            return codeStatus;
        }
        Status(int codeStatus) {
            this.codeStatus = codeStatus;
        }
    }
    // ==================== TYPE BD ====================
    int TYPEBD_MYSQL = 0;
    int TYPEBD_MSSQL = 1;
    int TYPEBD_ERROR = -1;
    enum TypeBaseDate {
        MYSQL       (TYPEBD_MYSQL),
        MSSQL       (TYPEBD_MSSQL),
        ERROR       (TYPEBD_ERROR);
        int codeTypeBaseData;
        int getCodeTypeBaseData() {
            return codeTypeBaseData;
        }
        TypeBaseDate(int codeTypeBaseData) {
            this.codeTypeBaseData = codeTypeBaseData;
        }
    }
    // ==================== PARAMETERS ====================
    interface Parameters {

    }
    // ==================== SQL ====================
    static BaseData create(Parameters parameters) {
        return null;
    }
}

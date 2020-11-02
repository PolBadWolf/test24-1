package ru.yandex.fixcolor.tests.spc.bd;

class TypeBaseDateConst{
    // ==================== TYPE BD ====================
    final static int TYPEBD_MYSQL = 0;
    final static int TYPEBD_MSSQL = 1;
    final static int TYPEBD_ERROR = -1;
}

public enum TypeBaseDate {
    MY_SQL      (TypeBaseDateConst.TYPEBD_MYSQL),
    MS_SQL      (TypeBaseDateConst.TYPEBD_MSSQL),
    ERROR       (TypeBaseDateConst.TYPEBD_ERROR);
    int codeTypeBaseData;
    int getCodeTypeBaseData() {
        return codeTypeBaseData;
    }
    TypeBaseDate(int codeTypeBaseData) {
        this.codeTypeBaseData = codeTypeBaseData;
    }
    static TypeBaseDate create(String typeBaseData
    //        , Consumer<TypeBaseDate> tbd
    ) throws Exception {
        if (typeBaseData == null) {
//            tbd.accept(TypeBaseDate.ERROR);
            throw new BaseDataException("ошибка типа БД (typeBaseData = null)", Status.BASE_TYPE_ERROR);
        }
        TypeBaseDate typeBaseDate;
        switch (typeBaseData.toUpperCase()) {
            case "MY_SQL":
                //tbd.accept(TypeBaseDate.MY_SQL);
                typeBaseDate = TypeBaseDate.MY_SQL;
                break;
            case "MS_SQL":
//                tbd.accept(TypeBaseDate.MS_SQL);
                typeBaseDate = TypeBaseDate.MS_SQL;
                break;
            /*default:
                tbd.accept(TypeBaseDate.ERROR);
                throw new BaseDataException("ошибка типа БД (typeBaseData = " + typeBaseData + ")", Status.BASE_TYPE_ERROR);*/
            default:
                throw new IllegalStateException("Unexpected value: " + typeBaseData.toUpperCase());
        }
        return typeBaseDate;
    }

    public String codeToString() throws BaseDataException {
        String text;
        switch (codeTypeBaseData) {
            case TypeBaseDateConst.TYPEBD_MYSQL:
                text = "MY_SQL";
                break;
            case TypeBaseDateConst.TYPEBD_MSSQL:
                text = "MS_SQL";
                break;
            default:
                throw new BaseDataException("не известный код типа БД", Status.BASE_TYPE_NO_SELECT);
        }
        return text;
    }
}

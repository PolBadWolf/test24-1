package org.example.test24.allinterface.bd;

import org.example.test24.lib.Converts;

import java.sql.Blob;
import java.sql.SQLException;

public class MeasuredBlobDecoder {
    private static final int baseLenght = 8;
    private Blob blob;
    private long lenght;
    public MeasuredBlobDecoder(Blob blob) throws Exception {
        this.blob = blob;
        try {
            lenght = blob.length() / 8;
        } catch (SQLException e) {
            throw new Exception("инициция декодирования", e);
        }
    }
    public long lenght() {
        return lenght;
    }
    public DistClass get(long i) throws Exception {
        byte[] bytes;
        try {
            bytes = blob.getBytes((i * baseLenght) + 1, baseLenght);

        } catch (SQLException e) {
            throw new Exception("чтение " + i, e);
        }
        int tik = Converts.bytesToInt(bytes, 4, 0);
        int distance = Converts.bytesToInt(bytes, 2, 4);
        int ves = Converts.bytesToInt(bytes, 2, 6);
        return new DistClass(tik, distance, ves);
    }
}

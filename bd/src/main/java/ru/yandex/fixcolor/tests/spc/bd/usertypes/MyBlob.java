package ru.yandex.fixcolor.tests.spc.bd.usertypes;

import ru.yandex.fixcolor.tests.spc.lib.Converts;
import ru.yandex.fixcolor.tests.spc.allinterface.bd.DistClass;

import javax.sql.rowset.serial.SerialException;
import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class MyBlob implements Blob {
    private byte[] buf;
    private Blob blob;
    private long len;
    private long origLen;

    public MyBlob(byte[] bytes) {
        len = bytes.length;
        buf = new byte[(int) len];
        System.arraycopy(bytes, 0, buf, 0, (int) len);
        origLen = len;
    }

    public MyBlob(ArrayList<DistClass> dataMeasured) {
        final int lenghtUnit = 8;
        len = dataMeasured.size();
        buf = new byte[(int) len * lenghtUnit];
        for (int i = 0; i < len; i++) {
            Converts.IntToBytes(dataMeasured.get(i).tik, buf, 4, i * lenghtUnit + 0 );
            Converts.IntToBytes(dataMeasured.get(i).distance, buf, 2, i * lenghtUnit + 4 );
            Converts.IntToBytes(dataMeasured.get(i).ves, buf, 2, i * lenghtUnit + 6 );
        }
        origLen = len = len * lenghtUnit;
    }

    @Override
    public long length() throws SQLException {
        isValid();
        return len;
    }

    @Override
    public byte[] getBytes(long pos, int length) throws SQLException {
        isValid();
        if (length > len) {
            length = (int) len;
        }

        if ( (pos < 1) || (len - pos < 0) ) {
            throw new SQLException("Invalid arguments: position cannot be "
                    + "less than 1 or greater than the length of the SerialBlob");
        }

        pos--;

        byte[] b = new byte[length];
        for (int i = 0; i < length; i++) {
            b[i] = buf[(int) pos];
            pos++;
        }

        return b;
    }

    @Override
    public InputStream getBinaryStream() throws SQLException {
        isValid();
        return new ByteArrayInputStream(buf);
    }

    @Override
    public long position(byte[] pattern, long start) throws SQLException {
        isValid();

        if ( (start < 1) || (start > len) ) {
            return -1;
        }

        int pos = (int) start - 1;
        int i = 0;
        long patlen = pattern.length;

        while (pos < len) {
            if (pattern[i] == buf[pos]) {
                if (i + 1 == patlen) {
                    return (pos + 1) - (patlen - 1);
                }
                i++;
                pos++;
            } else if (pattern[i] != buf[pos]) {
                pos++;
            }
        }
        return -1;
    }

    @Override
    public long position(Blob pattern, long start) throws SQLException {
        isValid();
        return position(pattern.getBytes(1, (int) (pattern.length())), start);
    }

    @Override
    public int setBytes(long pos, byte[] bytes) throws SQLException {
        return setBytes(pos, bytes, 0, bytes.length);
    }

    @Override
    public int setBytes(long pos, byte[] bytes, int offset, int length) throws SQLException {
        isValid();
        if ( (offset < 0) || (offset > bytes.length) ) {
            throw new SQLException("Invalid offset in byte array set");
        }

        if (pos < 1 || pos > this.length()) {
            throw new SerialException("Invalid position in BLOB object set");
        }

        if ((long) (length) > origLen) {
            throw new SerialException("Buffer is not sufficient to hold the value");
        }

        if ((length + offset) > bytes.length) {
            throw new SerialException("Invalid OffSet. Cannot have combined offset " +
                    "and length that is greater that the Blob buffer");
        }

        int i = 0;
        pos--; // correct to array indexing
        while ( i < length || (offset + i +1) < (bytes.length-offset) ) {
            this.buf[(int)pos + i] = bytes[offset + i ];
            i++;
        }
        return i;
    }

    @Override
    public OutputStream setBinaryStream(long pos) throws SQLException {
        isValid();
        if (this.blob != null) {
            return this.blob.setBinaryStream(pos);
        } else {
            throw new SerialException("Unsupported operation. SerialBlob cannot " +
                    "return a writable binary stream, unless instantiated with a Blob object " +
                    "that provides a setBinaryStream() implementation");
        }
    }

    @Override
    public void truncate(long length) throws SQLException {
        isValid();
        if (length > len) {
            throw new SerialException(
                    "Length more than what can be truncated");
        } else if((int)length == 0) {
            buf = new byte[0];
            len = length;
        } else {
            len = length;
            buf = this.getBytes(1, (int)len);
        }
    }

    @Override
    public void free() throws SQLException {
        if (buf != null) {
            buf = null;
            if (blob != null) {
                blob.free();
            }
            blob = null;
        }
    }

    @Override
    public InputStream getBinaryStream(long pos, long length) throws SQLException {
        isValid();
        if (pos < 1 || pos > this.length()) {
            throw new SerialException("Invalid position in BLOB object set");
        }
        if (length < 1 || length > len - pos + 1) {
            throw new SerialException(
                    "length is < 1 or pos + length > total number of bytes");
        }
        return new ByteArrayInputStream(buf, (int) pos - 1, (int) length);
    }

    private void isValid() throws SerialException {
        if (buf == null) {
            throw new SerialException("Error: You cannot call a method on a " +
                    "SerialBlob instance once free() has been called.");
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MyBlob) {
            MyBlob sb = (MyBlob)obj;
            if (this.len == sb.len) {
                return Arrays.equals(buf, sb.buf);
            }
        }
        return false;
    }

    public int hashCode() {
        return ((31 + Arrays.hashCode(buf)) * 31 + (int)len) * 31 + (int)origLen;
    }

    public Object clone() {
        try {
            MyBlob sb = (MyBlob) super.clone();
            sb.buf = (buf != null) ? Arrays.copyOf(buf, (int)len) : null;
            sb.blob = null;
            return sb;
        } catch (CloneNotSupportedException ex) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    private void readObject(ObjectInputStream s)
            throws IOException, ClassNotFoundException {

        ObjectInputStream.GetField fields = s.readFields();
        byte[] tmp = (byte[])fields.get("buf", null);
        if (tmp == null)
            throw new InvalidObjectException("buf is null and should not be!");
        buf = tmp.clone();
        len = fields.get("len", 0L);
        if (buf.length != len)
            throw new InvalidObjectException("buf is not the expected size");
        origLen = fields.get("origLen", 0L);
        blob = (Blob) fields.get("blob", null);
    }

    private void writeObject(ObjectOutputStream s)
            throws IOException {

        ObjectOutputStream.PutField fields = s.putFields();
        fields.put("buf", buf);
        fields.put("len", len);
        fields.put("origLen", origLen);
        // Note: this check to see if it is an instance of Serializable
        // is for backwards compatibiity
        fields.put("blob", blob instanceof Serializable ? blob : null);
        s.writeFields();
    }

}

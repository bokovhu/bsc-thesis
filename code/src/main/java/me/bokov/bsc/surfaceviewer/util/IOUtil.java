package me.bokov.bsc.surfaceviewer.util;

import java.io.*;

public final class IOUtil {

    private IOUtil() {}

    public static <T extends Serializable> T serialize(T object) {

        byte[] bytes = null;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeObject(object);
            oos.flush();
            bytes = baos.toByteArray();

        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new ObjectInputStream(bais)) {

            return (T) ois.readObject();

        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }

    }

}

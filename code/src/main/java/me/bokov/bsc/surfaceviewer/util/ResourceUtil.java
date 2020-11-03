package me.bokov.bsc.surfaceviewer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

// TODO: readResource should attempt to load resource as a file
//      If not found through the ClassLoader
public final class ResourceUtil {

    private ResourceUtil() {
        throw new UnsupportedOperationException();
    }

    public static String readResource(String name) {

        final URL resourceURL = ResourceUtil.class.getClassLoader()
                .getResource(name);

        if (resourceURL == null) {
            throw new IllegalArgumentException(name + " does not exist");
        }

        try (InputStream resourceStream = resourceURL.openStream();
             BufferedReader resourceReader = new BufferedReader(
                     new InputStreamReader(resourceStream))
        ) {

            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = resourceReader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            return sb.toString();

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

}

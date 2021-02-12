package me.bokov.bsc.surfaceviewer.render;

import lombok.Builder;
import lombok.Getter;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Getter
public class Texture {

    private int handle;
    private int width = 1, depth = 1, height = 1;

    private int target = GL46.GL_TEXTURE_2D;
    private int format = GL46.GL_RGBA;
    private int internalFormat = GL46.GL_RGBA8UI;
    private int dataType = GL46.GL_UNSIGNED_BYTE;

    private int wrapS = GL46.GL_CLAMP_TO_EDGE;
    private int wrapT = GL46.GL_CLAMP_TO_EDGE;
    private int wrapR = GL46.GL_CLAMP_TO_EDGE;

    private int minFilter = GL46.GL_LINEAR;
    private int magFilter = GL46.GL_LINEAR;

    private static int findInternalFormatFor(int format, int dataType) {

        if (format == GL46.GL_RED) {

            if (dataType == GL46.GL_UNSIGNED_BYTE) {
                return GL46.GL_R8UI;
            } else if (dataType == GL46.GL_UNSIGNED_INT) {
                return GL46.GL_R32UI;
            } else if (dataType == GL46.GL_INT) {
                return GL46.GL_R32I;
            } else if (dataType == GL46.GL_FLOAT) {
                return GL46.GL_FLOAT;
            } else {
                throw new IllegalArgumentException("Unsupported format and data type combination!");
            }

        } else if (format == GL46.GL_RGB) {

            if (dataType == GL46.GL_UNSIGNED_BYTE) {
                return GL46.GL_RGB;
            } else if (dataType == GL46.GL_UNSIGNED_INT) {
                return GL46.GL_RGB;
            } else if (dataType == GL46.GL_FLOAT) {
                return GL46.GL_FLOAT;
            } else {
                throw new IllegalArgumentException("Unsupported format and data type combination!");
            }

        } else if (format == GL46.GL_RGBA) {

            if (dataType == GL46.GL_UNSIGNED_BYTE) {
                return GL46.GL_RGBA8UI;
            } else if (dataType == GL46.GL_UNSIGNED_INT) {
                return GL46.GL_RGBA;
            } else if (dataType == GL46.GL_INT) {
                return GL46.GL_RGBA32I;
            } else if (dataType == GL46.GL_FLOAT) {
                return GL46.GL_RGBA32F;
            } else {
                throw new IllegalArgumentException("Unsupported format and data type combination!");
            }

        } else if (format == GL46.GL_DEPTH_STENCIL) {
            return GL46.GL_DEPTH24_STENCIL8;
        } else if (format == GL46.GL_DEPTH_COMPONENT) {
            return GL46.GL_DEPTH_COMPONENT32F;
        }

        throw new IllegalArgumentException("Unsupported format and data type combination!");

    }

    private static ByteBuffer loadDataFromBufferedImage(BufferedImage image) {

        final var buffer = BufferUtils.createByteBuffer(4 * image.getWidth() * image.getHeight());
        final int[] pixelData = image.getRGB(
                0, 0,
                image.getWidth(), image.getHeight(),
                null,
                0,
                image.getWidth()
        );
        final int w = image.getWidth();
        final int h = image.getHeight();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = pixelData[y * w + x];
                final Color color = new Color(rgb);
                buffer.put((byte) color.getRed());
                buffer.put((byte) color.getGreen());
                buffer.put((byte) color.getBlue());
                buffer.put((byte) color.getAlpha());
            }
        }

        return buffer.flip();

    }

    private static LoadedTexture loadDataFromFile(File file) {

        if (!file.exists()) {
            throw new IllegalArgumentException(file + " does not exist!");
        }

        try {
            final var loadedImage = ImageIO.read(file);
            return LoadedTexture.builder()
                    .width(loadedImage.getWidth())
                    .height(loadedImage.getHeight())
                    .rgbData(loadDataFromBufferedImage(loadedImage))
                    .build();
        } catch (IOException ex) {
            throw new IllegalStateException(file + " failed to load!", ex);
        }

    }

    private static LoadedTexture loadDataFromInputStream(InputStream inputStream, String path) {

        try {
            final var loadedImage = ImageIO.read(inputStream);
            return LoadedTexture.builder()
                    .width(loadedImage.getWidth())
                    .height(loadedImage.getHeight())
                    .rgbData(loadDataFromBufferedImage(loadedImage))
                    .build();
        } catch (IOException ex) {
            throw new IllegalStateException(path + " failed to load!", ex);
        }

    }

    public static Texture load(String path) {

        final File currentDir = new File(System.getProperty("user.dir"));

        final File fileInCurrentDir = new File(currentDir, path);
        if (fileInCurrentDir.exists() && fileInCurrentDir.canRead()) {
            return loadDataFromFile(fileInCurrentDir)
                    .createTexture();
        }

        final URL urlOnClasspath = Texture.class.getResource(path);
        if (urlOnClasspath != null) {
            try {
                return loadDataFromInputStream(
                        urlOnClasspath.openStream(),
                        path
                ).createTexture();
            } catch (IOException ex) {
                throw new IllegalStateException(
                        "Tried to load " + path + " from the classpath, but git an IO Exception!",
                        ex
                );
            }
        }

        throw new IllegalArgumentException("Cannot find texture " + path);

    }

    public Texture bind() {

        GL46.glBindTexture(this.target, this.handle);
        return this;

    }

    public Texture bind(int unit) {

        GL46.glActiveTexture(GL46.GL_TEXTURE0 + unit);
        return this.bind();

    }

    public Texture bindImage(int unit, boolean read, boolean write) {

        if (!read && !write) {
            throw new IllegalArgumentException(
                    "Cannot bind texture to image unit with both read and write set to false!");
        }

        GL46.glBindImageTexture(
                unit,
                this.handle,
                0,
                this.target == GL46.GL_TEXTURE_3D,
                0,
                read ? write ? GL46.GL_READ_WRITE : GL46.GL_READ_ONLY : GL46.GL_WRITE_ONLY,
                this.internalFormat
        );

        return this;

    }

    public Texture tearDown() {

        GL46.glDeleteTextures(this.handle);

        return this;

    }

    public Texture init() {

        this.handle = GL46.glGenTextures();

        return this;

    }

    public Texture configure(
            int target,
            int format,
            int dataType
    ) {

        this.target = target;
        this.internalFormat = findInternalFormatFor(format, dataType);
        this.format = format;
        this.dataType = dataType;

        return this;

    }

    public Texture setupSampling(
            int wS, int minF, int magF
    ) {

        if (this.target != GL46.GL_TEXTURE_1D) {
            throw new IllegalStateException("Texture is not 1 dimensional!");
        }

        bind();

        this.wrapS = wS;
        this.minFilter = minF;
        this.magFilter = magF;

        GL46.glTexParameteri(this.target, GL46.GL_TEXTURE_WRAP_S, wS);
        GL46.glTexParameteri(this.target, GL46.GL_TEXTURE_MIN_FILTER, minF);
        GL46.glTexParameteri(this.target, GL46.GL_TEXTURE_MAG_FILTER, magF);

        return this;

    }

    public Texture setupSampling(
            int wS, int wT, int minF, int magF
    ) {

        if (this.target != GL46.GL_TEXTURE_2D) {
            throw new IllegalStateException("Texture is not 2 dimensional!");
        }

        bind();

        this.wrapS = wS;
        this.wrapT = wT;
        this.minFilter = minF;
        this.magFilter = magF;

        GL46.glTexParameteri(this.target, GL46.GL_TEXTURE_WRAP_S, wS);
        GL46.glTexParameteri(this.target, GL46.GL_TEXTURE_WRAP_T, wT);
        GL46.glTexParameteri(this.target, GL46.GL_TEXTURE_MIN_FILTER, minF);
        GL46.glTexParameteri(this.target, GL46.GL_TEXTURE_MAG_FILTER, magF);

        return this;

    }

    public Texture setupSampling(
            int wS, int wT, int wR, int minF, int magF
    ) {

        if (this.target != GL46.GL_TEXTURE_3D) {
            throw new IllegalStateException("Texture is not 3 dimensional!");
        }

        bind();

        this.wrapS = wS;
        this.wrapT = wT;
        this.wrapR = wR;

        this.minFilter = minF;
        this.magFilter = magF;

        GL46.glTexParameteri(this.target, GL46.GL_TEXTURE_WRAP_S, wS);
        GL46.glTexParameteri(this.target, GL46.GL_TEXTURE_WRAP_T, wT);
        GL46.glTexParameteri(this.target, GL46.GL_TEXTURE_WRAP_R, wR);
        GL46.glTexParameteri(this.target, GL46.GL_TEXTURE_MIN_FILTER, minF);
        GL46.glTexParameteri(this.target, GL46.GL_TEXTURE_MAG_FILTER, magF);

        return this;

    }

    public Texture resize(int w) {

        if (this.target != GL46.GL_TEXTURE_1D) {
            throw new IllegalStateException("Texture is not 1 dimensional!");
        }

        this.width = w;
        this.height = 1;
        this.depth = 1;

        return this;

    }

    public Texture resize(int w, int h) {

        if (this.target != GL46.GL_TEXTURE_2D) {
            throw new IllegalStateException("Texture is not 2 dimensional!");
        }

        this.width = w;
        this.height = h;
        this.depth = 1;

        return this;

    }

    public Texture resize(int w, int h, int d) {

        if (this.target != GL46.GL_TEXTURE_3D) {
            throw new IllegalStateException("Texture is not 3 dimensional!");
        }

        this.width = w;
        this.height = h;
        this.depth = d;

        return this;

    }

    public Texture uploadFloat(FloatBuffer buffer) {

        bind();

        switch (this.target) {
            case GL46.GL_TEXTURE_1D:
                GL46.glTexImage1D(
                        this.target,
                        0,
                        this.internalFormat,
                        this.width,
                        0,
                        this.format,
                        GL46.GL_FLOAT,
                        buffer
                );
                break;
            case GL46.GL_TEXTURE_2D:
                GL46.glTexImage2D(
                        this.target,
                        0,
                        this.internalFormat,
                        this.width,
                        this.height,
                        0,
                        this.format,
                        GL46.GL_FLOAT,
                        buffer
                );
                break;
            case GL46.GL_TEXTURE_3D:
                GL46.glTexImage3D(
                        this.target,
                        0,
                        this.internalFormat,
                        this.width,
                        this.height,
                        this.depth,
                        0,
                        this.format,
                        GL46.GL_FLOAT,
                        buffer
                );
                break;
            default:
                throw new IllegalStateException("Invalid upload target!");
        }

        return this;

    }

    public Texture uploadByte(ByteBuffer buffer) {
        return uploadByte(buffer, false);
    }

    public Texture uploadByte(ByteBuffer buffer, boolean signed) {

        bind();

        switch (this.target) {
            case GL46.GL_TEXTURE_1D:
                GL46.glTexImage1D(
                        this.target,
                        0,
                        this.internalFormat,
                        this.width,
                        0,
                        this.format,
                        signed ? GL46.GL_BYTE : GL46.GL_UNSIGNED_BYTE,
                        buffer
                );
                break;
            case GL46.GL_TEXTURE_2D:
                GL46.glTexImage2D(
                        this.target,
                        0,
                        this.internalFormat,
                        this.width,
                        this.height,
                        0,
                        this.format,
                        signed ? GL46.GL_BYTE : GL46.GL_UNSIGNED_BYTE,
                        buffer
                );
                break;
            case GL46.GL_TEXTURE_3D:
                GL46.glTexImage3D(
                        this.target,
                        0,
                        this.internalFormat,
                        this.width,
                        this.height,
                        this.depth,
                        0,
                        this.format,
                        signed ? GL46.GL_BYTE : GL46.GL_UNSIGNED_BYTE,
                        buffer
                );
                break;
            default:
                throw new IllegalStateException("Invalid upload target!");
        }

        return this;

    }

    public Texture uploadByte(ByteBuffer buffer, int dataType) {

        bind();

        switch (this.target) {
            case GL46.GL_TEXTURE_1D:
                GL46.glTexImage1D(
                        this.target,
                        0,
                        this.internalFormat,
                        this.width,
                        0,
                        this.format,
                        dataType,
                        buffer
                );
                break;
            case GL46.GL_TEXTURE_2D:
                GL46.glTexImage2D(
                        this.target,
                        0,
                        this.internalFormat,
                        this.width,
                        this.height,
                        0,
                        this.format,
                        dataType,
                        buffer
                );
                break;
            case GL46.GL_TEXTURE_3D:
                GL46.glTexImage3D(
                        this.target,
                        0,
                        this.internalFormat,
                        this.width,
                        this.height,
                        this.depth,
                        0,
                        this.format,
                        dataType,
                        buffer
                );
                break;
            default:
                throw new IllegalStateException("Invalid upload target!");
        }

        return this;

    }

    public Texture uploadInt(IntBuffer buffer) {
        return uploadInt(buffer, false);
    }

    public Texture uploadInt(IntBuffer buffer, boolean signed) {

        bind();

        switch (this.target) {
            case GL46.GL_TEXTURE_1D:
                GL46.glTexImage1D(
                        this.target,
                        0,
                        this.internalFormat,
                        this.width,
                        0,
                        this.format,
                        signed ? GL46.GL_INT : GL46.GL_UNSIGNED_INT,
                        buffer
                );
                break;
            case GL46.GL_TEXTURE_2D:
                GL46.glTexImage2D(
                        this.target,
                        0,
                        this.internalFormat,
                        this.width,
                        this.height,
                        0,
                        this.format,
                        signed ? GL46.GL_INT : GL46.GL_UNSIGNED_INT,
                        buffer
                );
                break;
            case GL46.GL_TEXTURE_3D:
                GL46.glTexImage3D(
                        this.target,
                        0,
                        this.internalFormat,
                        this.width,
                        this.height,
                        this.depth,
                        0,
                        this.format,
                        signed ? GL46.GL_INT : GL46.GL_UNSIGNED_INT,
                        buffer
                );
                break;
            default:
                throw new IllegalStateException("Invalid upload target!");
        }

        return this;

    }

    public Texture makeStorage() {

        bind();

        switch (this.target) {
            case GL46.GL_TEXTURE_1D:
                GL46.glTexStorage1D(this.target, 1, this.internalFormat, this.width);
                break;
            case GL46.GL_TEXTURE_2D:
                GL46.glTexStorage2D(this.target, 1, this.internalFormat, this.width, this.height);
                break;
            case GL46.GL_TEXTURE_3D:
                GL46.glTexStorage3D(this.target, 1, this.internalFormat, this.width, this.height, this.depth);
                break;

            default:
                throw new IllegalStateException("Invalid texture target!");
        }

        return this;

    }

    @Getter
    @Builder
    private static class LoadedTexture {
        private final int width;
        private final int height;
        private final ByteBuffer rgbData;

        public Texture createTexture() {

            Texture texture = new Texture();

            GL46.glActiveTexture(GL46.GL_TEXTURE4);

            texture.init()
                    .configure(
                            GL46.GL_TEXTURE_2D,
                            GL46.GL_RGBA,
                            GL46.GL_UNSIGNED_BYTE
                    )
                    .resize(width, height);

            int error = GL46.glGetError();

            if (error != GL46.GL_NO_ERROR) {
                System.out.println("GL Error after configuring texture: " + error);
            }

            texture.bind();
            GL46.glTexImage2D(
                    GL46.GL_TEXTURE_2D,
                    0,
                    GL46.GL_RGBA,
                    width, height, 0,
                    GL46.GL_RGBA,
                    GL46.GL_UNSIGNED_BYTE,
                    rgbData
            );

            error = GL46.glGetError();

            if (error != GL46.GL_NO_ERROR) {
                System.out.println("GL Error after loading texture data: " + error);
            }

            texture.setupSampling(
                            GL46.GL_REPEAT, GL46.GL_REPEAT,
                            GL46.GL_NEAREST, GL46.GL_NEAREST
                    );

            error = GL46.glGetError();

            if (error != GL46.GL_NO_ERROR) {
                System.out.println("GL Error after apply texture sampling: " + error);
            }

            return texture;

        }
    }

}

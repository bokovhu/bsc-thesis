package me.bokov.bsc.surfaceviewer.render;

import org.joml.Vector2i;
import org.joml.Vector3i;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.*;

// TODO: Complete texture handling refactor required
// TODO: Should support both generated and loaded textures
// TODO: Should support 1D, 2D and 3D images
@Deprecated
public class Texture {

    private int textureHandle;
    private StorageType storageType = null;
    private TextureType textureType = null;
    private int width = 0, height = 0, depth = 0;

    private Runnable binder = null;
    private Consumer<Object> uploader = null;

    public Texture init() {

        this.textureHandle = GL46.glGenTextures();
        return this;

    }

    public TextureUploader upload() {
        return new TextureUploader();
    }

    public int handle() {
        return textureHandle;
    }

    public int w() {
        return width;
    }

    public int h() {
        return height;
    }

    public int d() {
        return depth;
    }

    public StorageType storageType() {
        return storageType;
    }

    public TextureType textureType() {
        return textureType;
    }

    public Texture bind() {
        this.binder.run();
        return this;
    }

    public Texture bind(int unit) {
        GL46.glActiveTexture(GL46.GL_TEXTURE0 + unit);
        this.binder.run();
        return this;
    }

    public enum StorageType {
        RGBA_32F(GL46.GL_RGBA32F, GL46.GL_RGBA, GL46.GL_FLOAT, 4),
        RGBA_8UI(GL46.GL_RGBA8UI, GL46.GL_RGBA, GL46.GL_UNSIGNED_INT, 4),
        R_32F(GL46.GL_R32F, GL46.GL_RED, GL46.GL_FLOAT, 1),
        R_32I(GL46.GL_R32I, GL46.GL_RED, GL46.GL_INT, 1);

        public final int internalFormat;
        public final int format;
        public final int type;
        public final int numComponents;

        StorageType(int internalFormat, int format, int type, int numComponents) {
            this.internalFormat = internalFormat;
            this.format = format;
            this.type = type;
            this.numComponents = numComponents;
        }
    }

    public enum TextureType {
        Tex1D(GL46.GL_TEXTURE_1D), Tex2D(GL46.GL_TEXTURE_2D), Tex3D(GL46.GL_TEXTURE_3D);

        public final int target;

        TextureType(int target) {
            this.target = target;
        }
    }

    public class TextureUploader {

        private void copyRegion(
                FloatBuffer from,
                FloatBuffer to,
                Vector3i offset, Vector3i region, Vector3i srcDimensions,
                int elements
        ) {

            final int topLeft =
                    offset.z * srcDimensions.y * srcDimensions.x + offset.y * srcDimensions.x
                            + offset.x;

            for (int z = 0; z < region.z; z++) {
                for (int y = 0; y < region.y; y++) {
                    for (int x = 0; x < region.x; x++) {
                        final int t = elements * (topLeft + x + y * srcDimensions.x
                                + z * srcDimensions.y * srcDimensions.x);
                        for (int i = 0; i < elements; i++) {
                            to.put(from.get(t + i));
                        }
                    }
                }
            }

        }

        private void copyRegion(
                IntBuffer from,
                IntBuffer to,
                Vector3i offset, Vector3i region, Vector3i srcDimensions,
                int elements
        ) {

            final int topLeft =
                    offset.z * srcDimensions.y * srcDimensions.x + offset.y * srcDimensions.x
                            + offset.x;

            for (int z = 0; z < region.z; z++) {
                for (int y = 0; y < region.y; y++) {
                    for (int x = 0; x < region.x; x++) {
                        final int t = elements * (topLeft + x + y * srcDimensions.x
                                + z * srcDimensions.y * srcDimensions.x);
                        for (int i = 0; i < elements; i++) {
                            to.put(from.get(t + i));
                        }
                    }
                }
            }

        }

        public TextureUploader make1D() {
            textureType = TextureType.Tex1D;
            binder = () -> GL46.glBindTexture(TextureType.Tex1D.target, textureHandle);
            uploader = (data) -> {
                if (storageType.type == GL46.GL_FLOAT) {
                    GL46.glTexImage1D(
                            textureType.target, 0, storageType.internalFormat, width, height,
                            storageType.format, storageType.type, (FloatBuffer) data
                    );
                } else {
                    GL46.glTexImage1D(
                            textureType.target, 0, storageType.internalFormat, width, 0,
                            storageType.format, storageType.type, (IntBuffer) data
                    );
                }
            };
            return this;
        }

        public TextureUploader make2D() {
            textureType = TextureType.Tex2D;
            binder = () -> GL46.glBindTexture(TextureType.Tex2D.target, textureHandle);
            uploader = (data) -> {
                if (storageType.type == GL46.GL_FLOAT) {
                    GL46.glTexImage2D(
                            textureType.target, 0, storageType.internalFormat, width,
                            height, 0,
                            storageType.format, storageType.type, (FloatBuffer) data
                    );
                } else {
                    GL46.glTexImage2D(
                            textureType.target, 0, storageType.internalFormat, width,
                            height, 0,
                            storageType.format, storageType.type, (IntBuffer) data
                    );
                }
            };
            return this;
        }

        public TextureUploader make3D() {
            textureType = TextureType.Tex3D;
            binder = () -> GL46.glBindTexture(TextureType.Tex3D.target, textureHandle);
            uploader = (data) -> {
                if (storageType.type == GL46.GL_FLOAT) {
                    GL46.glTexImage3D(
                            textureType.target, 0, storageType.internalFormat, width,
                            height, depth, 0,
                            storageType.format, storageType.type, (FloatBuffer) data
                    );
                } else {
                    GL46.glTexImage3D(
                            textureType.target, 0, storageType.internalFormat, width,
                            height, depth, 0,
                            storageType.format, storageType.type, (IntBuffer) data
                    );
                }
            };
            return this;
        }

        public Texture fromRgba(
                int offset,
                int region,
                int srcLength,
                FloatBuffer data
        ) {

            width = region;
            storageType = StorageType.RGBA_32F;
            make1D();

            final FloatBuffer textureData = BufferUtils.createFloatBuffer(4 * region);

            copyRegion(
                    data,
                    textureData,
                    new Vector3i(offset, 0, 0),
                    new Vector3i(region, 1, 1),
                    new Vector3i(srcLength, 1, 1),
                    4
            );

            textureData.flip();

            binder.run();
            uploader.accept(textureData);

            return Texture.this;

        }

        public Texture fromRgba(
                Vector2i offset,
                Vector2i region,
                Vector2i srcDimensions,
                FloatBuffer data
        ) {

            width = region.x;
            height = region.y;
            storageType = StorageType.RGBA_32F;
            make2D();

            final FloatBuffer textureData = BufferUtils.createFloatBuffer(4 * region.x * region.y);

            copyRegion(
                    data,
                    textureData,
                    new Vector3i(offset, 0),
                    new Vector3i(region, 1),
                    new Vector3i(srcDimensions, 1),
                    4
            );

            textureData.flip();

            binder.run();
            uploader.accept(textureData);

            return Texture.this;

        }

        public Texture fromRgba(
                Vector3i offset,
                Vector3i region,
                Vector3i srcDimensions,
                FloatBuffer data
        ) {

            width = region.x;
            height = region.y;
            depth = region.z;
            storageType = StorageType.RGBA_32F;
            make3D();

            final FloatBuffer textureData = BufferUtils
                    .createFloatBuffer(4 * region.x * region.y * region.z);

            copyRegion(
                    data,
                    textureData,
                    offset,
                    region,
                    srcDimensions,
                    4
            );

            textureData.flip();

            binder.run();
            uploader.accept(textureData);

            return Texture.this;

        }

        public Texture fromRgba(
                int offset,
                int region,
                int srcLength,
                IntBuffer data
        ) {

            width = region;
            storageType = StorageType.RGBA_8UI;
            make1D();

            final IntBuffer textureData = BufferUtils.createIntBuffer(4 * region);

            copyRegion(
                    data,
                    textureData,
                    new Vector3i(offset, 0, 0),
                    new Vector3i(region, 1, 1),
                    new Vector3i(srcLength, 1, 1),
                    4
            );

            textureData.flip();

            binder.run();
            uploader.accept(textureData);

            return Texture.this;

        }

        public Texture fromRgba(
                Vector2i offset,
                Vector2i region,
                Vector2i srcDimensions,
                IntBuffer data
        ) {

            width = region.x;
            height = region.y;
            storageType = StorageType.RGBA_8UI;
            make2D();

            final IntBuffer textureData = BufferUtils.createIntBuffer(4 * region.x * region.y);

            copyRegion(
                    data,
                    textureData,
                    new Vector3i(offset, 0),
                    new Vector3i(region, 1),
                    new Vector3i(srcDimensions, 1),
                    4
            );

            textureData.flip();

            binder.run();
            uploader.accept(textureData);

            return Texture.this;

        }

        public Texture fromRgba(
                Vector3i offset,
                Vector3i region,
                Vector3i srcDimensions,
                IntBuffer data
        ) {

            width = region.x;
            height = region.y;
            storageType = StorageType.RGBA_8UI;
            make3D();

            final IntBuffer textureData = BufferUtils
                    .createIntBuffer(4 * region.x * region.y * region.z);

            copyRegion(
                    data,
                    textureData,
                    offset,
                    region,
                    srcDimensions,
                    4
            );

            textureData.flip();

            binder.run();
            uploader.accept(textureData);

            return Texture.this;

        }

        public Texture fromSingle(
                int offset,
                int region,
                int srcLength,
                FloatBuffer data
        ) {

            width = region;
            storageType = StorageType.R_32F;
            make1D();

            final FloatBuffer textureData = BufferUtils.createFloatBuffer(region);

            copyRegion(
                    data,
                    textureData,
                    new Vector3i(offset, 1, 0),
                    new Vector3i(region, 1, 1),
                    new Vector3i(srcLength, 1, 1),
                    4
            );

            textureData.flip();

            binder.run();
            uploader.accept(textureData);

            return Texture.this;

        }

        public Texture fromSingle(
                Vector2i offset,
                Vector2i region,
                Vector2i srcDimensions,
                FloatBuffer data
        ) {

            width = region.x;
            height = region.y;
            storageType = StorageType.R_32F;
            make2D();

            final FloatBuffer textureData = BufferUtils.createFloatBuffer(region.x * region.y);

            copyRegion(
                    data,
                    textureData,
                    new Vector3i(offset, 0),
                    new Vector3i(region, 1),
                    new Vector3i(srcDimensions, 1),
                    4
            );

            textureData.flip();

            binder.run();
            uploader.accept(textureData);

            return Texture.this;

        }

        public Texture fromSingle(
                Vector3i offset,
                Vector3i region,
                Vector3i srcDimensions,
                FloatBuffer data
        ) {

            width = region.x;
            height = region.y;
            depth = region.z;
            storageType = StorageType.R_32F;
            make3D();

            final FloatBuffer textureData = BufferUtils
                    .createFloatBuffer(region.x * region.y * region.z);

            copyRegion(
                    data,
                    textureData,
                    offset,
                    region,
                    srcDimensions,
                    4
            );

            textureData.flip();

            binder.run();
            uploader.accept(textureData);

            return Texture.this;

        }

        public Texture fromSingle(
                int offset,
                int region,
                int srcLength,
                IntBuffer data
        ) {

            width = region;
            storageType = StorageType.R_32I;
            make1D();

            final IntBuffer textureData = BufferUtils.createIntBuffer(region);

            copyRegion(
                    data,
                    textureData,
                    new Vector3i(offset, 0, 0),
                    new Vector3i(region, 1, 1),
                    new Vector3i(srcLength, 1, 1),
                    1
            );

            textureData.flip();

            binder.run();
            uploader.accept(textureData);

            return Texture.this;

        }

        public Texture fromSingle(
                Vector2i offset,
                Vector2i region,
                Vector2i srcDimensions,
                IntBuffer data
        ) {

            width = region.x;
            height = region.y;
            storageType = StorageType.R_32I;
            make2D();

            final IntBuffer textureData = BufferUtils.createIntBuffer(region.x * region.y);

            copyRegion(
                    data,
                    textureData,
                    new Vector3i(offset, 0),
                    new Vector3i(region, 1),
                    new Vector3i(srcDimensions, 1),
                    1
            );

            textureData.flip();

            binder.run();
            uploader.accept(textureData);

            return Texture.this;

        }

        public Texture fromSingle(
                Vector3i offset,
                Vector3i region,
                Vector3i srcDimensions,
                IntBuffer data
        ) {

            width = region.x;
            height = region.y;
            depth = region.z;
            storageType = StorageType.R_32I;
            make3D();

            final IntBuffer textureData = BufferUtils
                    .createIntBuffer(region.x * region.y * region.z);

            copyRegion(
                    data,
                    textureData,
                    offset,
                    region,
                    srcDimensions,
                    1
            );

            textureData.flip();

            binder.run();
            uploader.accept(textureData);

            return Texture.this;

        }

        private BufferedImage toArgbImage(BufferedImage img) {
            BufferedImage out = new BufferedImage(
                    img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = out.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            return out;
        }

        public Texture fromResourceRgba(String resourceName) {

            try {

                BufferedImage img = ImageIO.read(
                        Texture.class.getClassLoader()
                                .getResource(resourceName)
                );

                if (img.getType() != BufferedImage.TYPE_INT_ARGB) {
                    img = toArgbImage(img);
                }

                width = img.getWidth();
                height = img.getHeight();
                storageType = StorageType.RGBA_8UI;
                make2D();

                IntBuffer pixelBuffer = BufferUtils.createIntBuffer(4 * width * height);

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {

                        int rgba = img.getRGB(x, y);
                        int blue = rgba & 0xff;
                        int green = (rgba & 0xff00) >> 8;
                        int red = (rgba & 0xff0000) >> 16;
                        int alpha = (rgba & 0xff000000) >>> 24;

                        pixelBuffer.put(red).put(green).put(blue).put(alpha);

                    }
                }

                binder.run();
                uploader.accept(pixelBuffer.flip());

                return Texture.this;

            } catch (Exception exc) {
                exc.printStackTrace();
                throw new RuntimeException("Could not load texture " + resourceName, exc);
            }

        }

    }

}

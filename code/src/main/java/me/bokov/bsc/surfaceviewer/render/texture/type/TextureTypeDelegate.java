package me.bokov.bsc.surfaceviewer.render.texture.type;

public abstract class TextureTypeDelegate<B, T> {

    public abstract B createBuffer(int[] dimensions);

    public abstract B copyData(B targetBuffer, int[] dimensions, T[] data);

    public abstract void uploadData(B dataBuffer, int[] dimensions);

    public abstract void downloadData(B targetBuffer);

}

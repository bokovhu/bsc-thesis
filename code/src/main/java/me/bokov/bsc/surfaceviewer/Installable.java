package me.bokov.bsc.surfaceviewer;

public interface Installable<TInto> {

    void install(TInto parent);

    void uninstall();

}

package me.bokov.bsc.v2;

public interface Installable <TInto> {

    void install(TInto parent);
    void uninstall();

}

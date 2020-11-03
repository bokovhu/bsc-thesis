package me.bokov.bsc.surfaceviewer;

import java.io.Serializable;

public interface Configuration {

    <T extends Serializable> T get(Property<T> prop);

    <T extends Serializable> T get(Property<T> prop, T defaultValue);

    <T extends Serializable> T get(String group, String name, Class<T> propClass);

    <T extends Serializable> T get(String group, String name, Class<T> propClass, T defaultValue);

    <T extends Serializable> T get(String group, String name);

    <T extends Serializable> T get(String group, String name, T defaultValue);

}

package me.bokov.bsc.v2;

public interface Configuration {

    <T> T get(Property<T> prop);

    <T> T get(Property<T> prop, T defaultValue);

    <T> T get(String group, String name, Class<T> propClass);

    <T> T get(String group, String name, Class<T> propClass, T defaultValue);

    <T> T get(String group, String name);

    <T> T get(String group, String name, T defaultValue);

}

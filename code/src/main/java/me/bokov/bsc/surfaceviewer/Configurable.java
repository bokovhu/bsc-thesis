package me.bokov.bsc.surfaceviewer;

import java.io.Serializable;
import java.util.*;

public interface Configurable {

    List<Property<? extends Serializable>> getConfigurationProperties();

}

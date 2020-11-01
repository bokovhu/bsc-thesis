package me.bokov.bsc.v2.view;

import me.bokov.bsc.v2.Configurable;
import me.bokov.bsc.v2.Installable;
import me.bokov.bsc.v2.Scene;
import me.bokov.bsc.v2.View;

public interface Renderer extends Installable<View>, Configurable {

    void render(Scene scene);

}

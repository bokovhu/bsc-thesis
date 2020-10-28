package me.bokov.bsc.surfaceviewer.render.text;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Glyph {

    private final int charCode;
    private final int u, v;
    private final int xoff, yoff;
    private final int xadv;
    private final int w, h;
    private final int page;

}

package me.bokov.bsc.surfaceviewer.surfacelang;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

import java.util.*;

@Getter
@Builder
@ToString
public class ParsedComponent <T> {

    @ToString.Exclude
    private T component;
    @Singular
    private List<String> errors;

}

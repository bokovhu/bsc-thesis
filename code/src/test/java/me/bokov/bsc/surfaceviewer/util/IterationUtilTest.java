package me.bokov.bsc.surfaceviewer.util;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class IterationUtilTest {

    @Test
    public void iterateDimensionsLexicographicallyWorks() {

        List<Integer> result = new ArrayList<>();

        IterationUtil.iterateDimensionsLexicographically(
                4,
                new int[]{3, 1, 2, 2},
                (c, idx) -> result.add(idx)
        );

        assertEquals(
                List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
                result
        );

    }

}
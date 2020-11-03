package me.bokov.bsc.surfaceviewer.util;

import java.util.*;
import java.util.function.*;

public final class IterationUtil {

    private IterationUtil() {}

    public static void iterateDimensionsLexicographically(
            int numDimensions,
            int[] bounds,
            BiConsumer<int[], Integer> iterationConsumer
    ) {


        Deque<Integer> dimCoords = new ArrayDeque<>();

        int idx = 0;

        for (int dim = 0; dim < numDimensions; dim++) {
            dimCoords.addLast(0);
        }

        int[] iterationCoords = new int[numDimensions];

        while (!dimCoords.isEmpty()) {

            int curr = dimCoords.removeLast();

            int currDimension = dimCoords.size();
            int dimensionMax = bounds[currDimension];
            if (curr == dimensionMax) {
                // Skip this dimension, next should be proceeded
                continue;
            } else {

                if (currDimension == numDimensions - 1) {

                    // In the last possible dimension, we invoke the consumer
                    Iterator<Integer> dimIt = dimCoords.iterator();
                    for (int i = 0; i < dimCoords.size(); i++) {
                        iterationCoords[i] = dimIt.next();
                    }
                    iterationCoords[numDimensions - 1] = curr;
                    iterationConsumer.accept(iterationCoords, idx++);

                    if (curr + 1 < dimensionMax) {
                        // Add the next coordinate back
                        dimCoords.addLast(curr + 1);
                    }

                } else {

                    if (curr + 1 < dimensionMax) {
                        // Add the next coordinate back, and create the next lexicographical iteration

                        dimCoords.addLast(curr + 1);
                        // This dimension was bumped, every other dimension is set to 0
                        for (int dim = currDimension; dim < numDimensions - 1; dim++) {
                            dimCoords.addLast(0);
                        }

                    }

                }

            }

        }

    }

}

package me.bokov.bsc.surfaceviewer.render.text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.bokov.bsc.surfaceviewer.render.Texture;

public class TextureFont {

    private FontInstance defaultInstance = null;
    private final List<FontInstance> instances = new ArrayList<>();

    private Map<String, String> lineToMap(String fntLine) {
        return Arrays.stream(fntLine.split(" ")).map(s -> s.split("="))
                .filter(arr -> arr.length == 2)
                .collect(
                        Collectors.groupingBy(
                                a -> a[0],
                                Collectors.mapping(
                                        a -> a[1],
                                        Collectors.reducing("", (a, b) -> b)
                                )
                        )
                );
    }

    private void loadFnt(String fntResource) {

        int fntSize = 0;
        String fntName = null;
        Map<Integer, String> fntImageFiles = new HashMap<>();
        List<Map<String, String>> fntChars = new ArrayList<>();

        try (final InputStream fntInput = TextureFont.class.getClassLoader()
                .getResourceAsStream(fntResource);
                final InputStreamReader fntInputReader = new InputStreamReader(fntInput);
                BufferedReader fntReader = new BufferedReader(fntInputReader)) {

            String fntLine = null;
            while ((fntLine = fntReader.readLine()) != null) {

                if (fntLine.startsWith("info ")) {
                    final var fntInfo = lineToMap(fntLine.substring("info ".length()));

                    fntSize = Integer.parseInt(fntInfo.get("size"));
                    fntName = fntInfo.getOrDefault("face", "<UNKNOWN>");
                } else if (fntLine.startsWith("page ")) {
                    final var pageInfo = lineToMap(fntLine.substring("page ".length()));
                    final var resourcePath = Path.of("", fntResource.split("/"));
                    final var filePath = pageInfo.get("file");
                    final var texturePath = resourcePath.getParent()
                            .resolve(filePath.substring(1, filePath.length() - 1));
                    fntImageFiles.put(
                            Integer.parseInt(pageInfo.getOrDefault("id", "0")),
                            texturePath.toString().replaceAll("\\\\", "/")
                    );
                } else if (fntLine.startsWith("char ")) {
                    final var charInfo = lineToMap(fntLine.substring("char ".length()));
                    fntChars.add(charInfo);
                }

            }

            System.out.println(
                    "FNT loading statistics\n" +
                            "    Name: " + fntName + "\n" +
                            "    Size: " + fntSize + "\n" +
                            "    Number of images: " + fntImageFiles.size() + "\n" +
                            "    Number of glyphs: " + fntChars.size()
            );

            Map<Integer, Texture> loadedPages = new HashMap<>();
            for (Integer page : fntImageFiles.keySet()) {
                loadedPages.put(
                        page,
                        new Texture()
                                .init()
                                .upload().fromResourceRgba(fntImageFiles.get(page))
                );
            }

            FontInstance fontInstance = new FontInstance(
                    this,
                    fntSize,
                    loadedPages
            );

            for (var charInfo : fntChars) {
                fontInstance.glyph(
                        new Glyph(
                                Integer.parseInt(charInfo.get("id")),
                                Integer.parseInt(charInfo.get("x")),
                                Integer.parseInt(charInfo.get("y")),
                                Integer.parseInt(charInfo.get("xoffset")),
                                Integer.parseInt(charInfo.get("yoffset")),
                                Integer.parseInt(charInfo.get("xadvance")),
                                Integer.parseInt(charInfo.get("width")),
                                Integer.parseInt(charInfo.get("height")),
                                Integer.parseInt(charInfo.get("page"))
                        )
                );
            }

            instances.add(fontInstance);

            if (defaultInstance == null) {
                defaultInstance = fontInstance;
            }

        } catch (Exception exc) {
            throw new RuntimeException("Could not load font from resource " + fntResource, exc);
        }

    }

    public TextureFont load(String... args) {
        for (String r : args) {
            loadFnt(r);
        }
        return this;
    }

    public TextureFont chooseDefault(int size) {
        defaultInstance = instances.stream().filter(i -> i.getSize() == size)
                .findFirst().orElse(null);
        return this;
    }

    public FontInstance forSize(int size) {
        return instances.stream().filter(i -> i.getSize() == size)
                .findFirst().orElse(null);
    }

    public FontInstance getDefault() {
        return defaultInstance;
    }

}

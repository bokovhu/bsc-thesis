package me.bokov.bsc.surfaceviewer.view.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import me.bokov.bsc.surfaceviewer.render.ShaderProgram;
import me.bokov.bsc.surfaceviewer.util.ResourceUtil;
import me.bokov.bsc.surfaceviewer.view.AppView;

public class ShaderManager {

    private final AppView view;

    private Map<String, ShaderResources> resourcesByName = new HashMap<>();
    private Map<String, ShaderProgram> programsByName = new HashMap<>();

    public ShaderManager(AppView view) {
        this.view = view;
    }

    public Optional<ShaderProgram> program(String name) {
        return Optional.ofNullable(programsByName.getOrDefault(name, null));
    }

    public ShaderLoaderBuilder load(String name) {
        return new ShaderLoaderBuilder(name);
    }

    public Optional<ShaderProgram> reload(String name) {

        if (this.programsByName.containsKey(name)) {

            final ShaderProgram program = programsByName.get(name);
            final ShaderResources resources = resourcesByName.get(name);

            ShaderLoaderBuilder loader = load(name);

            if (resources != null) {
                if (resources.vertexResource != null) {
                    loader.vertexFromResource(resources.vertexResource);
                } else if (program.vertexSource() != null) {
                    loader.vertexFromSource(program.vertexSource());
                }

                if (resources.fragmentResource != null) {
                    loader.fragmentFromResource(resources.fragmentResource);
                } else if (program.fragmentSource() != null) {
                    loader.fragmentFromSource(program.fragmentSource());
                }

                if (resources.geometryResource != null) {
                    loader.geometryFromResource(resources.geometryResource);
                } else if (program.geometrySource() != null) {
                    loader.geometryFromSource(program.geometrySource());
                }

                if (resources.tessellationControlResource != null) {
                    loader.tessellationControlFromResource(resources.tessellationControlResource);
                } else if (program.tessellationControlSource() != null) {
                    loader.tessellationControlFromSource(program.tessellationControlSource());
                }

                if (resources.tessellationEvaluationResource != null) {
                    loader.tessellationEvaluationFromResource(
                            resources.tessellationEvaluationResource);
                } else if (program.tessellationEvaluationSource() != null) {
                    loader.tessellationEvaluationFromSource(program.tessellationEvaluationSource());
                }
            } else {
                if (program.vertexSource() != null) {
                    loader.vertexFromSource(program.vertexSource());
                }
                if (program.fragmentSource() != null) {
                    loader.fragmentFromSource(program.fragmentSource());
                }
                if (program.geometrySource() != null) {
                    loader.geometryFromSource(program.geometrySource());
                }
                if (program.tessellationControlSource() != null) {
                    loader.tessellationControlFromSource(program.tessellationControlSource());
                }
                if (program.tessellationEvaluationSource() != null) {
                    loader.tessellationEvaluationFromSource(program.tessellationEvaluationSource());
                }
            }

            program.tearDown();

            return Optional.of(loader.end());

        }

        return Optional.empty();

    }

    public ShaderManager reloadAll() {
        final Set<String> namesOfProgramsToReload = new HashSet<>(this.programsByName.keySet());
        for(String programName : namesOfProgramsToReload) {
            this.reload(programName);
        }
        return this;
    }

    public void tearDown() {

        programsByName.forEach(
                (key, value) -> value.tearDown()
        );

        programsByName.clear();
        resourcesByName.clear();

    }

    public class ShaderLoaderBuilder {

        private final String programName;
        private String vertexShaderSource = null;
        private String fragmentShaderSource = null;
        private String geometryShaderSource = null;
        private String tessellationControlShaderSource = null;
        private String tessellationEvaluationShaderSource = null;

        private String vertexShaderResourceName = null;
        private String fragmentShaderResourceName = null;
        private String geometryShaderResourceName = null;
        private String tessellationControlShaderResourceName = null;
        private String tessellationEvaluationShaderResourceName = null;

        public ShaderLoaderBuilder(String programName) {
            this.programName = programName;
        }

        public ShaderLoaderBuilder vertexFromResource(String resourceName) {
            this.vertexShaderSource = ResourceUtil.readResource(resourceName);
            this.vertexShaderResourceName = resourceName;
            return this;
        }

        public ShaderLoaderBuilder vertexFromSource(String src) {
            this.vertexShaderSource = src;
            return this;
        }

        public ShaderLoaderBuilder fragmentFromResource(String resourceName) {
            this.fragmentShaderSource = ResourceUtil.readResource(resourceName);
            this.fragmentShaderResourceName = resourceName;
            return this;
        }

        public ShaderLoaderBuilder fragmentFromSource(String src) {
            this.fragmentShaderSource = src;
            return this;
        }

        public ShaderLoaderBuilder geometryFromResource(String resourceName) {
            this.geometryShaderSource = ResourceUtil.readResource(resourceName);
            this.geometryShaderResourceName = resourceName;
            return this;
        }

        public ShaderLoaderBuilder geometryFromSource(String src) {
            this.geometryShaderSource = src;
            return this;
        }

        public ShaderLoaderBuilder tessellationControlFromResource(String resourceName) {
            this.tessellationControlShaderSource = ResourceUtil.readResource(resourceName);
            this.tessellationControlShaderResourceName = resourceName;
            return this;
        }

        public ShaderLoaderBuilder tessellationControlFromSource(String src) {
            this.tessellationControlShaderSource = src;
            return this;
        }

        public ShaderLoaderBuilder tessellationEvaluationFromResource(String resourceName) {
            this.tessellationEvaluationShaderSource = ResourceUtil.readResource(resourceName);
            this.tessellationEvaluationShaderResourceName = resourceName;
            return this;
        }

        public ShaderLoaderBuilder tessellationEvaluationFromSource(String src) {
            this.tessellationEvaluationShaderSource = src;
            return this;
        }

        public ShaderProgram end() {

            ShaderProgram program = new ShaderProgram();
            program.init();

            if (vertexShaderSource != null) {
                program.attachVertexShaderFromSource(vertexShaderSource);
            }

            if (fragmentShaderSource != null) {
                program.attachFragmentShaderFromSource(fragmentShaderSource);
            }

            if (geometryShaderSource != null) {
                program.attachGeometryShaderFromSource(geometryShaderSource);
            }

            if (tessellationControlShaderSource != null) {
                program.attachTessellationControlShaderFromSource(tessellationControlShaderSource);
            }

            if (tessellationEvaluationShaderSource != null) {
                program.attachTessellationEvaluationShaderFromSource(
                        tessellationEvaluationShaderSource);
            }

            program.linkAndValidate();

            programsByName.put(programName, program);
            resourcesByName.put(programName, new ShaderResources(
                    vertexShaderResourceName,
                    fragmentShaderResourceName,
                    geometryShaderResourceName,
                    tessellationControlShaderResourceName,
                    tessellationEvaluationShaderResourceName
            ));

            return program;
        }

    }

    private class ShaderResources {

        private final String vertexResource;
        private final String fragmentResource;
        private final String geometryResource;
        private final String tessellationControlResource;
        private final String tessellationEvaluationResource;

        private ShaderResources(String vertexResource, String fragmentResource,
                String geometryResource,
                String tessellationControlResource,
                String tessellationEvaluationResource
        ) {
            this.vertexResource = vertexResource;
            this.fragmentResource = fragmentResource;
            this.geometryResource = geometryResource;
            this.tessellationControlResource = tessellationControlResource;
            this.tessellationEvaluationResource = tessellationEvaluationResource;
        }
    }

}

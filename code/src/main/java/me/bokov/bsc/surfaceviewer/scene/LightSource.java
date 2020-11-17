package me.bokov.bsc.surfaceviewer.scene;

import lombok.Data;
import lombok.experimental.Accessors;
import me.bokov.bsc.surfaceviewer.glsl.GLSLStatement;
import org.joml.Vector3f;

import java.io.Serializable;
import java.util.*;

public interface LightSource extends Serializable {

    int getId();

    Vector3f getEnergy();
    LightSource setEnergy(float r, float g, float b);
    LightSource setEnergy(Vector3f e);

    List<GLSLStatement> evaluateContribution(ContributionEvaluationContext context);

    @Data
    @Accessors(chain = true)
    class ContributionEvaluationContext implements Serializable {

        private String contextId;
        private String hitPoint;
        private String hitNormal;
        private String diffuseColor;
        private String shininess;
        private String eye;

        public String getResult() {
            return contextId + "_Result";
        }

        public ContributionEvaluationContext branch(String c) {
            return new ContributionEvaluationContext()
                    .setContextId(contextId + "_" + c)
                    .setDiffuseColor(diffuseColor).setShininess(shininess)
                    .setHitPoint(hitPoint).setHitNormal(hitNormal)
                    .setEye(eye);
        }

    }

}

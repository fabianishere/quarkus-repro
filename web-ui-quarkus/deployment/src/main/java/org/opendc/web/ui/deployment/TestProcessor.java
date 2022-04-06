/*
 * Copyright (c) 2022 AtLarge Research
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.opendc.web.ui.deployment;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.ShutdownContextBuildItem;
import io.quarkus.maven.dependency.GACT;
import io.quarkus.vertx.http.deployment.HttpRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.quarkus.vertx.http.deployment.webjar.WebJarBuildItem;
import io.quarkus.vertx.http.deployment.webjar.WebJarResultsBuildItem;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import org.opendc.web.ui.runtime.TestRecorder;

public class TestProcessor {

    private static final String FEATURE = "test";
    private static final GACT UI_WEBJAR_ARTIFACT_KEY = new GACT("org.example", "web-ui", null, "jar");
    private static final String UI_WEBJAR_STATIC_RESOURCES_PATH = "META-INF/resources";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BuildStep
    public FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    public WebJarBuildItem buildWebJar() {
        return WebJarBuildItem.builder()
            .artifactKey(UI_WEBJAR_ARTIFACT_KEY)
            .root(UI_WEBJAR_STATIC_RESOURCES_PATH)
            .onlyCopyNonArtifactFiles(false)
            .useDefaultQuarkusBranding(false)
            .build();
    }


    @BuildStep
    @Record(ExecutionTime.RUNTIME_INIT)
    public void registerOpenDCUiHandler(TestRecorder recorder,
                                        BuildProducer<RouteBuildItem> routes,
                                        HttpRootPathBuildItem httpRootPathBuildItem,
                                        WebJarResultsBuildItem webJarResultsBuildItem,
                                        ShutdownContextBuildItem shutdownContext) {

        WebJarResultsBuildItem.WebJarResult result = webJarResultsBuildItem.byArtifactKey(UI_WEBJAR_ARTIFACT_KEY);
        if (result == null) {
            return;
        }

        String basePath = httpRootPathBuildItem.resolvePath("/");
        String finalDestination = result.getFinalDestination();

        /* Construct static routes */
        Handler<RoutingContext> staticHandler = recorder.handler(
            finalDestination,
            basePath,
            result.getWebRootConfigurations(),
            shutdownContext
        );

        routes.produce(httpRootPathBuildItem.routeBuilder()
            .route("/")
            .displayOnNotFoundPage("Test UI")
            .routeConfigKey("quarkus.test.path")
            .handler(staticHandler)
            .build());

        routes.produce(httpRootPathBuildItem.routeBuilder()
            .route("*")
            .handler(staticHandler)
            .build());
    }
}

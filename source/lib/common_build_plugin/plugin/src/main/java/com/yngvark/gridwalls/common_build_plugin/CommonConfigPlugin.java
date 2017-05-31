package com.yngvark.gridwalls.common_build_plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.plugins.ApplicationPlugin;
import org.gradle.api.plugins.ApplicationPluginConvention;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.tasks.Copy;
import org.gradle.model.Managed;
import org.gradle.model.Model;
import org.gradle.model.ModelMap;
import org.gradle.model.Mutate;
import org.gradle.model.RuleSource;
import org.gradle.model.Unmanaged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class CommonConfigPlugin implements Plugin<Project> {
    @Override
    public void apply(Project target) {
        target.getPluginManager().apply(ApplicationPlugin.class);
        target.getExtensions().create("commonConfigExecutable", CommonConfigExecutable.class);

        target.afterEvaluate((action) -> {
            String mainClassName = target.getExtensions().getByType(CommonConfigExecutable.class).mainClassName;
            target.getConvention().getPlugin(ApplicationPluginConvention.class).setMainClassName(mainClassName);
        });

        target.getPluginManager().apply(MavenPublishPlugin.class);
    }

    @SuppressWarnings("UnusedDeclaration")
    static class MakeExecutableRules extends RuleSource {
        @Mutate
        void createMakeExecutableTask(ModelMap<Task> tasks) {
            tasks.create("makeExecutable", Copy.class, (copyConfig) -> {
                copyConfig.dependsOn("installDist");

                copyConfig
                        .from("build/install/" + copyConfig.getProject().getName())
                        .into("../container/app");

            });
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    static class MavenPublishRules extends RuleSource {
        @Managed
        interface CommonConfig {
            @Unmanaged SoftwareComponent getComponentToMavenPublish();
            void setComponentToMavenPublish(SoftwareComponent softwareComponent);
        }

        @Model
        void commonConfig(CommonConfig commonConfig) {}

        @Mutate
        void createPublications(PublishingExtension publishing, CommonConfig commonConfig) {
            publishing.publications((publicationContainer) -> {
                publicationContainer.create("somePublish", MavenPublication.class, (publicationConfig) -> {
                    SoftwareComponent softwareComponent = commonConfig.getComponentToMavenPublish();
                    publicationConfig.from(softwareComponent);
                });
            });

            publishing.repositories((repoHandler) -> repoHandler.maven(
                    mavenRepo -> {
                        String mavenRepoUrl = readMavenRepoUrlFromFile();
                        mavenRepo.setUrl(mavenRepoUrl);
                    }
            ));
        }

        private String readMavenRepoUrlFromFile() {
            Logger logger = LoggerFactory.getLogger(getClass());
            logger.debug("Attempting to get maven repo url from file...");
            String resource = "/mavenRepoUrl";

            Properties properties = new Properties();
            try {
                properties.load(getClass().getResourceAsStream(resource));
            } catch (IOException e) {
                throw new RuntimeException("Could not load resource: " + resource);
            }
            String mavenRepoUrl = properties.getProperty("url");
            logger.debug("Result: " + mavenRepoUrl);
            return mavenRepoUrl;
        }
    }
}


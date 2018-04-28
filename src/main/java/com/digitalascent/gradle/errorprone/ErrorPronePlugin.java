package com.digitalascent.gradle.errorprone;

import net.ltgt.gradle.errorprone.ErrorProneBasePlugin;
import net.ltgt.gradle.errorprone.ErrorProneToolChain;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskCollection;
import org.gradle.api.tasks.compile.CompileOptions;
import org.gradle.api.tasks.compile.JavaCompile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ErrorPronePlugin implements Plugin<Project> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void apply(Project project) {
        project.getPlugins().withType(JavaPlugin.class, javaPlugin -> {
            // apply error prone plugin
            project.getPluginManager().apply(net.ltgt.gradle.errorprone.ErrorPronePlugin.class);

            ErrorPronePluginExtension errorPronePluginExtension = new ErrorPronePluginExtension();
            project.getExtensions().add(ErrorPronePluginExtension.class, "errorprone", errorPronePluginExtension);
            configureErrorPronePatterns(errorPronePluginExtension);

            setErrorProneVersion(project);

            final Action<JavaCompile> action = task -> {
                if (!(task.getToolChain() instanceof ErrorProneToolChain)) {
                    return;
                }

                CompileOptions options = task.getOptions();
                configureErrorProne(task, options, errorPronePluginExtension);
            };
            project.afterEvaluate(foo -> {
                final TaskCollection<JavaCompile> javaCompileTasks = project.getTasks().withType(JavaCompile.class);
                javaCompileTasks.all(action);
                javaCompileTasks.whenTaskAdded(action);
            });
        });
    }

    private void configureErrorPronePatterns(ErrorPronePluginExtension errorPronePluginExtension) {
        errorPronePluginExtension.error("AssertFalse");
        errorPronePluginExtension.error("BigDecimalLiteralDouble");
        errorPronePluginExtension.error("ConstructorInvokesOverridable");
        errorPronePluginExtension.error("EmptyTopLevelDeclaration");
        errorPronePluginExtension.error("MissingDefault");
        errorPronePluginExtension.error("NonCanonicalStaticMemberImport");
        errorPronePluginExtension.error("PrimitiveArrayPassedToVarargsMethod");
        errorPronePluginExtension.error("RedundantThrows");
        errorPronePluginExtension.error("StaticQualifiedUsingExpression");
        errorPronePluginExtension.error("StringEquality");
        errorPronePluginExtension.error("UnnecessaryDefaultInEnumSwitch");
        errorPronePluginExtension.error("WildcardImport");
        errorPronePluginExtension.error("MultipleTopLevelClasses");
        errorPronePluginExtension.error("MultiVariableDeclaration");
        errorPronePluginExtension.error("MixedArrayDimensions");
        errorPronePluginExtension.error("MethodCanBeStatic");
        errorPronePluginExtension.error("PrivateConstructorForUtilityClass");
        errorPronePluginExtension.error("PackageLocation");
        errorPronePluginExtension.error("ConstantField");
        errorPronePluginExtension.error("ReturnMissingNullable");
        errorPronePluginExtension.error("FieldMissingNullable");
        errorPronePluginExtension.error("ParameterNotNullable");
        errorPronePluginExtension.error("ConstructorLeaksThis");
        errorPronePluginExtension.error("MultiVariableDeclaration");
        errorPronePluginExtension.error("FieldCanBeFinal");
        errorPronePluginExtension.error("LambdaFunctionalInterface");
        errorPronePluginExtension.error("PackageLocation");
        errorPronePluginExtension.error("RemoveUnusedImports");
        errorPronePluginExtension.error("ReturnMissingNullable");
        errorPronePluginExtension.error("SwitchDefault");
        errorPronePluginExtension.error("ThrowsUncheckedException");
        errorPronePluginExtension.error("TypeParameterNaming");
        errorPronePluginExtension.error("UnnecessaryStaticImport");
        errorPronePluginExtension.error("WildcardImport");
        errorPronePluginExtension.error("UnnecessaryDefaultInEnumSwitch");
        errorPronePluginExtension.error("FunctionalInterfaceClash");
    }

    private void configureErrorProne(JavaCompile task, CompileOptions options, ErrorPronePluginExtension errorPronePluginExtension) {
        List<String> errorProneOptions = new ArrayList<>();

        // overrides
        errorProneOptions.addAll(generateErrorProneCompilerOpts(errorPronePluginExtension.getErrorPatternNames(), "ERROR"));
        errorProneOptions.addAll(generateErrorProneCompilerOpts(errorPronePluginExtension.getWarnPatternNames(), "WARN"));
        errorProneOptions.addAll(generateErrorProneCompilerOpts(errorPronePluginExtension.getDisabledPatternNames(), "OFF"));

        // http://errorprone.info/docs/flags
        if (errorPronePluginExtension.disableWarningsInGeneratedCode()) {
            errorProneOptions.add("-XepDisableWarningsInGeneratedCode");
        }

        errorPronePluginExtension.getExcludedPaths().forEach(excludedPath -> errorProneOptions.add("-XepExcludedPaths:" + excludedPath));

        logger.info("Using error prone options for task {} : {}", task.getName(), errorProneOptions);
        options.getCompilerArgs().addAll( errorProneOptions );

    }

    private List<String> generateErrorProneCompilerOpts(Set<String> patternNames, String level) {
        return patternNames.stream().map(patternName -> "-Xep:" + patternName + ":" + level).collect(Collectors.toList());
    }

    private Configuration getErrorProneConfiguration(Project project) {
        String errorProneConfigurationMame = ErrorProneBasePlugin.CONFIGURATION_NAME;
        return project.getConfigurations().getAt(errorProneConfigurationMame);
    }

    private void setErrorProneVersion(Project project) {
        Configuration errorProneConfiguration = getErrorProneConfiguration(project);
        String errorProneDependency = "com.google.errorprone:error_prone_core:2.3.1";
        errorProneConfiguration.getDependencies().add(project.getDependencies().create(errorProneDependency));
    }
}
package com.digitalascent.gradle.errorprone;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ErrorPronePluginExtension {
    private Set<String> errorPatternNames = new TreeSet<>();
    private Set<String> warnPatternNames = new TreeSet<>();
    private Set<String> disabledPatternNames = new TreeSet<>();

    public void setDisableWarningsInGeneratedCode(boolean disableWarningsInGeneratedCode) {
        this.disableWarningsInGeneratedCode = disableWarningsInGeneratedCode;
    }

    private boolean disableWarningsInGeneratedCode = true;
    private List<String> excludedPaths = new ArrayList<>();;

    boolean disableWarningsInGeneratedCode() {
        return disableWarningsInGeneratedCode;
    }

    List<String> getExcludedPaths() {
        return excludedPaths;
    }

    public void error( String patternName ) {
        errorPatternNames.add(patternName);
    }

    public void warn( String patternName ) {
        warnPatternNames.add(patternName);
    }

    public void off( String patternName ) {
        disabledPatternNames.add(patternName);
    }

    Set<String> getErrorPatternNames() {
        return errorPatternNames;
    }

    Set<String> getWarnPatternNames() {
        return warnPatternNames;
    }

    Set<String> getDisabledPatternNames() {
        return disabledPatternNames;
    }
}

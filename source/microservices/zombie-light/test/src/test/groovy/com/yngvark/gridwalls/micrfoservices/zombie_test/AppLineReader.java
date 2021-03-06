package com.yngvark.gridwalls.micrfoservices.zombie_test;

import com.yngvark.named_piped_app_runner.NamedPipeProcess;
import groovy.transform.PackageScope;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class AppLineReader {
    public static String readLine(NamedPipeProcess app) {
        return assertTimeoutPreemptively(Duration.ofMillis(2000), app.inputFileLineReader::readLine);
    }
}

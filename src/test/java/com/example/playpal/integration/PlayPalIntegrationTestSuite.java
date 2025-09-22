package com.example.playpal.integration;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.test.context.ActiveProfiles;

@Suite
@ActiveProfiles("test")
@SelectPackages("com.example.playpal.integration")
public class PlayPalIntegrationTestSuite {
}

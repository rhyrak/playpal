package com.example.playpal.unit;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.test.context.ActiveProfiles;

@Suite
@ActiveProfiles("test")
@SelectPackages("com.example.playpal.unit")
public class PlayPalUnitTestSuite {
}

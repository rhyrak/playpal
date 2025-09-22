package com.example.playpal;

import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Suite
@ActiveProfiles("test")
@SelectPackages("com.example.playpal")
@SpringBootTest
class PlayPalApplicationTest {

    @Test
    public void contextLoads() {
    }
}
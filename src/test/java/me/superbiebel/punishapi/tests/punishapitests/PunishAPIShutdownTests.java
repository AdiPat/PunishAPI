package me.superbiebel.punishapi.tests.punishapitests;

import me.superbiebel.punishapi.api.PunishAPI;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PunishAPIShutdownTests {
    
    @Test
    public void readyBooleanShutdownTestTest() {
        PunishAPI api = new PunishAPI();
        api.startup();
        api.shutdown();
        Assertions.assertFalse(api.isReady());
    }
    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5})
    public void multipleShutdownTest(int times) {
        PunishAPI api = new PunishAPI();
        api.startup();
        api.shutdown();
        for (int i = 1; i<=times;i++) {
            Assertions.assertThrows(IllegalStateException.class, api::shutdown);
        }
    }
    @ParameterizedTest
    @ValueSource(ints = {1,2,3,4,5})
    public void multipleKilledShutdownTest(int times) {
        PunishAPI api = new PunishAPI();
        api.startup();
        api.kill();
        for (int i = 1; i<=times;i++) {
            Assertions.assertThrows(IllegalStateException.class, api::kill);
        }
    }
}

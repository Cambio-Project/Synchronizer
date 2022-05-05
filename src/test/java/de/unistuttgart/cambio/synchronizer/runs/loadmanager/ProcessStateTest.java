package de.unistuttgart.cambio.synchronizer.runs.loadmanager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessStateTest {

    @Test
    void toStringTest() {
        for (LoadProcessState state : LoadProcessState.values()) {
            System.out.println(state);
            assertEquals(state.toString(),state.name());
            assertEquals(state+"",state.name());
        }
    }
}
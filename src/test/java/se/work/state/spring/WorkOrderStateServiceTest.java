package se.work.state.spring;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import se.work.state.common.WorkOrderEvent;
import se.work.state.common.WorkOrderState;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static se.work.state.common.WorkOrderEvent.*;
import static se.work.state.common.WorkOrderState.*;

@SpringBootTest
public class WorkOrderStateServiceTest {

    @Autowired
    private WorkOrderStateService workOrderStateService;

    @ParameterizedTest
    @MethodSource("validTransitions")
    public void testValidTransitions(WorkOrderState currentState, WorkOrderEvent event, WorkOrderState expectedState) {
        assertEquals(expectedState, workOrderStateService.applyEvent(currentState, event));
    }

    private static Stream<Arguments> validTransitions() {
        return Stream.of(
                Arguments.of(CREATED, ASSIGN, ASSIGNED),
                Arguments.of(ASSIGNED, REJECT, UNASSIGNED),
                Arguments.of(IN_PROGRESS, COMPLETE, COMPLETED)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidTransitionRequest")
    public void testInvalidTransition(WorkOrderState currentState, WorkOrderEvent event) {
        Exception exception = assertThrows(IllegalStateException.class, () ->
                workOrderStateService.applyEvent(currentState, event)
        );

        assertTrue(exception.getMessage().contains("Invalid transition from"));
    }

    private static Stream<Arguments> invalidTransitionRequest() {
        return Stream.of(
                Arguments.of(CREATED, COMPLETE),
                Arguments.of(COMPLETED, REJECT)
        );
    }
}
package se.work.state.spring;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineEventResult;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import se.work.state.common.WorkOrderEvent;
import se.work.state.common.WorkOrderState;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class WorkOrderStateService {

    private final StateMachine<WorkOrderState, WorkOrderEvent> stateMachine;

    public WorkOrderStateService(StateMachine<WorkOrderState, WorkOrderEvent> stateMachine) {
        this.stateMachine = stateMachine;
        stateMachine.startReactively().block(); // Ensure the state machine is started
    }

    public WorkOrderState applyEvent(WorkOrderState currentState, WorkOrderEvent event) {
        resetStateMachine(currentState);

        Set<WorkOrderState> possibleStatesForCurrentState = getNextPossibleStates(currentState);
        Set<WorkOrderState> possibleStatesForEvent = getNextPossibleStates(event);

        if (Collections.disjoint(possibleStatesForCurrentState, possibleStatesForEvent)) {
            throw new IllegalStateException("Invalid transition from %s using event %s".formatted(currentState, event));
        }

        Message<WorkOrderEvent> message = MessageBuilder.withPayload(event).build();
        Mono<Message<WorkOrderEvent>> messageMono = Mono.just(message);
        Flux<StateMachineEventResult<WorkOrderState, WorkOrderEvent>> resultFlux = stateMachine.sendEvent(messageMono);

        StateMachineEventResult<WorkOrderState, WorkOrderEvent> result = resultFlux.blockFirst();

        if (result == null || result.getResultType() == StateMachineEventResult.ResultType.DENIED) {
            throw new IllegalStateException("Invalid transition from %s using event %s".formatted(currentState, event));
        }

        State<WorkOrderState, WorkOrderEvent> newState = stateMachine.getState();
        return newState != null ? newState.getId() : currentState;
    }

    private Set<WorkOrderState> getNextPossibleStates(WorkOrderState currentState) {
        return stateMachine.getTransitions().stream()
                .filter(t -> t.getSource().getId() == currentState)
                .map(Transition::getTarget)
                .map(State::getId)
                .collect(Collectors.toSet());
    }

    public void resetStateMachine(WorkOrderState currentState) {
        stateMachine.getStateMachineAccessor().doWithAllRegions(accessor ->
                accessor.resetStateMachineReactively(new DefaultStateMachineContext<>(currentState, null, null, null)).block()
        );
    }

    private Set<WorkOrderState> getNextPossibleStates(WorkOrderEvent event) {
        return stateMachine.getTransitions().stream()
                .filter(t -> t.getTrigger().getEvent() == event)
                .map(Transition::getTarget)
                .map(State::getId)
                .collect(Collectors.toSet());
    }
}
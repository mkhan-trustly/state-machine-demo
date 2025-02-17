package se.work.state.custom;

import se.work.state.common.WorkOrderEvent;
import se.work.state.common.WorkOrderState;

import java.util.HashMap;
import java.util.Map;

public class WorkOrderStateService {

    private final Map<WorkOrderState, Map<WorkOrderEvent, WorkOrderState>> stateTransitionMap;

    public WorkOrderStateService() {
        stateTransitionMap = new HashMap<>();

        stateTransitionMap.put(WorkOrderState.CREATED, new HashMap<>() {{
            put(WorkOrderEvent.ASSIGN, WorkOrderState.ASSIGNED);
            put(WorkOrderEvent.REJECT, WorkOrderState.REJECTED);
        }});

        stateTransitionMap.put(WorkOrderState.UNASSIGNED, new HashMap<>() {{
            put(WorkOrderEvent.ASSIGN, WorkOrderState.ASSIGNED);
        }});

        stateTransitionMap.put(WorkOrderState.ASSIGNED, new HashMap<>() {{
            put(WorkOrderEvent.REJECT, WorkOrderState.UNASSIGNED);
            put(WorkOrderEvent.START_PROGRESS, WorkOrderState.IN_PROGRESS);
        }});

        stateTransitionMap.put(WorkOrderState.IN_PROGRESS, new HashMap<>() {{
            put(WorkOrderEvent.COMPLETE, WorkOrderState.COMPLETED);
            put(WorkOrderEvent.TERMINATE, WorkOrderState.TERMINATED);
            put(WorkOrderEvent.CANCEL, WorkOrderState.CANCELLED);
        }});

        stateTransitionMap.put(WorkOrderState.COMPLETED, new HashMap<>() {{
            put(WorkOrderEvent.ARCHIVE, WorkOrderState.ARCHIVED);
        }});
    }

    private boolean canTransition(WorkOrderState currentState, WorkOrderEvent event) {
        return stateTransitionMap.containsKey(currentState) &&
               stateTransitionMap.get(currentState).containsKey(event);
    }

    public WorkOrderState applyEvent(WorkOrderState currentState, WorkOrderEvent event) throws IllegalStateException {
        if (!canTransition(currentState, event)) {
            throw new IllegalStateException("Invalid transition from %s using event %s".formatted(currentState, event));
        }
        return stateTransitionMap.get(currentState).get(event);
    }
}
package se.work.state.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import se.work.state.common.WorkOrderEvent;
import se.work.state.common.WorkOrderState;

import java.util.EnumSet;

@Configuration
public class WorkOrderStateMachineConfig {

    @Bean
    public StateMachine<WorkOrderState, WorkOrderEvent> workOrderStateMachine() throws Exception {
        StateMachineBuilder.Builder<WorkOrderState, WorkOrderEvent> builder = StateMachineBuilder.builder();

        builder.configureStates()
                .withStates()
                .initial(WorkOrderState.CREATED)
                .states(EnumSet.allOf(WorkOrderState.class));

        builder.configureTransitions()
                .withExternal()
                .source(WorkOrderState.CREATED)
                .event(WorkOrderEvent.ASSIGN)
                .target(WorkOrderState.ASSIGNED)

                .and()
                .withExternal()
                .source(WorkOrderState.CREATED)
                .event(WorkOrderEvent.REJECT)
                .target(WorkOrderState.REJECTED)

                .and()
                .withExternal()
                .source(WorkOrderState.UNASSIGNED)
                .event(WorkOrderEvent.ASSIGN)
                .target(WorkOrderState.ASSIGNED)

                .and()
                .withExternal()
                .source(WorkOrderState.UNASSIGNED)
                .event(WorkOrderEvent.REJECT)
                .target(WorkOrderState.REJECTED)

                .and()
                .withExternal()
                .source(WorkOrderState.ASSIGNED)
                .event(WorkOrderEvent.REJECT)
                .target(WorkOrderState.UNASSIGNED)

                .and()
                .withExternal()
                .source(WorkOrderState.IN_PROGRESS)
                .event(WorkOrderEvent.COMPLETE)
                .target(WorkOrderState.COMPLETED)

                .and()
                .withExternal()
                .source(WorkOrderState.IN_PROGRESS)
                .event(WorkOrderEvent.TERMINATE)
                .target(WorkOrderState.TERMINATED)

                .and()
                .withExternal()
                .source(WorkOrderState.IN_PROGRESS)
                .event(WorkOrderEvent.CANCEL)
                .target(WorkOrderState.CANCELLED)

                .and()
                .withExternal()
                .source(WorkOrderState.COMPLETED)
                .event(WorkOrderEvent.ARCHIVE)
                .target(WorkOrderState.ARCHIVED);

        return builder.build();
    }
}
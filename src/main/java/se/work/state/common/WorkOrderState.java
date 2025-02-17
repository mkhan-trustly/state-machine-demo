package se.work.state.common;

public enum WorkOrderState {
    CREATED,
    UNASSIGNED,
    ASSIGNED,
    IN_PROGRESS,
    TERMINATED,
    CANCELLED,
    REJECTED,
    COMPLETED,
    ARCHIVED
}
package se.work.state.common;

public enum WorkOrderEvent {
    ASSIGN,
    START_PROGRESS,
    TERMINATE,
    CANCEL,
    REJECT,
    COMPLETE,
    ARCHIVE
}
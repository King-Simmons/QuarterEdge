package com.quarteredge.core.model;

public enum SessionStatus {
    /**
     * Indicates that the session has not yet started. This status is used to reflect the initial
     * state of the session before it is initialized or started.
     */
    PENDING,
    /** Indicates that the session has started and is currently in progress. */
    STARTED,
    /** Indicates that the session has been successfully completed. */
    COMPLETED,
    /** Indicates that the session has failed due to an error or unexpected condition. */
    FAILED
}

package com.alibaba.metrics;

/**
 * An enum of various reservoir type
 */
public enum ReservoirType {

    /**
     * The exponentially decaying reservoir
     */
    EXPONENTIALLY_DECAYING,
    /**
     * The sliding time window reservoir
     */
    SLIDING_TIME_WINDOW,
    /**
     * The sliding window reservoir
     */
    SLIDING_WINDOW,
    /**
     * The uniform reservoir
     */
    UNIFORM,
    /**
     * The bucket reservoir
     */
    BUCKET

}

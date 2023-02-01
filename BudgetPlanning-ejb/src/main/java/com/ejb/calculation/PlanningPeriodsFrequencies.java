package com.ejb.calculation;

/**
 * PlanningPeriodsFrequencies interface to contain enumiration of possible
 * planning periods frequencies pre-specified in the application.
 */
public interface PlanningPeriodsFrequencies {
    
    /**
     * Enumiration of possible planning periods frequencies pre-specified in 
     * the application.
     */
    enum Frequency {

        WEEKLY("W"),
        MONTHLY("M"),
        DAILY("D");

        private String frequency;

        private Frequency(String frequency) {
            this.frequency = frequency;
        }

        public String getFrequency() {
            return frequency;
        }
    }
}

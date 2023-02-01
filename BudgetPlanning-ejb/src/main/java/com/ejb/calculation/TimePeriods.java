
package com.ejb.calculation;

import java.util.TreeSet;

/**
 * TimePeriods class is used to contain static attributes of application
 * planning time periods.
 */
public class TimePeriods {

    /**
     * TimePeriods class private constructor.
     */
    private TimePeriods() {}

    static String currentPeriodDate;
    static String planningPeriodsFrequency;
    static Integer planningPeriodsHorizon;
    static TreeSet<String> timePeriodDates;

}

package ejb.calculation;

/**
 *
 * @author SoundlyGifted
 */
public interface PlanningPeriodsFrequencies {
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

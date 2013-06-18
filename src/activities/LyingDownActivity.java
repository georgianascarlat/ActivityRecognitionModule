package activities;

import models.Activity;
import models.Posture;


public class LyingDownActivity extends HumanActivity {

    public LyingDownActivity() {

        activityType = Activity.LyingDown;
    }


    /**
     * Get the index of the observation class corresponding
     * to the posture information.
     * <p/>
     * Index 0 - class horizontal
     * Index 1 - class toward horizontal
     * Index 2 - class not horizontal
     *
     * @param posture posture information
     * @return index of the observation class
     */
    @Override
    public int getObservationClass(Posture posture) {

        if (posture.getGeneralPosture() == 2)
            return 0;
        if (posture.getGeneralPosture() == 3
                || posture.getLeftLegSecond() == 2
                || posture.getRightLegSecond() == 2)
            return 1;
        return 2;

    }

    @Override
    public int getObservationDomainSize() {
        return 3;
    }
}

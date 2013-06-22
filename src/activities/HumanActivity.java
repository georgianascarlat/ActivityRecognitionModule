package activities;


import models.Activity;
import models.Posture;
import models.Prediction;
import utils.Pair;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class HumanActivity {

    protected Activity activityType;
    /* tha last position of the user on the grid*/
    protected Pair<Integer, Integer> lastPosition1 = null, lastPosition2 = null;


    public static final Map<Activity, List<String>> activityPosturesMap = initActivitiesMap();
    /* map activity type to activity objects*/
    public static final Map<Activity, HumanActivity> humanActivityMap = HumanActivity.InitHumanActivityMap();


    public abstract int getObservationClass(Posture posture);

    public abstract int getObservationDomainSize();

    public abstract void adjustPredictionUsingRoomModel(Prediction prediction, String skeletonFileName);


    public Activity getActivityType() {
        return activityType;
    }

    public static HumanActivity activityFactory(Activity activity) {


        switch (activity) {

            case Walking:
                return new WalkingActivity();

            case LyingDown:
                return new LyingDownActivity();

            default:
                throw new IllegalArgumentException("No such activity " + activity.getName());
        }
    }

    /**
     * Initialise the mapping from activities to their list of postures of interest.
     *
     * @return mapping from activities to their list of postures of interest
     */
    private static Map<Activity, List<String>> initActivitiesMap() {
        Map<Activity, List<String>> map = new EnumMap<Activity, List<String>>(Activity.class);
        List<String> interestingPostures;

        interestingPostures = new LinkedList<String>();
        interestingPostures.add("generalPosture");
        interestingPostures.add("leftLegFirst");
        interestingPostures.add("rightLegFirst");
        interestingPostures.add("leftLegSecond");
        interestingPostures.add("rightLegSecond");
        map.put(Activity.Walking, interestingPostures);

        interestingPostures = new LinkedList<String>();
        interestingPostures.add("generalPosture");
        map.put(Activity.LyingDown, interestingPostures);

        return map;


    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HumanActivity)) return false;

        HumanActivity that = (HumanActivity) o;

        if (activityType != that.activityType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return activityType != null ? activityType.hashCode() : 0;
    }


    public static Map<Activity, HumanActivity> InitHumanActivityMap() {

        Map<Activity, HumanActivity> humanActivityMap = new EnumMap<Activity, HumanActivity>(Activity.class);

        for (Activity activity : Activity.values()) {
            humanActivityMap.put(activity, activityFactory(activity));
        }

        return humanActivityMap;

    }


}

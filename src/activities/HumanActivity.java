package activities;


import models.Activity;
import models.HMMTypes;
import models.Prediction;
import tracking.User;
import utils.Pair;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class HumanActivity {

    public static final int NUM_SKELETONS = 3;
    protected Activity activityType;
    /* tha last position of the user on the grid*/
    protected Pair<Integer, Integer> lastPosition1 = null, lastPosition2 = null, lastPosition3 = null;
    protected User lastUserSkeletons[] = new User[NUM_SKELETONS];


    /* maps each activity with it's list of postures of interest */
    public static final Map<Activity, List<String>> activityPosturesMap = initActivitiesMap();

    /* union of all postures of interest for all activities */
    public static final List<String> allPosturesOfInterest = initAllPosturesOfInterest();


    /* map activity type to activity objects*/
    public static final Map<Activity, HumanActivity> humanActivityMap = HumanActivity.InitHumanActivityMap();


    public abstract void adjustPredictionUsingRoomModel(Prediction prediction, String skeletonFileName, HMMTypes hmmType);

    protected boolean allSkeletonsAreInitialised() {

        for (int i = 0; i < NUM_SKELETONS; i++) {
            if (lastUserSkeletons[i] == null)
                return false;
        }

        return true;
    }

    protected void updateLastUsers(User user) {
        for (int i = (NUM_SKELETONS - 1); i > 0; i--)
            lastUserSkeletons[i] = lastUserSkeletons[i - 1];

        lastUserSkeletons[0] = user;
    }


    protected void increaseProbability(HMMTypes hmmType, Prediction prediction, double added) {

        int lastIndex = prediction.getPredictions().length - 1;
        int lastPrediction = prediction.getPredictions()[lastIndex];
        double probability = prediction.getProbability();


        switch (hmmType) {

            case SpecialisedHMM:
                if (lastPrediction == 0) {
                    prediction.setProbability(added);
                    prediction.getPredictions()[lastIndex] = 1;
                } else {
                    prediction.setProbability(probability * (1 + added));
                }
                break;
            case GeneralHMM:
                prediction.setProbability(probability * (1 + added));
                break;
            default:
                break;
        }
    }

    protected void zeroProbability(HMMTypes hmmType, Prediction prediction) {

        int lastIndex = prediction.getPredictions().length - 1;

        switch (hmmType) {

            case SpecialisedHMM:
                prediction.getPredictions()[lastIndex] = 0;
                break;
            case GeneralHMM:
                prediction.setProbability(0.0);
                break;
            default:
                break;
        }
    }

    protected void decreaseProbability(HMMTypes hmmType, Prediction prediction, double subbed) {

        int lastIndex = prediction.getPredictions().length - 1;
        int lastPrediction = prediction.getPredictions()[lastIndex];
        double probability = prediction.getProbability();


        switch (hmmType) {

            case SpecialisedHMM:
                if (lastPrediction == 1)
                    prediction.setProbability(probability * (1 - subbed));
                break;
            case GeneralHMM:
                prediction.setProbability(probability * (1 - subbed));
                break;
            default:
                break;
        }
    }


    public static HumanActivity activityFactory(Activity activity) {


        switch (activity) {

            case Walking:
                return new WalkingActivity();

            case LyingDown:
                return new LyingDownActivity();

            case StandingUp:
                return new StandingUpActivity();

            case SittingDown:
                return new SittingDownActivity();

            case Bending:
                return new BendingActivity();

            case Falling:
                return new FallingActivity();

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

        interestingPostures = new LinkedList<String>();
        interestingPostures.add("generalPosture");
        interestingPostures.add("leftLegFirst");
        interestingPostures.add("rightLegFirst");
        interestingPostures.add("leftLegSecond");
        interestingPostures.add("rightLegSecond");
        interestingPostures.add("torsoSecond");
        map.put(Activity.StandingUp, interestingPostures);

        interestingPostures = new LinkedList<String>();
        interestingPostures.add("generalPosture");
        interestingPostures.add("leftLegFirst");
        interestingPostures.add("rightLegFirst");
        interestingPostures.add("leftLegSecond");
        interestingPostures.add("rightLegSecond");
        interestingPostures.add("torsoSecond");
        map.put(Activity.SittingDown, interestingPostures);

        interestingPostures = new LinkedList<String>();
        interestingPostures.add("generalPosture");
        interestingPostures.add("leftLegFirst");
        interestingPostures.add("rightLegFirst");
        interestingPostures.add("leftLegSecond");
        interestingPostures.add("rightLegSecond");
        interestingPostures.add("torsoSecond");
        map.put(Activity.Bending, interestingPostures);

        interestingPostures = new LinkedList<String>();
        interestingPostures.add("generalPosture");
        interestingPostures.add("leftHandFirst");
        interestingPostures.add("rightHandFirst");
        interestingPostures.add("leftHandSecond");
        interestingPostures.add("rightHandSecond");
        interestingPostures.add("torsoSecond");
        map.put(Activity.Falling, interestingPostures);

        return map;


    }

    private static List<String> initAllPosturesOfInterest() {

        List<String> interestingPostures;

        interestingPostures = new LinkedList<String>();
        interestingPostures.add("generalPosture");
        interestingPostures.add("leftLegFirst");
        interestingPostures.add("rightLegFirst");
        interestingPostures.add("leftLegSecond");
        interestingPostures.add("rightLegSecond");

        interestingPostures.add("leftHandFirst");
        interestingPostures.add("rightHandFirst");
        interestingPostures.add("leftHandSecond");
        interestingPostures.add("rightHandSecond");

        interestingPostures.add("torsoSecond");

        return interestingPostures;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HumanActivity)) return false;

        HumanActivity that = (HumanActivity) o;

        return activityType == that.activityType;

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

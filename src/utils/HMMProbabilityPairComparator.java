package utils;


import hmm.HMM;

import java.util.Comparator;

public class HMMProbabilityPairComparator implements Comparator<Pair<HMM, Double>> {
    @Override
    public int compare(Pair<HMM, Double> hmmDoublePair, Pair<HMM, Double> hmmDoublePair2) {
        if (hmmDoublePair.getSecond() < hmmDoublePair2.getSecond())
            return -1;
        if (hmmDoublePair.getSecond() > hmmDoublePair2.getSecond())
            return 1;
        return 0;
    }
}

package consumentor.shopgun.aidroid.util;

import consumentor.shopgun.aidroid.model.AdviceInfo;

/**
 * Created by Simon on 2014-03-06.
 */
public class AdviceRatingComparator implements java.util.Comparator<AdviceInfo> {
    @Override
    public int compare(AdviceInfo lhs, AdviceInfo rhs) {
        return rhs.adviceScore - lhs.adviceScore;
    }
}

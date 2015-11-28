package consumentor.shopgun.aidroid.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Plankton on 2013-11-17.
 */
public class AdviceFilterList {

    private List<AdviceInfo> mAdvices;
    private List<AdviceInfo> mFilteredAdvices;

    public AdviceFilterList() {
        this.mAdvices = new ArrayList<AdviceInfo>();
        this.mFilteredAdvices = new ArrayList<AdviceInfo>();
    }

    /**
     * Adds advices to the advice filter list.
     *
     * @param advices Advices
     */
    public void add(final List<? extends AdviceInfo> advices) {
        for (AdviceInfo advice : advices) {
            mAdvices.add(advice);
        }
    }

    /**
     * Filters the advices in this list. The filtered advices is accessed through
     * getFilteredAdvices. The constraint should be a 4-char string consisting of ones and zeros.
     * A one in the first position means that ethics should be shown, a one in the second means
     * that health should be shown, third position is for environment and fourth position is for
     * 'did you know'.
     *
     * @param constraint 4-character string of ones and zeros
     */
    public void applyFilter(CharSequence constraint) {
        mFilteredAdvices.clear();
        for (AdviceInfo advice : mAdvices) {
            if (constraint.charAt(0) == '1' && advice.tag.equals(AdviceInfo.TXT_ETHICS)) {
                mFilteredAdvices.add(advice);
            } else if (constraint.charAt(1) == '1' && advice.tag.equals(AdviceInfo.TXT_HEALTH)) {
                mFilteredAdvices.add(advice);
            } else if (constraint.charAt(2) == '1' && advice.tag.equals(AdviceInfo.TXT_ENVIRONMENT)) {
                mFilteredAdvices.add(advice);
            } else if (constraint.charAt(3) == '1' && advice.tag.equals(AdviceInfo.TXT_DIDYOUKNOW)) {
                mFilteredAdvices.add(advice);
            }
        }
    }

    /**
     * @return A list of all advices in this AdviceFilterList (including advices not accepted by
     * the filter)
     */
    public List<AdviceInfo> getAllAdvices() {
        ArrayList<AdviceInfo> output = new ArrayList<AdviceInfo>();
        for (AdviceInfo a : mAdvices) {
            output.add(a);
        }
        return output;
    }

    /**
     * @return A list of all advices in this AdviceFilterList that was accepted by the filter
     */
    public List<AdviceInfo> getFilteredAdvices() {
        ArrayList<AdviceInfo> output = new ArrayList<AdviceInfo>();
        for (AdviceInfo a : mFilteredAdvices) {
            output.add(a);
        }
        return output;
    }
}

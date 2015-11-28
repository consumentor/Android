package consumentor.shopgun.aidroid.model;

import java.util.ArrayList;

/**
 * Created by Simon on 30.08.13.
 */
public interface Adviceable {
    public ItemInfo getItemInfo();
    public ArrayList<? extends AdviceInfo> getAdvicesRecursively();
}

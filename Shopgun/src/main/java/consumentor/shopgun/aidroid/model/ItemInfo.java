package consumentor.shopgun.aidroid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Simon on 30.08.13.
 */
public class ItemInfo implements Serializable {

    @SerializedName("id")
    public int id;

    // @SerializedName("name")
    public String itemName;

    public ItemType itemType;

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof ItemInfo))return false;
        ItemInfo otherItemInfo = (ItemInfo)other;
        return this.id == otherItemInfo.id && this.itemType == otherItemInfo.itemType;
    }

    @Override
    public int hashCode() {
        return (ItemInfo.class.getName() +id +itemType).hashCode();
    }

    public enum ItemType {
        PRODUCT,
        INGREDIENT,
        COMPANY,
        BRAND
    }

    public enum Semaphore {
        RED(-1), YELLOW(0), GREEN(1), TRANSPARENT(-2);

        private int value;

        private Semaphore(int numVal) {
            this.value = numVal;
        }

        public int getValue(){
            return value;
        }
    }


}

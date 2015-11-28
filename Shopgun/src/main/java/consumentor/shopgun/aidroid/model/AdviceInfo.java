package consumentor.shopgun.aidroid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Simon on 30.08.13.
 */
public class AdviceInfo implements Serializable {

    // I can't seem to find these strings declared anywhere else in the code. These should be moved
    // if appropriate, I just don't want to hardcode the strings everywhere in the code. /Björn
    public static final String TXT_ETHICS = "Etik";
    public static final String TXT_HEALTH = "Hälsa";
    public static final String TXT_ENVIRONMENT = "Miljö";
    public static final String TXT_DIDYOUKNOW = "Visste du att...";

    public static final int SEMAPHORE_GREEN = 1;
    public static final int SEMAPHORE_YELLOW = 0;
    public static final int SEMAPHORE_RED = -1;
    public static final int SEMAPHORE_TRANSPARENT = -2;

    @SerializedName("id")
    public int id;

    @SerializedName("introduction")
    public String introduction;

    @SerializedName("itemName")
    public String itemName;

    @SerializedName("label")
    public String label;

    @SerializedName("mentor")
    public MentorInfo mentorInfo;

    @SerializedName("semaphoreValue")
    public int semaphoreValue;

    @SerializedName("tag")
    public String tag;

    @SerializedName("adviceScore")
    public int adviceScore;

    @SerializedName("ProductsId")
    public int productId;

    @SerializedName("IngredientsId")
    public int ingredientId;

    @SerializedName("CompanysId")
    public int companyId;

    @SerializedName("BrandsId")
    public int brandId;

    public ItemInfo getItemInfo() {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.id = getItemId();
        itemInfo.itemName = itemName;
        itemInfo.itemType = getItemType();

        return itemInfo;
    }

    public int getItemId() {
        switch (getItemType()) {
            case BRAND:
                return brandId;
            case COMPANY:
                return companyId;
            case INGREDIENT:
                return ingredientId;
            case PRODUCT:
                return productId;
            default:
                return 0;
        }
    }

    public ItemInfo.ItemType getItemType() {

        if (productId != 0) return ItemInfo.ItemType.PRODUCT;
        if (ingredientId != 0) return ItemInfo.ItemType.INGREDIENT;
        if (companyId != 0) return ItemInfo.ItemType.COMPANY;
        if (brandId != 0) return ItemInfo.ItemType.BRAND;
        return null;
    }
}

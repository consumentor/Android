package consumentor.shopgun.aidroid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Simon on 30.08.13.
 */
public class Ingredient extends ItemInfo implements Adviceable, Serializable {

    @SerializedName("ingredientName")
    public String name;

    @SerializedName("ingredientAdvices")
    public ArrayList<AdviceInfo> advices;

    @SerializedName("alternativeIngredientNames")
    public ArrayList<String> alternativeNames;

    @Override
    public ItemInfo getItemInfo() {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.itemName = name;
        itemInfo.itemType = ItemType.INGREDIENT;
        itemInfo.id = id;

        return itemInfo;
    }

    public ArrayList<? extends AdviceInfo> getAdvicesRecursively() {
        return advices;
    }
}

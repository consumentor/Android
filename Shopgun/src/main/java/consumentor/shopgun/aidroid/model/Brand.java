package consumentor.shopgun.aidroid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Simon on 11.09.13.
 */
public class Brand extends ItemInfo implements Adviceable, Serializable {

    @SerializedName("brandName")
    public String name;

    @SerializedName("brandAdvices")
    public ArrayList<AdviceInfo> advices;

    @SerializedName("owner")
    public Company company;

    @SerializedName("logotypeUrl")
    public String logotypeUrl;

    @SerializedName("description")
    public String description;

    @SerializedName("phoneNumber")
    public String phone;

    @SerializedName("city")
    public String city;

    @SerializedName("postCode")
    public String postalCode;

    @SerializedName("address")
    public String address;

    @SerializedName("urlToHomePage")
    public String homepage;

    @SerializedName("contactEmailAddress")
    public String email;

    @SerializedName("youtubeVideoId")
    public String youtubeVideoId;

    @SerializedName("isMember")
    public boolean isMember;


    @Override
    public ItemInfo getItemInfo() {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.itemName = name;
        itemInfo.itemType = ItemType.BRAND;
        itemInfo.id = id;

        return itemInfo;
    }

    public ArrayList<? extends AdviceInfo> getAdvicesRecursively() {

        ArrayList<AdviceInfo> allAdvices = new ArrayList<AdviceInfo>();
        allAdvices.addAll(advices);
        if (company != null){
            allAdvices.addAll(company.getAdvicesRecursively());
        }
        return allAdvices;

    }
}

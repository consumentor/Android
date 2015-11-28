package consumentor.shopgun.aidroid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Simon on 11.09.13.
 */
public class Company extends ItemInfo implements Adviceable, Serializable {
    @SerializedName("companyName")
    public String name;

    @SerializedName("companyAdvices")
    public ArrayList<AdviceInfo> advices;

    @SerializedName("imageUrlLarge")
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

    @SerializedName("csrInfos")
    public ArrayList<CSRInfo> csrInfos;

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
        itemInfo.itemType = ItemType.COMPANY;
        itemInfo.id = id;

        return itemInfo;
    }

    public ArrayList<? extends AdviceInfo> getAdvicesRecursively() {
        return advices;
    }
}

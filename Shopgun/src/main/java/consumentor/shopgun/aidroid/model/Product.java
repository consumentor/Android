package consumentor.shopgun.aidroid.model;

import com.google.gson.annotations.SerializedName;

import consumentor.shopgun.aidroid.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Simon on 28.08.13.
 */
public class Product extends ItemInfo implements Adviceable, Serializable {

    @SerializedName("globalTradeItemNumber")
    public String gtin;

    @SerializedName("logotype")
    public String imageUrl;

    @SerializedName("categoryDescription")
    public String description;

    @SerializedName("website")
    public String productHomepage;

//    <allergyInformation/>
//    <brand>...</brand>
//    <categoryDescription i:nil="true"/>
//    <certificationMarks>...</certificationMarks>
//    <country i:nil="true"/>
//    <durability i:nil="true"/>

//    <gpcCode i:nil="true"/>

    @SerializedName("imageUrlLarge")
    public String imageLarge;

    @SerializedName("imageUrlMedium")
    public String imageMedium;

    @SerializedName("imageUrlSmall")
    public String imageSmall;
//    <ingredients/>

    @SerializedName("lastUpdated")
    public Date lastUpdated;

    @SerializedName("nutrition")
    public String nutrition;

    @SerializedName("originCountryName")
    public String origin;

    @SerializedName("youtubeVideoId")
    public String youtubeVideoId;

//    <productAdvices xmlns:a="http://schemas.datacontract.org/2004/07/Consumentor.ShopGun.Domain">...</productAdvices>

    @SerializedName("productName")
    public String name;


    @SerializedName("quantity")
    public String quantity;

    @SerializedName("quantityUnit")
    public String quantityUnit;

//    <supplierArticleNumber i:nil="true"/>

    @SerializedName("tableOfContents")
    public String tableOfContents;

    @SerializedName("ingredients")
    public List<Ingredient> ingredients;

    @SerializedName("productAdvices")
    public List<AdviceInfo> advices;


    @SerializedName("certificationMarks")
    public List<CertificationMark> certificationMarks;

    @SerializedName("brand")
    public Brand brand;

    @SerializedName("productCategory")
    public ProductCategory productCategory;

    @SerializedName("producerId")
    public int producerId;

    @Override
    public String toString() {
        String formattedName = name;
        if (!Util.isNullOrEmpty(quantity) && !Util.isNullOrEmpty(quantityUnit)){
            formattedName += " " +quantity +" " +quantityUnit;
        }
        return formattedName;
    }

    @Override
    public ItemInfo getItemInfo() {
        ItemInfo itemInfo = new ItemInfo();
        itemInfo.itemType = ItemType.PRODUCT;
        itemInfo.id = id;
        itemInfo.itemName = toString();

        return itemInfo;
    }

    @Override
    public ArrayList<? extends AdviceInfo> getAdvicesRecursively() {

        ArrayList<AdviceInfo> allAdvices = new ArrayList<AdviceInfo>();
        allAdvices.addAll(advices);
        allAdvices.addAll(brand.getAdvicesRecursively());
        for (Ingredient ingredient : ingredients){
            allAdvices.addAll(ingredient.advices);
        }

        return allAdvices;
    }
}

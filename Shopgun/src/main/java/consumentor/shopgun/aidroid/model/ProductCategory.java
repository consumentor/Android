package consumentor.shopgun.aidroid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Simon on 2013-09-26.
 */
public class ProductCategory implements Serializable{

    @SerializedName("id")
    public int id;

    @SerializedName("categoryName")
    public String categoryName;

}

package consumentor.shopgun.aidroid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Simon on 16.09.13.
 */
public class CSRInfo implements Serializable {

    @SerializedName("tag")
    public String tag;

    @SerializedName("description")
    public String description;
}

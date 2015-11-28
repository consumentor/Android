package consumentor.shopgun.aidroid.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Simon on 27.08.13.
 */
public class Advisor {

    @SerializedName("id")
    public int id;

    @SerializedName("mentorName")
    public String name;

    @SerializedName("isActive")
    public boolean isActive ;

    @SerializedName("logotypeUrl")
    public String logotypeURL;

    @SerializedName("Url")
    public String homepage;

    @SerializedName("Description")
    public String description;

    @Override
    public String toString() {
        return name;
    }
}

package consumentor.shopgun.aidroid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Simon on 30.08.13.
 */
public class MentorInfo implements Serializable{

    @SerializedName("id")
    public int id;

    @SerializedName("mentorName")
    public String name;

    @SerializedName("logotypeUrl")
    public String logotypeUrl;
}

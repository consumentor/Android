package consumentor.shopgun.aidroid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Simon on 2013-11-29.
 */
public class UserAdviceRating implements Serializable{

    @SerializedName("id")
    public int id;

    @SerializedName("adviceId")
    public int adviceId;

    @SerializedName("userId")
    public int userId;

    @SerializedName("rating")
    public int rating;
}

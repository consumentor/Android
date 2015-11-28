package consumentor.shopgun.aidroid.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Simon on 2013-10-31.
 */
public class LoginResult {

    @SerializedName("message")
    public String message;

    @SerializedName("token")
    public String token;

    @SerializedName("value")
    public boolean success;
}

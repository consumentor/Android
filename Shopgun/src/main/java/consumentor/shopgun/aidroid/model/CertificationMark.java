package consumentor.shopgun.aidroid.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Simon on 2013-09-23.
 */
public class CertificationMark implements Serializable {

    @SerializedName("id")
    public int id;

    @SerializedName("certificationName")
    public String certificationName;


    @SerializedName("description")
    public String description;

    @SerializedName("certificationMarkImageUrlMedium")
    public String logotypeUrl;

    @SerializedName("url")
    public String homepage;
}

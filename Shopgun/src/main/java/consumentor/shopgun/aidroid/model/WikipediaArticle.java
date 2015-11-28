package consumentor.shopgun.aidroid.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Simon on 10.09.13.
 */
public class WikipediaArticle {

    @SerializedName("mobileview")
    public WikipediaSections sections;

    public class WikipediaSections{
        public ArrayList<WikipediaSection> sections;
    }

    public class WikipediaSection{
        @SerializedName("id")
        public int sectionId;

        @SerializedName("text")
        public String text;
    }

}

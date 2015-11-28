package consumentor.shopgun.aidroid.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by Simon on 28.08.13.
 */
public class JsonDateDeserializer implements JsonDeserializer<Date> {
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String s = json.getAsJsonPrimitive().getAsString();
        int start = s.indexOf("(") + 1;
        int end = s.indexOf("-") != -1 ? s.indexOf("-") : s.indexOf("+");
        String stringToParse = s.substring(start, end);
        long l = Long.parseLong(stringToParse);
        Date d = new Date(l);
        return d;
    }
}
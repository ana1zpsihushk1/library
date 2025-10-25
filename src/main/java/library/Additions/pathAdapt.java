package library.Additions;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;

public class pathAdapt implements JsonSerializer<Path>, JsonDeserializer<Path> {
    @Override
    public JsonElement serialize(Path src, Type typeOfSrc, JsonSerializationContext context) {
        return (src == null) ? JsonNull.INSTANCE : new JsonPrimitive(src.toString());
    }

    @Override
    public Path deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) return null;
        String s = json.getAsString();
        return (s == null || s.isBlank()) ? null : Paths.get(s);
    }
}
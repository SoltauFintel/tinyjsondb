package tinyjsondb;

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class JsonFileService extends AbstractFileService {

    @Override
    public String getSuffix() {
        return ".json";
    }
    
    @Override
    public <T> T loadFile(File file, Class<T> type) {
        String json = loadPlainTextFile(file);
        return json == null ? null : new Gson().fromJson(json, type);
    }

    @Override
    public <T> void saveFile(File file, T data) {
        savePlainTextFile(file, data == null ? null : prettyJSON(data));
    }

    public String prettyJSON(String json) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonElement je = JsonParser.parseString(json);
            return gson.toJson(je);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> String prettyJSON(T data) {
        return prettyJSON(new Gson().toJson(data));
    }
}

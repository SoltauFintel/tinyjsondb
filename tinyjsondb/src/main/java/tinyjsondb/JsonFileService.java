package tinyjsondb;

import java.io.File;

import com.google.gson.Gson;

import tinyjsondb.base.StringService;

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
        savePlainTextFile(file, data == null ? null : StringService.prettyJSON(data));
    }
}

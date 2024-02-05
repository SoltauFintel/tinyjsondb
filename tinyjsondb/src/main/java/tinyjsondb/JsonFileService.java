package tinyjsondb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class JsonFileService {

    public boolean isFolderEmpty(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            return files != null && files.length == 0;
        }
        return false;
    }

    public void deleteFolder(File folder) {
        try {
            if (folder.isDirectory()) {
                Files.walk(folder.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting folder " + folder.getAbsolutePath() + ": " + e.getMessage(), e);
        }
    }
    
    public <T> T loadJsonFile(File file, Class<T> type) {
        String json = loadPlainTextFile(file);
        return json == null ? null : new Gson().fromJson(json, type);
    }

    public <T> void saveJsonFile(File file, T data) {
        savePlainTextFile(file, data == null ? null : prettyJSON(data));
    }

    public String loadPlainTextFile(File file) {
        if (file.isFile()) {
            try {
                return new String(Files.readAllBytes(file.toPath()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }                
        return null;
    }

    public void savePlainTextFile(File file, String content) {
        if (content == null) {
            file.delete();
        } else {
            file.getParentFile().mkdirs();
            try (FileWriter w = new FileWriter(file)) {
                w.write(content);
            } catch (IOException e) {
                throw new RuntimeException("Error saving file", e);
            }
        }
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

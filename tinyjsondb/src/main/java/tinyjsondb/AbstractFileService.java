package tinyjsondb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public abstract class AbstractFileService implements FileService {

    @Override
    public boolean isFolderEmpty(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            return files != null && files.length == 0;
        }
        return false;
    }

    @Override
    public void deleteFolder(File folder) {
        try {
            if (folder.isDirectory()) {
                Files.walk(folder.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error deleting folder " + folder.getAbsolutePath() + ": " + e.getMessage(), e);
        }
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

    @Override
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
}

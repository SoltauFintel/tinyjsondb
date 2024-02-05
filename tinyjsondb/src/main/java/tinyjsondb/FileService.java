package tinyjsondb;

import java.io.File;

public interface FileService {

    String getSuffix();
    
    boolean isFolderEmpty(File folder);

    void deleteFolder(File folder);

    <T> T loadFile(File file, Class<T> type);

    <T> void saveFile(File file, T data);
    
    void savePlainTextFile(File file, String content);
}
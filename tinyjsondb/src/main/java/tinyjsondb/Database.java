package tinyjsondb;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import tinyjsondb.IdAccess.IdValue;

public class Database {
    private final File folder;
    private final IdAccess idAccess;
    
    public Database(String folder, Class<?>... classes) {
        this.folder = new File(folder);
        idAccess = new IdAccess(classes);
    }

    public void save(String extra, Object entity) {
        IdValue j = idAccess.getId(entity, true);
        if (j.getId() == null) {
            throw new RuntimeException("Can not retrieve id from entity! Does a @Id annotated field exist?");
        }
        File file = getFile(extra, entity.getClass().getSimpleName(), j.getId());
        FileService.saveJsonFile(file, entity);
    }

    public void insert(String extra, Object entity) {
        IdValue j = idAccess.getId(entity, true);
        if (j.getId() == null) {
            throw new RuntimeException("Can not retrieve id from entity! Does a @Id annotated field exist?");
        }
        File file = getFile(extra, entity.getClass().getSimpleName(), j.getId());
        if (file.exists()) {
            throw new RuntimeException("Object already exists!");
        }
        FileService.saveJsonFile(file, entity);
    }

    public void update(String extra, Object entity) {
        File file = getFile4Entity(extra, entity);
        if (file.isFile()) {
            FileService.saveJsonFile(file, entity);
        } else {
            throw new RuntimeException("Entity does not exist!");
        }
    }

    public Object get(String extra, Class<?> cls, String id) {
        File file = getFile(extra, cls.getSimpleName(), id);
        return FileService.loadJsonFile(file, cls);
    }

    public boolean delete(String extra, Class<?> cls, String id) {
        File file = getFile(extra, cls.getSimpleName(), id);
        return _delete(extra, cls, file);
    }

    public boolean delete(String extra, Object entity) {
        File file = getFile4Entity(extra, entity);
        return _delete(extra, entity.getClass(), file);
    }
    
    private boolean _delete(String extra, Class<?> cls, File file) {
        if (file.isFile() && file.delete()) {
            removeFolderIfEmpty(extra, cls);
            return true;
        }
        return false;
    }

    public int deleteAll(String extra, Class<?> cls) {
        int ra = 0;
        for (File file : listFiles(extra, cls)) {
            if (file.delete()) {
                ra++;
            }
        }
        removeFolderIfEmpty(extra, cls);
        return ra;
    }
    
    /**
     * Deletes complete collection, also with sub collections.
     * @param cls -
     */
    public void dropCollection(String extra, Class<?> cls) {
        FileService.deleteFolder(new File(folder, extra + cls.getSimpleName()));
        removeExtraFolderIfEmpty(extra);
    }

    public List<Object> list(String extra, Class<?> cls) {
        return listFiles(extra, cls).stream().map(file -> FileService.loadJsonFile(file, cls)).collect(Collectors.toList());
    }

    public Iterator<?> iterator(String extra, Class<?> cls) {
        List<File> files = listFiles(extra, cls);
        return new Iterator<>() {
            private int i = -1;

            @Override
            public boolean hasNext() {
                return i + 1 < files.size();
            }

            @Override
            public Object next() {
                return FileService.loadJsonFile(files.get(++i), cls);
            }
        };
    }

    public int size(String extra, Class<?> cls) {
        return listFiles(extra, cls).size();
    }

    private List<File> listFiles(String extra, Class<?> cls) {
        List<File> ret = new ArrayList<>();
        File dir = new File(folder, extra + cls.getSimpleName());
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                        ret.add(file);
                    }
                }
            }
        }
        return ret;
    }

    public boolean exists(String extra, Object entity) {
        return getFile4Entity(extra, entity).isFile();
    }
    
    public void saveFile(String extra, Object entity, String filename, String content) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("filename must not be empty");
        }
        String id = id(entity);
        String className = entity.getClass().getSimpleName();
        File file = new File(folder, extra + className + "/" + filename.replace("{id}", id));
        FileService.savePlainTextFile(file, content);
    }
    
    private File getFile4Entity(String extra, Object entity) {
        String id = id(entity);
        return getFile(extra, entity.getClass().getSimpleName(), id);
    }

    private File getFile(String extra, String className, String id) {
        return new File(folder, extra + className + "/" + id + ".json");
        
        //   <base folder>  /   Person  /  <ID>.json
        //   <base folder>  /   Person  /  <ID>       / House / <House_ID>.json
        //                      ^^^^^^^^^^^^^^^^^^^^^^^
        //                      = extra
    }

    private void removeFolderIfEmpty(String extra, Class<?> cls) {
        File dir = new File(folder, extra + cls.getSimpleName());
        if (FileService.isFolderEmpty(dir)) {
            FileService.deleteFolder(dir);
            
            removeExtraFolderIfEmpty(extra);
        }
    }

    private void removeExtraFolderIfEmpty(String extra) {
        if (!extra.isEmpty()) {
            File dir = new File(folder, extra);
            if (FileService.isFolderEmpty(dir)) {
                FileService.deleteFolder(dir);
            }
        }
    }

    private String id(Object entity) {
        String id = idAccess.getId(entity, false).getId();
        if (id == null) {
            throw new RuntimeException("Can not retrieve id from entity! Does a @Id annotated field exist?");
        }
        return id;
    }
}

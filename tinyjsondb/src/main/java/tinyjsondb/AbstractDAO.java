package tinyjsondb;

import java.util.Iterator;
import java.util.List;

public abstract class AbstractDAO<E> {
    public static Database database;
    protected final String extra;
    
    public AbstractDAO() {
        this("");
    }
    
    public AbstractDAO(String extra) {
        if (extra == null) {
            this.extra = "";
        } else if (!extra.isEmpty() && !extra.endsWith("/")) {
            this.extra = extra + "/";
        } else {
            this.extra = extra;
        }
    }

    public AbstractDAO(String parentClassName, String parentId) {
        this(parentClassName + "/" + parentId + "/");
    }

    protected abstract Class<E> getEntityClass();

    public void save(E entity) {
        check(entity);
        ds().save(extra, entity);
    }
    
    public void insert(E entity) {
        check(entity);
        ds().insert(extra, entity);
    }

    public void update(E entity) {
        check(entity);
        ds().update(extra, entity);
    }

    public E get(String id) {
        return (E) ds().get(extra, getEntityClass(), id);
    }

    public boolean deleteById(String id) {
        return ds().delete(extra, getEntityClass(), id);
    }
    
    public boolean delete(E entity) {
        check(entity);
        return ds().delete(extra, entity);
    }

    /**
     * @return number of deleted entities
     */
    public int deleteAll() {
        return ds().deleteAll(extra, getEntityClass());
    }
    
    public void dropCollection() {
        ds().dropCollection(extra, getEntityClass());
    }

    @SuppressWarnings("rawtypes")
    public List<E> list() {
        return (List) ds().list(extra, getEntityClass());
    }
    
    @SuppressWarnings("rawtypes")
    public Iterator<E> iterator() {
        return (Iterator) ds().iterator(extra, getEntityClass());
    }

    public int size() {
        return ds().size(extra, getEntityClass());
    }
    
    public void saveFile(E entity, String filename, String content) {
        ds().saveFile(extra, entity, filename, content);
    }

    protected Database ds() {
        return database;
    }

    private void check(E entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity must not be null");
        } else if (!entity.getClass().equals(getEntityClass())) {
            throw new RuntimeException("entity must be of type " + getEntityClass().getName());
        }
    }
}

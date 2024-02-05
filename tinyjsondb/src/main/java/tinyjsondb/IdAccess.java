package tinyjsondb;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import tinyjsondb.base.IdGenerator;

public class IdAccess {
    /** class name, Id field */
    private final Map<String, Field> fieldMap = new HashMap<>();
    
    public IdAccess(Class<?>... classes) {
        String msg = "";
        for (Class<?> cls : classes) {
            boolean found = false;
            for (Field field : cls.getDeclaredFields()) {
                if (field.getAnnotation(Id.class) != null && String.class.equals(field.getType())) {
                    fieldMap.put(cls.getName(), field);
                    found = true;
                    break;
                }
            }
            if (!found) {
                if (!msg.isEmpty()) {
                    msg += "\n";
                }
                msg += "There is no @Id annotated attribute in class " + cls.getName();
            }
        }
        if (!msg.isEmpty()) {
            throw new RuntimeException(msg);
        }
    }
    
    public IdValue getId(Object entity, boolean idGenerationAllowed) {
        IdValue ret = new IdValue();
        try {
            Field field = fieldMap.get(entity.getClass().getName());
            field.setAccessible(true);
            try {
                if (field.get(entity) == null) {
                    if (idGenerationAllowed) {
                        ret.generate();
                        field.set(entity, ret.getId());
                    }
                } else {
                    String id = (String) field.get(entity);
                    if (id.isBlank()) {
                        if (idGenerationAllowed) {
                            ret.generate();
                            field.set(entity, ret.getId());
                        }
                    } else {
                        ret.id = id;
                    }
                }
            } finally {
                field.setAccessible(false);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error accessing ID from entity!", e);
        }
        return ret;
    }
    
    static class IdValue {
        String id = null;
        private boolean generated = false;
        
        void generate() {
            id = IdGenerator.createId6();
            generated = true;
        }

        String getId() {
            return id;
        }

        boolean isGenerated() {
            return generated;
        }
    }
}

package tinyjsondb;

import java.io.File;
import java.lang.reflect.Field;

import de.mwvb.base.xml.XMLDocument;
import de.mwvb.base.xml.XMLElement;

public class XmlFileService extends AbstractFileService {

    @Override
    public String getSuffix() {
        return ".xml";
    }

    @Override
    public <T> T loadFile(File file, Class<T> type) {
        if (!file.isFile()) {
            return null;
        }
        try (XMLDocument doc = new XMLDocument(file)) {
            T data = create(type);
            deserialize(doc.getElement(), data);
            return data;
        }
    }

    private <T> T create(Class<T> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> void deserialize(XMLElement e, T data) {
        for (int i = 0; i < e.getAttributeCount(); i++) {
            String name = e.getAttributeName(i);
            set(data, name, e.getValue(name));
        }

    }

    private <T> void set(T data, String field, String value) {
        for (Field f : data.getClass().getDeclaredFields()) {
            if (f.getName().equals(field)) {
                f.setAccessible(true);
                try {
                    f.set(data, value);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    f.setAccessible(false);
                }
            }
        }

    }

    @Override
    public <T> void saveFile(File file, T data) {
        try (XMLDocument doc = new XMLDocument("<" + data.getClass().getSimpleName() + "/>")) {
            serialize(data, doc.getElement());
            file.getParentFile().mkdirs();
            doc.saveFile(file.getAbsolutePath());
        }
    }

    private <T> void serialize(T data, XMLElement e) {
        for (Field f : data.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            try {
                // TODO can't handle sub objects (same in deserialize())
                e.setValue(f.getName(), (String) f.get(data));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            } finally {
                f.setAccessible(false);
            }
        }
    }
}

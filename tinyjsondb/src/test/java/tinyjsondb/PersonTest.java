package tinyjsondb;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tinyjsondb.base.IdGenerator;

public class PersonTest {
    private PersonDAO dao;
    private Person john;
    
    @Before
    public void init() {
        FileService fs = new JsonFileService();
        fs.deleteFolder(new File("testdata"));
        AbstractDAO.database = new Database(fs, "testdata", Person.class);
        dao = new PersonDAO();
    }
    
    @Test
    public void insert() {
        john = new Person();
        john.setId(IdGenerator.createId6());
        john.setForename("John");
        john.setSurname("Doe");
        
        dao.insert(john);
        
        Assert.assertTrue("File hasn't been saved!", new File("testdata/Person/" + john.getId() + ".json").isFile());
    }

    @Test
    public void duplicateKey() {
        john = new Person();
        john.setId(IdGenerator.createId6());
        john.setForename("John");
        john.setSurname("Doe");
        dao.insert(john);
        
        try {
            dao.insert(john);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            Assert.assertEquals("Object already exists!", e.getMessage());
        }
    }

    @Test
    public void insert_withNoId() {
        john = new Person();
        john.setForename("John");
        john.setSurname("Doe");
        
        dao.insert(john);
        
        Assert.assertTrue("ID wasn't set!", john.getId() != null && !john.getId().isBlank());
        Assert.assertTrue("File hasn't been saved!", new File("testdata/Person/" + john.getId() + ".json").isFile());
    }

    @Test
    public void insert_withEmptyId() {
        john = new Person();
        john.setId(" ");
        john.setForename("John");
        john.setSurname("Doe");
        
        dao.insert(john);
        
        Assert.assertTrue("ID wasn't set!", john.getId() != null && !john.getId().isBlank());
        Assert.assertTrue("File hasn't been saved!", new File("testdata/Person/" + john.getId() + ".json").isFile());
    }

    @Test
    public void save() {
        john = new Person();
        john.setId(IdGenerator.createId6());
        john.setForename("John");
        john.setSurname("Doe");
        
        // Test 1
        dao.save(john);
        
        // Verify 1
        Assert.assertTrue("File hasn't been saved!", new File("testdata/Person/" + john.getId() + ".json").isFile());
        
        // Test 2
        john.setSurname("McKenzie");
        dao.save(john);
        
        // Verify 2
        Person x = dao.get(john.getId());
        Assert.assertEquals("McKenzie", x.getSurname());
    }

    @Test
    public void save_withNoId() {
        john = new Person();
        john.setForename("John");
        john.setSurname("Doe");
        
        dao.save(john);
        
        Assert.assertTrue("ID wasn't set!", john.getId() != null && !john.getId().isBlank());
    }

    @Test
    public void save_withEmptyId() {
        john = new Person();
        john.setId("");
        john.setForename("John");
        john.setSurname("Doe");
        
        dao.save(john);
        
        Assert.assertTrue("ID wasn't set!", john.getId() != null && !john.getId().isBlank());
    }

    @Test
    public void get() {
        insert();
        
        Person p = dao.get(john.getId());
        
        Assert.assertNotNull(p);
        Assert.assertEquals("John", p.getForename());
        Assert.assertEquals("Doe", p.getSurname());
    }

    @Test
    public void get_notExist() {
        Assert.assertNull(dao.get("non-sense"));
    }

    @Test
    public void deleteById() {
        insert();
        
        Assert.assertTrue(dao.deleteById(john.getId()));

        Assert.assertFalse(new File("testdata/Person/" + john.getId() + ".json").isFile());
    }

    @Test
    public void deleteById_notExist() {
        Assert.assertFalse(dao.deleteById("non-sense"));
    }

    @Test
    public void deleteAll() {
        insert();
        
        int ra = dao.deleteAll();

        Assert.assertTrue(dao.list().isEmpty());
        Assert.assertEquals(1, ra);
    }

    @Test
    public void dropCollection() {
        insert();
        
        dao.dropCollection();

        Assert.assertFalse(new File("testdata/Person").isDirectory());
    }

    @Test
    public void emptyList() {
        Assert.assertTrue(dao.list().isEmpty());
    }

    @Test
    public void list() {
        insert();
        Person jane = new Person();
        jane.setId(IdGenerator.createId6());
        jane.setForename("Jane");
        jane.setSurname("Doe");
        dao.insert(jane);
        
        List<Person> list = dao.list();
        
        Assert.assertEquals(2, list.size());
        Assert.assertTrue(list.stream().anyMatch(p -> "John".equals(p.getForename())));
        Assert.assertTrue(list.stream().anyMatch(p -> "Jane".equals(p.getForename())));
    }

    @Test
    public void size0() {
        Assert.assertEquals(0, dao.size());
    }
    
    @Test
    public void size() {
        list();
        
        int r = dao.size();
        
        Assert.assertEquals(2, r);
    }

    @Test
    public void update() {
        insert();
        
        Person p = dao.get(john.getId());
        p.setSurname("Duff");
        dao.update(p);

        Person p2 = dao.get(p.getId());
        Assert.assertEquals("Duff", p2.getSurname());
    }

    @Test
    public void update_notExist() {
        Person p = new Person();
        p.setId(IdGenerator.createId6());
        
        try {
            dao.update(p);
            Assert.fail("Exception expected");
        } catch (Exception e) {
            Assert.assertEquals("Entity does not exist!", e.getMessage());
        }
    }
    
    @Test
    public void saveFile() {
        insert();
        Person jane = new Person();
        jane.setForename("Jane");
        dao.insert(jane);
        String dn = "en/{id}.html";
        String content = "english html file";

        dao.saveFile(john, dn, content);
        dao.saveFile(jane, dn, "content for Jane");
        dao.saveFile(jane, "de/{id}.html", "Inhalt für Jane");
        
        Assert.assertTrue(new File("testdata/Person/en/" + john.getId() + ".html").isFile());
    }
}

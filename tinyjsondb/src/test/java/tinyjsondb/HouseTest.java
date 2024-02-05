package tinyjsondb;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import tinyjsondb.base.IdGenerator;

public class HouseTest {
    private PersonDAO pdao;
    private Person john;
    private Person jane;
    private House house;
    
    @Before
    public void init() {
        FileService fs = new JsonFileService();
        fs.deleteFolder(new File("testdata"));
        AbstractDAO.database = new Database(fs, "testdata", Person.class, House.class);
        pdao = new PersonDAO();

        john = new Person();
        john.setId(IdGenerator.createId6());
        john.setForename("John");
        john.setSurname("Dafoe");
        pdao.insert(john);

        jane = new Person();
        jane.setId(IdGenerator.createId6());
        jane.setForename("Jane");
        jane.setSurname("Miller");
        pdao.insert(jane);
    }
    
    @Test
    public void insert() {
        HouseDAO dao = new HouseDAO(john);
        house = new House();
        house.setAddress("New York");
        dao.insert(house);
        
        Assert.assertNotNull("ID wasn't set!", house.getId());
        File x = new File("testdata/Person/" + john.getId() + "/House/" + house.getId() + ".json");
        Assert.assertTrue("House not saved!\n" + x.toString(), x.isFile());
    }
    
    @Test
    public void list() {
        insert();
        
        HouseDAO dao = new HouseDAO(john);
        List<House> houses = dao.list();
        
        Assert.assertEquals(1, houses.size());
    }
    
    @Test
    public void delete() {
        insert();
        
        HouseDAO dao = new HouseDAO(john);
        dao.deleteById(house.getId());
        
        Assert.assertNotNull(pdao.get(john.getId()));
        Assert.assertEquals(0, dao.list().size());
    }
    
    @Test
    public void update() {
        insert();
        
        HouseDAO dao = new HouseDAO(john);
        house.setAddress("Rio");
        dao.update(house);
        
        Assert.assertEquals("Rio", dao.list().get(0).getAddress());
    }

    @Test
    public void removeFolderAfterDelete() {
        File dir = new File("testdata/Person/" + john.getId());
        File dir2 = new File(dir, "House");
        Assert.assertFalse(dir.exists());

        HouseDAO dao = new HouseDAO(john);
        house = new House();
        house.setAddress("New York");
        dao.insert(house);
        Assert.assertTrue(dir.exists());
        dao.delete(house);
        
        Assert.assertFalse("House dir should be deleted: " + dir2.toString(), dir2.exists());
        Assert.assertFalse("Person-Id dir should be deleted: " + dir.toString(), dir.exists());
    }
}

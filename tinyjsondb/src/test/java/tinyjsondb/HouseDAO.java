package tinyjsondb;

public class HouseDAO extends AbstractDAO<House> {
    
    public HouseDAO(Person person) {
        super("Person", person.getId());
    }

    @Override
    protected Class<House> getEntityClass() {
        return House.class;
    }
}

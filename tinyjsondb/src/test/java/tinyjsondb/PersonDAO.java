package tinyjsondb;

public class PersonDAO extends AbstractDAO<Person> {

    @Override
    protected Class<Person> getEntityClass() {
        return Person.class;
    }
}

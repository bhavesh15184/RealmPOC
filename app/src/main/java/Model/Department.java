package Model;

import io.realm.RealmList;
import io.realm.RealmObject;


public class Department extends RealmObject {
    private long id;
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    private String name;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private RealmList<Employee> employees;
    public RealmList<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(RealmList<Employee> employees) {
        this.employees = employees;
    }
}

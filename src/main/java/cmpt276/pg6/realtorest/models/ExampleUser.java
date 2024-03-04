package cmpt276.pg6.realtorest.models;

import jakarta.persistence.*;

// This is a user object.
// The @Entity annotation tells Spring that this is a table in the database.
@Entity
@Table(name = "example_users")
public class ExampleUser {
    // The @Id annotation tells Spring that this is the primary key.
    @Id
    // The @GeneratedValue annotation tells Spring that this is an auto-incremented field.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // The above two annotations are applied to the uid field.
    private int uid;
    private String name;
    private String password;
    private int size;

    public ExampleUser() {}

    public ExampleUser(String name, String password, int size) {
        this.name = name;
        this.password = password;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}

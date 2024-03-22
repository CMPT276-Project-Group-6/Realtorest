package cmpt276.pg6.realtorest.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// This is an admin object.
// The @Entity annotation tells Spring that this is a table in the database.
@Entity
@Table(name = "admins")
public class Admin {
    @Id
    // The @GeneratedValue annotation tells Spring that this is an auto-incremented field.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // The above two annotations are applied to the uid field.
    private int aid;
    private String adminName;
    private String email;
    private String password;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Admin() {}

    public Admin(String adminName, String email, String password) {
        this.adminName = adminName;
        this.email = email;
        this.password = password;
    }
}

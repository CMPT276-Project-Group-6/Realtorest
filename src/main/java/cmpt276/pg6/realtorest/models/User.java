package cmpt276.pg6.realtorest.models;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

// This is a user object.
// The @Entity annotation tells Spring that this is a table in the database.
@Entity
@Table(name = "users")
public class User {
    // The @Id annotation tells Spring that this is the primary key.
    @Id
    // The @GeneratedValue annotation tells Spring that this is an auto-incremented field.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // The above two annotations are applied to the uid field.
    private int uid;
    private String username;
    private String email;
    private String password;

    @ManyToMany
    @JoinTable(
        name = "user_favourite_properties",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "property_id"))

    private Set<Property> favouriteProperties = new HashSet<>();

    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public Set<Property> getFavouriteProperties() {
        return favouriteProperties;
    }

    public void setFavouriteProperties(Set<Property> favouriteProperties) {
        this.favouriteProperties = favouriteProperties;
    }
}

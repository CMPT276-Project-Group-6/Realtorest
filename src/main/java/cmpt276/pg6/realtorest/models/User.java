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
    private String email;
    private String password;
    private String username;
    private String firstName;
    private String lastName;
    private boolean isOnMailingList;
    private String resetToken;

    @ManyToMany
    @JoinTable(
        name = "user_favorite_properties",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "property_id"))

    private Set<Property> favoriteProperties = new HashSet<>();

    public User() {}

    // Constructor with no isOnMailingList
    // Defaults to false
    public User(String email, String password, String username, String firstName, String lastName) {
        this(email, password, username, firstName, lastName, false);
    }

    public User(String email, String password, String username, String firstName, String lastName, boolean isOnMailingList) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isOnMailingList = isOnMailingList;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isOnMailingList() {
        return isOnMailingList;
    }

    public void setOnMailingList(boolean isOnMailingList) {
        this.isOnMailingList = isOnMailingList;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public Set<Property> getFavoriteProperties() {
        return favoriteProperties;
    }

    public void setFavoriteProperties(Set<Property> favoriteProperties) {
        this.favoriteProperties = favoriteProperties;
    }
}

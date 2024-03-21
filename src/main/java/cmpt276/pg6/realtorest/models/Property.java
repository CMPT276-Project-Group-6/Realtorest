package cmpt276.pg6.realtorest.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pid;
    private String name; // name of the property listing
    private String location; // just the city name
    private int price; // in CAD
    private int brCount; // number of bedrooms
    private int baCount; // number of bathrooms

    public Property() {}

    public Property(String name, String location, int price, int brCount, int baCount) {
        this.name = name;
        this.location = location;
        this.price = price;
        this.brCount = brCount;
        this.baCount = baCount;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getBrCount() {
        return brCount;
    }

    public void setBrCount(int brCount) {
        this.brCount = brCount;
    }

    public int getBaCount() {
        return baCount;
    }

    public void setBaCount(int baCount) {
        this.baCount = baCount;
    }
}

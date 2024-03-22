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
    private String name; // name of the owner listing the property
    private String street; // street address
    private String city; // city name
    private String province; // province name
    private String zipCode; // zip code
    private String description; // description of the property
    private int price; // in CAD
    private int brCount; // number of bedrooms
    private int baCount; // number of bathrooms

    public Property() {}

    public Property(String name, String street, String city, String province, String zipCode, String description, int price, int brCount, int baCount) {
        this.name = name;
        this.street = street;
        this.city = city;
        this.province = province;
        this.zipCode = zipCode;
        this.description = description;
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

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

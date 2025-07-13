package br.edu.ifrs.tads.ppa.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile {

    public enum AccountType {
        FREE,
        PROFESSIONAL,
        ENTERPRISE
    }
    
    @Id
    private Integer id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    @JsonBackReference
    private User user;

    private String name;
    private String company;

    @Enumerated(EnumType.STRING)
    private AccountType type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

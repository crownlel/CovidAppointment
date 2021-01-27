package com.example.covidvac.models;

import java.io.Serializable;
import java.util.Date;

public class Citizen implements Serializable {
    private final int id;
    private final String name;
    private final Date birthday;
    private final String id_number;
    private final String tax_id;
    private final String telephone;

    public Citizen(int id, String name, Date birthday, String id_number, String tax_id, String telephone) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.id_number = id_number;
        this.tax_id = tax_id;
        this.telephone = telephone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getId_number() {
        return id_number;
    }

    public String getTax_id() {
        return tax_id;
    }

    public String getTelephone() {
        return telephone;
    }

}

package com.example.covidvac.models;

import java.io.Serializable;
import java.util.Date;

public class Citizen implements Serializable {
    private  int id;
    private String name;
    private Date birthday;
    private String id_number;
    private String tax_id;
    private String telephone;




    public Citizen(int id, String name, Date birthday, String id_number, String tax_id, String telephone) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.id_number = id_number;
        this.tax_id = tax_id;
        this.telephone = telephone;
    }

    public Citizen(){

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

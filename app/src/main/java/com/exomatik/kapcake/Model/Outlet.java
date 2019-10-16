
package com.exomatik.kapcake.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Outlet {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nama")
    @Expose
    private String nama;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Outlet() {
    }

    /**
     * 
     * @param id
     * @param nama
     */
    public Outlet(Integer id, String nama) {
        super();
        this.id = id;
        this.nama = nama;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

}

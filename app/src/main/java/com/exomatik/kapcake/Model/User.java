
package com.exomatik.kapcake.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("bisnis_id")
    @Expose
    private Integer bisnisId;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("telpon")
    @Expose
    private String telpon;
    @SerializedName("alamat")
    @Expose
    private String alamat;
    @SerializedName("is_super_admin")
    @Expose
    private Integer isSuperAdmin;
    @SerializedName("pin")
    @Expose
    private Integer pin;
    @SerializedName("outlet")
    @Expose
    private List<Outlet> outlet = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public User() {
    }

    /**
     * 
     * @param id
     * @param isSuperAdmin
     * @param outlet
     * @param bisnisId
     * @param pin
     * @param email
     * @param telpon
     * @param nama
     * @param alamat
     */
    public User(Integer id, Integer bisnisId, String nama, String email, String telpon, String alamat, Integer isSuperAdmin, Integer pin, List<Outlet> outlet) {
        super();
        this.id = id;
        this.bisnisId = bisnisId;
        this.nama = nama;
        this.email = email;
        this.telpon = telpon;
        this.alamat = alamat;
        this.isSuperAdmin = isSuperAdmin;
        this.pin = pin;
        this.outlet = outlet;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBisnisId() {
        return bisnisId;
    }

    public void setBisnisId(Integer bisnisId) {
        this.bisnisId = bisnisId;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelpon() {
        return telpon;
    }

    public void setTelpon(String telpon) {
        this.telpon = telpon;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public Integer getIsSuperAdmin() {
        return isSuperAdmin;
    }

    public void setIsSuperAdmin(Integer isSuperAdmin) {
        this.isSuperAdmin = isSuperAdmin;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public List<Outlet> getOutlet() {
        return outlet;
    }

    public void setOutlet(List<Outlet> outlet) {
        this.outlet = outlet;
    }

}

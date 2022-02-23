package com.pillarglobal.photoUpload.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class PhotoInfo {

    @Id
    @GeneratedValue
    @Column(name ="sno")
    private Integer id;
    private String photoName;
    private String photoGuid;

    public PhotoInfo(){
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getPhotoGuid() {
        return photoGuid;
    }

    public void setPhotoGuid(String photoGuid) {
        this.photoGuid = photoGuid;
    }
}

package com.globalm.platform.models.assingments;

public class Organization {
    private Integer id;
    private String name;
    private String code;
    private String thumb;

    public Organization(Integer id, String name, String code, String thumb) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.thumb = thumb;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getThumb() {
        return thumb;
    }
}

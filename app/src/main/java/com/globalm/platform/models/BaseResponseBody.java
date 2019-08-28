package com.globalm.platform.models;

//TODO USE THIS AS BASE RESPONSE CLASS SINCE THEY ALL ARE THE SAME
public class BaseResponseBody<VALIDATION, DATA> {
    private Boolean success;
    private Boolean error;
    private String type;
    private String message;
    protected VALIDATION validation;
    private Integer code;
    private DATA data;

    public BaseResponseBody(
            Boolean success,
            Boolean error,
            String type,
            String message,
            VALIDATION validation,
            Integer code,
            DATA data) {
        this.success = success;
        this.error = error;
        this.type = type;
        this.message = message;
        this.validation = validation;
        this.code = code;
        this.data = data;
    }


    public DATA getData() {
        return data;
    }

    public Integer getCode() {
        return code;
    }

    public VALIDATION getValidation() {
        return validation;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public Boolean getError() {
        return error;
    }

    public Boolean getSuccess() {
        return success;
    }
}

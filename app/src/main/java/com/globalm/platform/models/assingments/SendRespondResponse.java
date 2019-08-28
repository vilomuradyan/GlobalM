package com.globalm.platform.models.assingments;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class SendRespondResponse {
    private Boolean success;
    private Boolean error;
    private String type;
    private String message;
    @Nullable
    private transient ResponseValidationError validation;
    private Integer code;
    private ContentData data;

    public SendRespondResponse(Boolean success, Boolean error, String type, String message, Integer code, ContentData data) {
        this.success = success;
        this.error = error;
        this.type = type;
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public void setValidation(ResponseValidationError validation) {
        this.validation = validation;
    }

    public Boolean getSuccess() {
        return success;
    }

    public Boolean getError() {
        return error;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    @Nullable
    public ResponseValidationError getValidation() {
        return validation;
    }

    public Integer getCode() {
        return code;
    }

    public ContentData getData() {
        return data;
    }

    public static class ContentData {
        Response item;

        public ContentData(Response item) {
            this.item = item;
        }

        public Response getItem() {
            return item;
        }
    }

    public static class RespondDeserializer implements JsonDeserializer<SendRespondResponse> {

        private SendRespondResponse getResponse(JsonObject jsonObject, ContentData data) {
            return new SendRespondResponse(
                    jsonObject.get("success").getAsBoolean(),
                    jsonObject.get("error").getAsBoolean(),
                    jsonObject.get("type").getAsString(),
                    jsonObject.get("message").getAsString(),
                    jsonObject.get("code").getAsInt(),
                    data);
        }

        @Override
        public SendRespondResponse deserialize(
                JsonElement json,
                Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {

            SendRespondResponse response = null;
            JsonObject jsonObject = json.getAsJsonObject();
            if (jsonObject.has("validation")) {
                JsonElement element = jsonObject.get("validation");
                JsonElement dataElement = jsonObject.get("data");
                if (element != null && !element.isJsonNull()
                        && dataElement != null && !element.isJsonNull()) {
                    if (element.isJsonArray() && dataElement.isJsonObject()) {
                        response = getResponse(
                                jsonObject,
                                new Gson().fromJson(jsonObject.get("data").getAsJsonObject(),
                                        ContentData.class));
                        response.setValidation(null);
                    } else if (jsonObject.get("code").getAsInt() == 405) {
                        response = getResponse(jsonObject, null);
                    } else {
                        JsonObject valueStr = element.getAsJsonObject();
                        response = getResponse(jsonObject, null);

                        ResponseValidationError validation = new Gson().fromJson(valueStr, ResponseValidationError.class);
                        response.setValidation(validation);
                    }
                }
            }

            return response;
        }
    }
}

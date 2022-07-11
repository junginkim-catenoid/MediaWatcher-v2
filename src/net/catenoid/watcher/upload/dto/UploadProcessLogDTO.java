package net.catenoid.watcher.upload.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.catenoid.watcher.upload.types.UploadMode;

import java.security.InvalidParameterException;

public class UploadProcessLogDTO {

    private final UploadMode uploadMode;
    private final String currentStep;
    private final String totalStep;
    private final String title;
    private String description;
    private Object fileObject;

    public UploadProcessLogDTO(UploadMode uploadMode, String currentStep, String title, String description, Object fileObject) {
        this.uploadMode = uploadMode;
        this.currentStep = currentStep;
        this.totalStep = getTotalStep(uploadMode);
        this.title = title;
        this.description = description;
        this.fileObject = fileObject;
    }

    public UploadProcessLogDTO(UploadMode uploadMode, String currentStep, String title) {
        this.uploadMode = uploadMode;
        this.currentStep = currentStep;
        this.totalStep = getTotalStep(uploadMode);
        this.title = title;
    }

    public UploadProcessLogDTO(UploadMode uploadMode, String currentStep, String title, String description) {
        this.uploadMode = uploadMode;
        this.currentStep = currentStep;
        this.totalStep = getTotalStep(uploadMode);
        this.title = title;
        this.description = description;
    }

    public String getJsonStringMessage() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public JsonObject getJsonObjectMessage() {
        Gson gson = new Gson();
        return (JsonObject) gson.toJsonTree(this);
    }

    private String getTotalStep(UploadMode uploadMode) {
        if (uploadMode == null) {
            throw new InvalidParameterException();
        }

        if (uploadMode == UploadMode.FTP) {
            return "10";
        }

        if (uploadMode == UploadMode.KUS) {
            return "12";
        }

        throw new InvalidParameterException();
    }

}

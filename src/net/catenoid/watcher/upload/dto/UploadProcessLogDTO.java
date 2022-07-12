package net.catenoid.watcher.upload.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.catenoid.watcher.upload.types.UploadMode;
import org.apache.log4j.MDC;

import java.security.InvalidParameterException;
import java.util.ArrayList;

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

    public String getJsonLogMsg() {
        putMdcUploadInfo();
        return this.title;
    }
    private void putMdcUploadInfo() {
        MDC.clear();
        MDC.remove("fileObject");

        MDC.put("uploadMode", this.uploadMode);
        MDC.put("currentStep", this.currentStep);
        MDC.put("totalStep", this.totalStep);

        if (hasText(this.description)) {
            MDC.put("description", this.description);
        }

        if (this.fileObject != null) {
            MDC.put("fileObject", convertJsonStringMessage(this.fileObject));
        }
    }

    public String convertJsonStringMessage(Object fileObject) {
        Gson gson = new Gson();
        return gson.toJson(fileObject);
    }

    public JsonObject getJsonObjectMessage(Object fileObject) {
        Gson gson = new Gson();
        return (JsonObject) gson.toJsonTree(fileObject);
    }

    private String getTotalStep(UploadMode uploadMode) {
        if (uploadMode == null) {
            throw new InvalidParameterException();
        }

        if (uploadMode == UploadMode.FTP) {
            return "05";
        }

        if (uploadMode == UploadMode.KUS) {
            return "05";
        }

        throw new InvalidParameterException();
    }

    private static boolean hasText(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        return true;
    }

}

package net.catenoid.watcher.upload.dto;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.catenoid.watcher.upload.types.UploadMode;
import org.apache.log4j.Logger;

import java.security.InvalidParameterException;

public class UploadProcessLogDTO {

    private static Logger log = Logger.getLogger(UploadProcessLogDTO.class);

    private final UploadMode uploadMode;

    private final String currentStep;

    private final String totalStep;

    private final String title;

    private String description;

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
        this.description = getDescriptionJson();
    }

    public String getJsonMessage() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    private String getDescriptionJson() {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ddd", "ddd");
        return gson.toJson(jsonObject);
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

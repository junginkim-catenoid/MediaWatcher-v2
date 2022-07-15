package net.catenoid.watcher.upload.dto;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.catenoid.watcher.upload.types.UploadMode;
import org.apache.log4j.MDC;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UploadProcessLogDTO {

    private final UploadMode uploadMode;
    private final String currentStep;
    private final String totalStep;
    private final String title;
    private String description;

    private String contentProviderKey;

    private String physicalPath;

    private String uploadPath;

    private String uploadFileKey;

    private Object fileObject;

    public UploadProcessLogDTO(UploadMode uploadMode, String currentStep, String title, String description, ArrayList<FileItemDTO> files) {
        this.uploadMode = uploadMode;
        this.currentStep = currentStep;
        this.totalStep = getTotalStep(uploadMode);
        this.title = title;
        this.description = description;

        if (files.size() > 0) {
            this.contentProviderKey = getContentProviderKey(files);
        }
    }

    public UploadProcessLogDTO(UploadMode uploadMode, String currentStep, String title, String description, FileItemDTO item) {
        this.uploadMode = uploadMode;
        this.currentStep = currentStep;
        this.totalStep = getTotalStep(uploadMode);
        this.title = title;
        this.description = description;
        setFileItemDto(item);
    }

    public UploadProcessLogDTO(UploadMode uploadMode, String currentStep, String title, String description, KollusApiWatcherContentDTO[] contentsDTO) {
        this.uploadMode = uploadMode;
        this.currentStep = currentStep;
        this.totalStep = getTotalStep(uploadMode);
        this.title = title;
        this.description = description;

        if (contentsDTO.length > 0) {
            this.contentProviderKey = contentsDTO[0].result.content_provider_key;
        }
    }

    public UploadProcessLogDTO(UploadMode uploadMode, String currentStep, String title, String description, KollusApiWatcherContentDTO contentDTO) {
        this.uploadMode = uploadMode;
        this.currentStep = currentStep;
        this.totalStep = getTotalStep(uploadMode);
        this.title = title;
        this.description = description;

        if (contentDTO != null) {
            this.contentProviderKey = contentDTO.result.content_provider_key;
        }
    }



    public UploadProcessLogDTO(UploadMode uploadMode, String currentStep, String title, String description) {
        this.uploadMode = uploadMode;
        this.currentStep = currentStep;
        this.totalStep = getTotalStep(uploadMode);
        this.title = title;
        this.description = description;
    }

    private String getContentProviderKey(ArrayList<FileItemDTO> files) {
        return files.get(0).getContentProviderKey();
    }

    public String getJsonLogMsg() {
        putMdcUploadInfo();
        return this.title;
    }
    private void putMdcUploadInfo() {
        MDC.clear();

        MDC.put("uploadMode", this.uploadMode);
        MDC.put("currentStep", this.currentStep);
        MDC.put("totalStep", this.totalStep);

        if (hasText(this.description)) {
            MDC.put("description", this.description);
        }

        if (hasText(this.contentProviderKey)) {
            MDC.put("contentProviderKey", this.contentProviderKey);
        }

        if (hasText(this.uploadPath)) {
            MDC.put("uploadPath", this.uploadPath);
        }

        if (hasText(this.physicalPath)) {
            MDC.put("physicalPath", this.physicalPath);
        }

        if (hasText(this.uploadFileKey)) {
            MDC.put("uploadFileKey", this.uploadFileKey);
        }



//        if (this.fileObject != null) {
//            HashMap nestedMdc = new HashMap<String, String>();
//            nestedMdc.put("bar","baz");
//            MDC.put("foo",nestedMdc);
//
//            MDC.put("fileObject", this.fileObject);
//        }


    }

    private void setFileItemDto(FileItemDTO item) {
        this.physicalPath = item.getPhysicalPath();
        this.uploadPath = item.getUploadPath();
        this.contentProviderKey = item.getContentProviderKey();
        this.uploadFileKey = item.getUploadFileKey();
    }

    public String convertJsonStringMessage(Object fileObject) {
        Gson gson = new Gson();
        return gson.toJson(fileObject);
    }

    // Object 형태가 아니라서 안됨
    public JsonObject getJsonObjectMessage(Object fileObject) {
        Gson gson = new Gson();
        return gson.toJsonTree(fileObject).getAsJsonObject();
    }

    // JsonArray 는 로그로 안찍힘
    public JsonArray getJsonObjectMessage2(Object fileObject) {
        Gson gson = new Gson();
        return gson.toJsonTree(fileObject).getAsJsonArray();
    }

    public JsonArray getJsonArrayMessage(Object fileObject) {
        Gson gson = new Gson();
        JsonArray jsonArray = new JsonArray();
        List<FileItemDTO> fileItemDTOS = (List<FileItemDTO>) fileObject;

        for (FileItemDTO item : fileItemDTOS) {
            String json = gson.toJson(item);
            JsonObject convertedObject = new Gson().fromJson(json, JsonObject.class);
            jsonArray.add(convertedObject);
        }

        return jsonArray;
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

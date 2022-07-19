package net.catenoid.watcher.upload.dto;

import net.catenoid.watcher.upload.types.UploadMode;
import net.catenoid.watcher.upload.types.UploadProcessStep;
import org.apache.log4j.MDC;

import java.security.InvalidParameterException;
import java.util.ArrayList;

public class UploadProcessLogDTO {

    private UploadMode uploadMode;
    private String currentStep;
    private String totalStep;
    private String stepName;
    private String description;
    private String title;
    private String contentProviderKey;
    private String physicalPath;
    private String uploadPath;
    private String uploadFileKey;
    private ContentInfoDTO mediaInfo;

    public UploadProcessLogDTO(UploadMode uploadMode, UploadProcessStep uploadProcessStep, String stepName,
                               String description, ArrayList<FileItemDTO> files) {
        this.uploadMode = uploadMode;
        this.currentStep = uploadProcessStep.getCurrentStep();
        this.totalStep = uploadProcessStep.getTotalStep(uploadMode);
        this.stepName = stepName;
        this.description = description;

        if (files.size() > 0) {
            this.contentProviderKey = getContentProviderKey(files);
        }
    }

    public UploadProcessLogDTO(UploadMode uploadMode, UploadProcessStep uploadProcessStep, int fileOrder, String stepName,
                               String description, FileItemDTO item) {
        this.uploadMode = uploadMode;
        this.currentStep = uploadProcessStep.getCurrentStep() + fileOrder;
        this.totalStep = uploadProcessStep.getTotalStep(uploadMode);
        this.stepName = stepName;
        this.description = description;
        setFileItemDto(item);
    }

    public UploadProcessLogDTO(UploadMode uploadMode, UploadProcessStep uploadProcessStep, String stepName,
                               String description, FileItemDTO item) {
        this.uploadMode = uploadMode;
        this.currentStep = uploadProcessStep.getCurrentStep();
        this.totalStep = uploadProcessStep.getTotalStep(uploadMode);
        this.stepName = stepName;
        this.description = description;
        setFileItemDto(item);
    }

    public UploadProcessLogDTO(UploadMode uploadMode, UploadProcessStep uploadProcessStep, String stepName, String description, KollusApiWatcherContentDTO[] contentsDTO) {
        this.uploadMode = uploadMode;
        this.currentStep = uploadProcessStep.getCurrentStep();
        this.totalStep = uploadProcessStep.getTotalStep(uploadMode);
        this.stepName = stepName;
        this.description = description;
        if (contentsDTO.length > 0) {
            this.contentProviderKey = contentsDTO[0].result.content_provider_key;
        }
    }

    public UploadProcessLogDTO(UploadMode uploadMode, UploadProcessStep uploadProcessStep, String stepName, String description, KollusApiWatcherContentDTO contentDTO) {
        this.uploadMode = uploadMode;
        this.currentStep = uploadProcessStep.getCurrentStep();
        this.totalStep = uploadProcessStep.getTotalStep(uploadMode);
        this.stepName = stepName;
        this.description = description;
        if (contentDTO != null) {
            this.contentProviderKey = contentDTO.result.content_provider_key;
        }
    }

    public UploadProcessLogDTO(UploadMode uploadMode, UploadProcessStep uploadProcessStep, int fileOrder, String stepName, String description, KollusApiWatcherContentDTO contentDTO) {
        this.uploadMode = uploadMode;
        this.currentStep = uploadProcessStep.getCurrentStep() + fileOrder;
        this.totalStep = uploadProcessStep.getTotalStep(uploadMode);
        this.stepName = stepName;
        this.description = description;
        if (contentDTO != null) {
            this.contentProviderKey = contentDTO.result.content_provider_key;
        }
    }

    public UploadProcessLogDTO(UploadMode uploadMode, UploadProcessStep uploadProcessStep, String stepName, String description) {
        this.uploadMode = uploadMode;
        this.currentStep = uploadProcessStep.getCurrentStep();
        this.totalStep = uploadProcessStep.getTotalStep(uploadMode);
        this.stepName = stepName;
        this.description = description;
    }

    private void setFileItemDto(FileItemDTO item) {
        this.physicalPath = item.getPhysicalPath();
        this.uploadPath = item.getUploadPath();
        this.contentProviderKey = item.getContentProviderKey();
        this.uploadFileKey = item.getUploadFileKey();
        this.title = item.getTitle();

        if (item.getMediaInfo() != null) {
            this.mediaInfo = item.getMediaInfo();
        }
    }

    private void putMdcUploadInfo() {
        MDC.clear();

        MDC.put("uploadMode", this.uploadMode);
        MDC.put("currentStep", this.currentStep);
        MDC.put("totalStep", this.totalStep);
        MDC.put("description", this.description);

        if (hasText(this.contentProviderKey)) {
            MDC.put("contentProviderKey", this.contentProviderKey);
        }

        if (hasText(this.title)) {
            MDC.put("title", this.title);
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

        if (this.mediaInfo != null) {
            MDC.put("mediaInfo", this.mediaInfo);
        }
    }

    public String getJsonLogMsg() {
        putMdcUploadInfo();
        return this.stepName;
    }

    private String getContentProviderKey(ArrayList<FileItemDTO> files) {
        if (files.size() == 0) {
            throw new InvalidParameterException();
        }
        return files.get(0).getContentProviderKey();
    }

    private static boolean hasText(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        return true;
    }
}

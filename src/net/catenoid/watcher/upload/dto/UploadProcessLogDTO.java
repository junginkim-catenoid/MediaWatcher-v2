package net.catenoid.watcher.upload.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.catenoid.watcher.upload.types.UploadMode;
import net.catenoid.watcher.upload.types.UploadProcessStep;
import net.logstash.log4j.JSONEventLayoutV1;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import java.security.InvalidParameterException;
import java.util.*;

import static net.catenoid.watcher.Watcher.VERSION;

public class UploadProcessLogDTO {

    private static Logger uploadProcessLog = Logger.getLogger("UploadProcessLog");
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
    private Map<String,String> mediaInfo;
    private long bytes;

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

    public String getJsonLogMsg() {
        setContextCustomFields();
        putMdcUploadInfo();
        return this.stepName;
    }

    private void setFileItemDto(FileItemDTO item) {
        this.physicalPath = item.getPhysicalPath();
        this.uploadPath = item.getUploadPath();
        this.contentProviderKey = item.getContentProviderKey();
        this.uploadFileKey = item.getUploadFileKey();
        this.title = item.getTitle();
        this.bytes = item.getFilesize();

        if (item.getMediaInfo() != null) {
            setMediaInfoToMap(item.getMediaInfo());
        }
    }

    private void putMdcUploadInfo() {
        MDC.clear();

        MDC.put("uploadMode", this.uploadMode);
        MDC.put("currentStep", this.currentStep);
        MDC.put("totalStep", this.totalStep);
        MDC.put("description", this.description);

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

        if (this.bytes > 0) {
            MDC.put("bytes", this.bytes);
        }

        if (this.mediaInfo != null) {
            MDC.put("mediaInfo", this.mediaInfo);
        }
    }
    private void setMediaInfoToMap(ContentInfoDTO mediaInfo) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        this.mediaInfo = mapper.convertValue(mediaInfo, Map.class);
    }

    private void setContextCustomFields() {
        Appender appender = uploadProcessLog.getAppender("uploadProcessFile");
        JSONEventLayoutV1 layoutV1 = (JSONEventLayoutV1) appender.getLayout();
        List<String> customFields = new ArrayList<>();

        customFields.add("stream_type:vod");

        if (hasText(VERSION)) {
            customFields.add("version:" + VERSION);
        }
        if (hasText(this.contentProviderKey)) {
            customFields.add("cpk:" + this.contentProviderKey);
        }

        StringBuilder sb = new StringBuilder();
        for (int i=0; i<customFields.size(); i++) {
            sb.append(customFields.get(i));
            if (i < customFields.size()-1) {
                sb.append(",");
            }
        }

        if (hasText(sb.toString())) {
            layoutV1.setUserFields(sb.toString());
        }
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

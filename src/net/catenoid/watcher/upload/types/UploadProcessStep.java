package net.catenoid.watcher.upload.types;

import java.security.InvalidParameterException;

public enum UploadProcessStep {

    LS_PARSING_NEW_FILES("1"),
    LS_PARSING_NEW_FILES_SUB("1-"),
    HTTP_REGISTER_SERVER_SEND("2"),
    HTTP_REGISTER_SERVER_SEND_SUB("2-"),
    CREATE_SNAP_FILE("3"),
    WORK_FILE_MOVE_DIRECTORY("4"),
    WORK_FILE_MOVE_DIRECTORY_SUB("4-"),
    HTTP_COMPLETE_SERVER_SEND("5"),
    HTTP_COMPLETE_SERVER_SEND_SUB("5-");

    private String currentStep;

    UploadProcessStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public String getCurrentStep() {
        return this.currentStep;
    }

    public String getTotalStep(UploadMode uploadMode) {
        if (uploadMode == null) {
            throw new InvalidParameterException();
        }

        if (uploadMode == UploadMode.FTP) {
            return "5";
        }

        if (uploadMode == UploadMode.KUS) {
            return "5";
        }

        throw new InvalidParameterException();
    }
}

package net.catenoid.watcher.upload.utils;

import net.catenoid.watcher.config.TransferFileExtConf;

import java.io.File;
import java.security.InvalidParameterException;

public class FileExtTransfer {

    private String contentProviderKey;

    private String fileExt;

    private TransferFileExtConf[] conf;
    public FileExtTransfer(TransferFileExtConf[] conf, String contentProviderKey, String fileExt) {
        this.conf = conf;
        this.contentProviderKey = contentProviderKey;
        this.fileExt = fileExt;
    }

    public boolean isTarget() {

        if (this.conf == null || this.conf.length == 0) {
            return false;
        }

        if (!hasText(this.contentProviderKey) || !hasText(this.fileExt)) {
            return false;
        }

        for (TransferFileExtConf data : this.conf) {
            if (this.contentProviderKey.equals(data.getContentProviderKey()) && isTransferTargetExt(data.getTransferTargetExt(), fileExt)) {
                return true;
            }
        }

        return false;
    }

    public String run(String physicalPath) {

        String convertFilePath = physicalPath;

        return convertFilePath;
    }

    private boolean hasText(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }
        return true;
    }

    private boolean isTransferTargetExt(TransferFileExtConf.TransferTargetExt[] data, String fileExt) {

        for (TransferFileExtConf.TransferTargetExt ext : data) {
            if (ext.from.equals(fileExt)) {
                return true;
            }
        }

        return false;
    }

}

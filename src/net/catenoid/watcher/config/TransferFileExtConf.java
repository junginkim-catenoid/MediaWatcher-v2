package net.catenoid.watcher.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransferFileExtConf {

    @Expose
    @SerializedName("cpk")
    private String contentProviderKey;

    @Expose
    @SerializedName("transfer_target_ext")
    private TransferTargetExt[] transferTargetExt;

    public static class TransferTargetExt {

        @Expose
        @SerializedName("from")
        public String from;

        @Expose
        @SerializedName("to")
        public String to;

        public TransferTargetExt(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }

    public String getContentProviderKey() {
        return this.contentProviderKey;
    }

    public TransferTargetExt[] getTransferTargetExt() {
        return this.transferTargetExt;
    }
}

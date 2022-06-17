package net.catenoid.watcher.utils;

import net.catenoid.watcher.config.Config;
import net.catenoid.watcher.config.WatcherFolder;
import net.catenoid.watcher.upload.utils.CommonUtils;
import org.apache.log4j.Logger;

public class KusUploadUtils extends CommonUtils {

    private static Logger log = Logger.getLogger(KusUploadUtils.class);

    public KusUploadUtils(WatcherFolder info, Config conf) {
        // TODO Auto-generated constructor stub
        super(info, conf);
    }
}

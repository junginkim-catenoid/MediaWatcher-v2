package net.catenoid.watcher.upload.utils;

import net.catenoid.watcher.config.Config;
import net.catenoid.watcher.config.WatcherFolder;
import org.apache.log4j.Logger;

public class FtpUploadUtils extends CommonUtils {

    private static Logger log = Logger.getLogger(FtpUploadUtils.class);
//	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final int H2_TRACE_LEVEL = 0;

    public FtpUploadUtils() {
    }

    // 공통 Utils 생성자로 전역변수 정의
    public FtpUploadUtils(WatcherFolder info, Config conf) {
        super(info, conf);
    }
}

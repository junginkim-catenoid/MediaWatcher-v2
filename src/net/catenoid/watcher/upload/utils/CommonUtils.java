package net.catenoid.watcher.upload.utils;

import net.catenoid.watcher.config.Config;
import net.catenoid.watcher.config.WatcherFolder;
import org.apache.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CommonUtils {

    private static Logger log = Logger.getLogger(CommonUtils.class);

    /**
     * HTTP 통신에 사용될 기본 문자열 CHARSET 설정 (UTF-8)
     */
    protected static String DEFAULT_CHARSET = "UTF-8";

    // WatcherFile 정보
    public WatcherFolder info;
    // Config 정보
    public Config conf;

    public CommonUtils() {
    }

    public CommonUtils(WatcherFolder watcherFolder, Config config) {
        this.info = watcherFolder;
        this.conf = config;
    }

    public void ExceptionLogPrint(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        log.error(exceptionAsString);
        log.error(e.toString());
    }

}

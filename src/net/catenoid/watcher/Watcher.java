package net.catenoid.watcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import net.catenoid.watcher.config.Config;
import net.catenoid.watcher.config.WatcherFolder;
import net.catenoid.watcher.job.Role_Watcher;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.quartz.SchedulerException;

public class Watcher {

    private static Logger log = Logger.getLogger(Watcher.class);

    public static String VERSION = getManifestInfo();

    public static void main(String[] args) throws Exception {
        String user_dir = System.getProperty("user.dir");


        String[] paths = {
                user_dir + "/log4j.properties",
                user_dir + "/bin/log4j.properties",
                user_dir + "/conf/log4j.properties"
        };

        // log Property 주입 , 주입 이후에 로그 사용 가능
        for(String path : paths) {
            File propFile = new File(path);
            if (propFile.exists()) {
                PropertyConfigurator.configureAndWatch(path, 60000L);
            }
        }

        log.info("user_dir : " + user_dir);

        String mode = null;

        for(int i=0;args != null && i < args.length; i++) {
            if(args[i].startsWith("start")){
                mode = args[i];

            }else if(args[i].startsWith("stop")){
                mode = args[i];
            }
        }

        log.info("mode : " + mode);

        start();

//        if ("start".equals(mode)){
//            try {
//                start();
//            } catch(Exception e) {
////                log.error(WatcherUtils.getStackTrace(e));
//            }
//        }else if("stop".equals(mode)){
//            stop();
//        }
    }

    public static void start() throws Exception {

        log.info(LogAction.PROGRAM_START);

        /**
         * 프로그램 버전 출력
         */
        printVersion();

        /**
         * 초기화 및 환경 정보 확인 작업
         */
        Config conf = Config.getConfig();

        /**
         * 시작과 함께 모든 Watcher를 한번 실행하도록 옵션이 설정된 경우
         */
        if(conf.isStartWithRun()) {
            runOneTimeWatcher(conf.getWatchers());
        }
    }

    public static void stop() throws Exception {

    }

    public static void printVersion() {
        log.info(LogAction.PROGRAM_VERSION + Watcher.VERSION);
    }

    public static String getManifestInfo() {
        Enumeration resEnum;
        try {
            resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
            while (resEnum.hasMoreElements()) {
                try {
                    URL url = (URL)resEnum.nextElement();
                    InputStream is = url.openStream();
                    if (is != null) {
                        Manifest manifest = new Manifest(is);
                        Attributes mainAttribs = manifest.getMainAttributes();
                        String version = mainAttribs.getValue("MediaWatcher2-Version");
                        if(version != null) {
                            return version;
                        }
                    }
                }
                catch (Exception e) {
                    // Silently ignore wrong manifests on classpath?
                }
            }
        } catch (IOException e1) {
            // Silently ignore wrong manifests on classpath?
        }
        return null;
    }

    /**
     * 시작과 동시에 watcher를 한번씩 실행하도록 옵션을 설정한 경우 실행된다.
     * @param watchers
     * @throws SchedulerException
     */
    private static void runOneTimeWatcher(WatcherFolder[] watchers) throws SchedulerException {

        if(watchers == null || watchers.length == 0)
            return;

        for(WatcherFolder info : watchers) {
            if(info.isEnabled()) {
                Role_Watcher watcher = new Role_Watcher();
                watcher.run(info);
            }
        }
    }
}

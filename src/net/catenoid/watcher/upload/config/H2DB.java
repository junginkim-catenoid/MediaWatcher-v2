package net.catenoid.watcher.upload.config;

import net.catenoid.watcher.config.WatcherFolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;

public class H2DB {

    private static Logger log= LogManager.getLogger(H2DB.class);
    private static final int H2_TRACE_LEVEL = 0;

    /**
     * 데이터베이스 연결 전역으로 사용해야할 변수 Local 사용시 주석 필수
     *
     * @return Connection
     * @throws Exception
     */
    public static Connection connectDatabase(WatcherFolder info) throws Exception {

        String appPath = System.getProperty("user.dir");
//		String appPath = "tcp://localhost:1521";

        String dbPath = String.format("%s/%s", appPath, info.getName());

        Class.forName("org.h2.Driver");
        String dbURL = String.format("jdbc:h2:file:%s/MEDIAWATCHER;TRACE_LEVEL_FILE=%d;TRACE_LEVEL_SYSTEM_OUT=%d;", dbPath, H2_TRACE_LEVEL, H2_TRACE_LEVEL);
//		String dbURL = String.format("jdbc:h2:%s;TRACE_LEVEL_FILE=%d;TRACE_LEVEL_SYSTEM_OUT=%d;", dbPath,H2_TRACE_LEVEL, H2_TRACE_LEVEL);

        Connection connect = null;
        try {
            Class.forName("org.h2.Driver");
            connect = DriverManager.getConnection(dbURL);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }

//		log.info("H2DB is connected");
        return connect;
    }

}

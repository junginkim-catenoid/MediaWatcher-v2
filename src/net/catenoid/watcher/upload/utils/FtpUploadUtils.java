package net.catenoid.watcher.upload.utils;

import net.catenoid.watcher.config.Config;
import net.catenoid.watcher.config.WatcherFolder;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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

    /**
     * Database 연결 statment를 얻는다. 연결풀을 사용할때를 대비해 파라미터를 받음.
     *
     * @param name
     * @param h2Conn
     * @return Statement
     * @throws SQLException
     */
    public Statement getStatment(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt;
    }

    /**
     * 데이터베이스 연결 종료
     *
     * @param jobName
     * @param stmt
     * @throws SQLException
     */
    public void shutdownDerby(Connection conn, Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error(e.toString());
                return;
            }
        }

        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
            conn = null;
        }
    }
}

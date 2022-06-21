package net.catenoid.watcher.uploadDao;

import net.catenoid.watcher.upload.utils.FtpUploadUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

public class FtpUploadDao {

    private static Logger log = Logger.getLogger(FtpUploadDao.class);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    protected Statement stmt = null;
    protected Connection conn = null;
    protected FtpUploadUtils utils = null;

    public FtpUploadDao() {
    }

    /**
     * 기존의 Table이 없을경우 신규로 생성
     *
     * @throws SQLException
     */
    protected void createNewTable() throws SQLException {
        String sql = "CREATE TABLE FILES (" + "  PATH VARCHAR(2048) PRIMARY KEY " + ", DOMAIN VARCHAR(1024)"
                + ", OLD_SIZE BIGINT" + ", NEW_SIZE BIGINT" + ", OLD_DATE TIMESTAMP" + // 파일 마지막 업데이트 이전 시간
                ", NEW_DATE TIMESTAMP" + // 파일 마지막 업데이트 시간
                ", ERR_DATE TIMESTAMP" + // 미디어 확인 오류시 오류 발생 작업시간
                ", CHK_DATE TIMESTAMP" + // 최종 확인 작업 시간
                ", COMP_CNT INTEGER" + // 비교횟수
                ", STATUS SMALLINT" + ", UPLOAD_PATH VARCHAR(2048) " + // 감시경로 (논리적 경로) {upload_path}
                ", CID VARCHAR(50) " + // 서버로 부터 획득한 CID {key}
                ", CPATH VARCHAR(2048) " + // 서버로 부터 획득한 CPATH {content_path}
                ", SNAP_PATH VARCHAR(2048) " + // SNAP 파일 경로 {snapshot_path}
                ", TITLE VARCHAR(2048) " + // TITLE {title}
                ", MD5 VARCHAR(50) " + // file md5 checksun {md5}
                ", CHECKSUM_TYPE INT " + // checksum type{0:0ff, 1:md5}
                ", POSTER_POS INT " + // poster position
                ", POSTER_WIDTH INT " + // poster width
                ", POSTER_HEIGHT INT " + // poster height
                ")";

        stmt.executeUpdate(sql);
    }

}

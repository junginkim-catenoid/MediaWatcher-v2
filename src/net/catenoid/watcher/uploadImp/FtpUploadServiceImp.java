package net.catenoid.watcher.uploadImp;

import net.catenoid.watcher.config.Config;
import net.catenoid.watcher.config.WatcherFolder;
import net.catenoid.watcher.upload.FtpUploadService;
import net.catenoid.watcher.upload.config.H2DB;
import net.catenoid.watcher.upload.dto.FileItemDTO;
import net.catenoid.watcher.upload.utils.FtpUploadUtils;
import net.catenoid.watcher.uploadDao.FtpUploadDao;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FtpUploadServiceImp extends FtpUploadDao implements FtpUploadService  {

    private static Logger log = Logger.getLogger(FtpUploadServiceImp.class);

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


    private ArrayList<FileItemDTO> lsFiles = null;
    private ArrayList<FileItemDTO> lsDirs = null;


    /**
     * Job을 시작한 시간 (dateFormat style에 따른다.)
     */
    private String check_time;
    /**
     * Role_Watcher에서 예외 발생시 종료시키기 위해사용
     */

    public FtpUploadServiceImp() {
    }
    public FtpUploadServiceImp(WatcherFolder info, Config conf) {
        super.utils = new FtpUploadUtils(info, conf);

        Date now = new Date();
        this.check_time = dateFormat.format(now);

        try {
            super.conn = H2DB.connectDatabase(info);
            super.stmt = utils.getStatment(conn);

            createTable();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("FtpUploadServiceImp Construct Err : " + e.getMessage());
            utils.shutdownDerby(conn, stmt);
        }
    }

    @Override
    public void findWorkFileList() throws Exception {

    }

    @Override
    public void renewWorkFileList() throws Exception {

    }

    /**
     * Derby 테이블을 생성한다.
     *
     * @throws SQLException
     */

    private void createTable() throws SQLException {
        if (conn == null) {
            log.warn("Not connect H2 database");
            return;
        }
        if (stmt == null) {
            log.warn("Not Statement instance");
            return;
        }

        /**
         * FILES 테이블이 존재하면 그냥 패스, 없으면 생성 (Apache derby) 무조건 생성하는 경우 오히려 시간이 더 걸리거나 메모리가
         * 증가하는 현상 발견 (H2)에서는 현상 발견하지 못함.
         */
        String sql = "SHOW TABLES";

//		sql = "SELECT * FROM FILES;";

        ResultSet rs = stmt.executeQuery(sql);

        int num = 0;

        while (rs.next()) {
            num++;
        }

        if (num == 0) {
            createNewTable();
        }
    }
}

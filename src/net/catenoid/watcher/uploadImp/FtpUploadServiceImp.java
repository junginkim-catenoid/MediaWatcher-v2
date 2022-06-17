package net.catenoid.watcher.uploadImp;

import net.catenoid.watcher.config.Config;
import net.catenoid.watcher.config.WatcherFolder;
import net.catenoid.watcher.upload.FtpUploadService;
import net.catenoid.watcher.upload.config.H2DB;
import net.catenoid.watcher.upload.dto.FileItemDTO;
import net.catenoid.watcher.upload.utils.FtpUploadUtils;
import net.catenoid.watcher.uploadDao.FtpUploadDao;
import org.apache.log4j.Logger;

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
//            super.conn = H2DB.connectDatabase(info);
//            super.stmt = utils.getStatment(conn);

//            createTable();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.error("FtpUploadServiceImp Construct Err : " + e.getMessage());
//            utils.shutdownDerby(conn, stmt);
        }
    }

    @Override
    public void findWorkFileList() throws Exception {

    }

    @Override
    public void renewWorkFileList() throws Exception {

    }
}

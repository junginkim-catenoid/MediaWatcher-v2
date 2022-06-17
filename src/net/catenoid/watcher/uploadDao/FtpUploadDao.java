package net.catenoid.watcher.uploadDao;

import net.catenoid.watcher.upload.utils.FtpUploadUtils;
import org.apache.log4j.Logger;

import java.sql.Connection;
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

}

package net.catenoid.watcher.uploadImp;

import net.catenoid.watcher.config.Config;
import net.catenoid.watcher.config.WatcherFolder;
import net.catenoid.watcher.upload.KusUploadService;
import net.catenoid.watcher.utils.KusUploadUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class KusUploadServiceImp implements KusUploadService {

    private static Logger log = Logger.getLogger(KusUploadServiceImp.class);

//    private SendFileItemsDTO fileList = null;
    private List<String> dirList = null;

    protected KusUploadUtils utils = null;

    public KusUploadServiceImp(WatcherFolder info, Config conf) {
        // TODO Auto-generated method stub

        utils = new KusUploadUtils(info, conf);
    }
    @Override
    public void findWorkFileList() throws Exception {

//        this.fileList = new SendFileItemsDTO();
//        this.dirList = new ArrayList<String>();
//
//        readFiles(fileList, dirList);
//
//        if(fileList.size() == 0) {
//            return;
//        }
//        sendToHttpApi(fileList, "register");
    }

    @Override
    public void moveToWorkFiles() throws Exception {

    }

    @Override
    public int sendCompleteWorkFiles() throws Exception {
        return 0;
    }
}

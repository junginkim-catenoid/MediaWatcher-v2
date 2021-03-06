package net.catenoid.watcher.uploadImp;

import com.google.gson.Gson;
import com.kollus.json_data.BaseCommand;
import net.catenoid.watcher.config.Config;
import net.catenoid.watcher.config.WatcherFolder;
import net.catenoid.watcher.upload.KusUploadService;
import net.catenoid.watcher.upload.dto.*;
import net.catenoid.watcher.upload.types.UploadMode;
import net.catenoid.watcher.upload.types.UploadProcessStep;
import net.catenoid.watcher.utils.KusUploadUtils;
import net.logstash.log4j.JSONEventLayoutV1;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class KusUploadServiceImp implements KusUploadService {

    private static Logger log = Logger.getLogger(KusUploadServiceImp.class);

    private static Logger uploadProcessLog = Logger.getLogger("UploadProcessLog");

    private SendFileItemsDTO fileList = null;
    private List<String> dirList = null;

    protected KusUploadUtils utils = null;

    public KusUploadServiceImp(WatcherFolder info, Config conf) {
        // TODO Auto-generated method stub

        utils = new KusUploadUtils(info, conf);
    }

    @Override
    public void findWorkFileList() throws Exception {


        // TODO Auto-generated method stub
        this.fileList = new SendFileItemsDTO();
        this.dirList = new ArrayList<String>();

        readFiles(fileList, dirList);

        if(fileList.size() == 0) {
            return;
        }

        UploadProcessLogDTO step1Msg = new UploadProcessLogDTO(UploadMode.KUS, UploadProcessStep.LS_PARSING_NEW_FILES, "Ls Parsing New File Items STEP", "Ls parsing file cnt : " + this.fileList.size(), this.fileList);
        uploadProcessLog.info(step1Msg.getJsonLogMsg());

        sendToHttpApi(fileList, "register");
    }

    @Override
    public void moveToWorkFiles() throws Exception {
        // TODO Auto-generated method stub

        if(fileList.size() == 0) {
            return;
        }

        int snapFileCnt = utils.createSnapFile(fileList);
        UploadProcessLogDTO step3Msg = new UploadProcessLogDTO(UploadMode.KUS, UploadProcessStep.CREATE_SNAP_FILE, "CREATE Snapshot File Create STEP", "snapshotCnt : " + snapFileCnt, fileList);
        uploadProcessLog.info(step3Msg.getJsonLogMsg());

        if(fileList.size() == 0) {
            return;
        }

        UploadProcessLogDTO step4Msg = new UploadProcessLogDTO(UploadMode.KUS, UploadProcessStep.WORK_FILE_MOVE_DIRECTORY, "Work File Move Directory STEP", "move file count : " + fileList.size(), fileList);
        uploadProcessLog.info(step4Msg.getJsonLogMsg());

        utils.moveToWorkDir(fileList, UploadMode.KUS);

    }

    @Override
    public int sendCompleteWorkFiles() throws Exception {
        int cnt = 0;

        if(fileList.size() == 0) {
            return cnt;
        }

        String responseBody = utils.sendPostToApi(fileList, "copy");

        if (responseBody == null) {
            return cnt;
        }

        Gson gson = BaseCommand.gson(false);
        KollusApiWatchersDTO apiResult = gson.fromJson(responseBody, KollusApiWatchersDTO.class);

        UploadProcessLogDTO step5Msg = new UploadProcessLogDTO(UploadMode.KUS, UploadProcessStep.HTTP_COMPLETE_SERVER_SEND, "WORK File Info Send Http Server", "sendFtpCompleteApiCnt : " + apiResult.result.watcher_files.length, apiResult.result.watcher_files);
        uploadProcessLog.info(step5Msg.getJsonLogMsg());

        if (apiResult.error != 0) {
            log.error(responseBody);
            UploadProcessLogDTO step5ErrMsg = new UploadProcessLogDTO(UploadMode.KUS, UploadProcessStep.HTTP_COMPLETE_SERVER_SEND, "[ERROR] WORK File Info Send Http Server STEP","error code : " + apiResult.error);
            uploadProcessLog.error(step5ErrMsg.getJsonLogMsg());
            utils.failApiResultOrRegisterProcess(apiResult, null);
            return cnt;
        }

        for (int i = 0; i < apiResult.result.watcher_files.length; i++) {

            KollusApiWatcherContentDTO item = apiResult.result.watcher_files[i];
            if (item.error == 0) {
                // error??? ?????? ????????? media_content_id??? ????????? ???????????? ?????? ?????????
                FileItemDTO findItem = utils.findSendItem(fileList, item.result.key);
                if (findItem == null) {
                    log.error("FileItem  ?????? ????????? ?????? ??? ????????????. [" + item.result.key + "]");
                    UploadProcessLogDTO step5SubMsg = new UploadProcessLogDTO(UploadMode.KUS, UploadProcessStep.HTTP_COMPLETE_SERVER_SEND_SUB, i+1, "[ERROR] WORK File Info Send Http Server STEP", "Complete ??????, FileItem  ?????? ????????? ?????? ??? ????????????. [" + item.result.key + "]", item);
                    uploadProcessLog.error(step5SubMsg.getJsonLogMsg());
                }
                cnt += 1;
                UploadProcessLogDTO step5SubMsg = new UploadProcessLogDTO(UploadMode.KUS, UploadProcessStep.HTTP_COMPLETE_SERVER_SEND_SUB, i+1, "WORK File Info Send Http Server STEP","complete ?????? : " + findItem.getUploadFileKey(), findItem);
                uploadProcessLog.info(step5SubMsg.getJsonLogMsg());
                continue;
            }

            log.warn("warn code: " + item.error + ", warn message: " + item.message);
            UploadProcessLogDTO step5SubMsg = new UploadProcessLogDTO(UploadMode.KUS, UploadProcessStep.HTTP_COMPLETE_SERVER_SEND_SUB, i+1, "[ERROR] WORK File Info Send Http Server STEP", "complete ?????? ??????, warn code: " + item.error + ", warn message: " + item.message, item);
            uploadProcessLog.error(step5SubMsg.getJsonLogMsg());

            if (item.result == null) {
                continue;
            }

            FileItemDTO findItem = utils.findSendItem(fileList, item.result.key);

            if (findItem == null) {
                log.error("????????? ?????? ????????? ?????? ??? ????????????. [" + item.result.key + "]");
                UploadProcessLogDTO step5ErrMsg = new UploadProcessLogDTO(UploadMode.KUS, UploadProcessStep.HTTP_COMPLETE_SERVER_SEND_SUB, i+1, "[ERROR] WORK File Info Send Http Server STEP", "Complete ??????, FileItem  ?????? ????????? ?????? ??? ????????????. [" + item.result.key + "]", item);
                uploadProcessLog.error(step5ErrMsg.getJsonLogMsg());
                continue;
            }
            List<String> msgList = new ArrayList<String>();

            msgList.add("FAIL");
            msgList.add("Fail to API transfered ?????? ??????" + findItem.getPhysicalPath());
            msgList.add("Fail to API transfered ?????? ??????" + findItem.getPhysicalPath());

            String completePath = findItem.getPhysicalPath() + "_complete";
            if (findItem.isConsoleUpload()) {
                completePath = "";
            }

            log.warn("Complete Api is deleted to status is failed" + findItem.toString());
            UploadProcessLogDTO step5ErrMsg = new UploadProcessLogDTO(UploadMode.KUS, UploadProcessStep.HTTP_COMPLETE_SERVER_SEND_SUB, i+1, "[ERROR] WORK File Info Send Http Server STEP", "Complete Api is deleted to status is failed", findItem);
            uploadProcessLog.error(step5ErrMsg.getJsonLogMsg());
            utils.removeToIndividuaFile(findItem.getPhysicalPath(), findItem.getSnapshotPath(), completePath, msgList);

            findItem.setCompleteFail(true);
        }

        return cnt;
    }

    // http_upload ????????? ?????? ????????? ????????? ???
    private void readFiles(SendFileItemsDTO fileList, List<String> dirList) throws Exception {
        dirList = utils.searchDirs();

        List<File> storedFileList = new ArrayList<File>();

        // provider Key????????? ??? ???????????? ??????????????? ??????
        for (String dir : dirList) {
            List<String> dirFullPathList = utils.findToUploadFullDirPath(dir);

            // ????????? ???????????? ??????
            if (dirFullPathList.size() > 0) {
                for (String dirPath : dirFullPathList) {
                    storedFileList.add(new File(dirPath));
                }
            }
        }

        // ????????? ?????? ????????? ?????? ??????
        for (File f : storedFileList) {
            utils.getUploadFullFilePath(f.listFiles(), fileList);
        }
    }

    /*
     * ?????? ????????? ????????? ???????????? API??? ?????? ??? ?????? ?????? ?????? ???????????? ?????? ???????????? ????????? API????????? ???????????? ?????? ????????? ??????????????????
     * ????????? ??????
     */
    public void sendToHttpApi(SendFileItemsDTO items, String apiType) throws Exception {
        String responseBody = utils.sendPostToApi(items, apiType);
        log.info(responseBody);

        if (responseBody == null) {
            utils.removeFailedFiles(items);
            return;
        }

        Gson gson = BaseCommand.gson(false);
        KollusApiWatchersDTO apiResult = gson.fromJson(responseBody, KollusApiWatchersDTO.class);

        UploadProcessLogDTO step2Msg = new UploadProcessLogDTO(UploadMode.KUS, UploadProcessStep.HTTP_REGISTER_SERVER_SEND, "HTTP Register Server Send STEP", "sendItem size: " + apiResult.result.watcher_files.length, apiResult.result.watcher_files);
        uploadProcessLog.info(step2Msg.getJsonLogMsg());

        if (apiResult.error != 0) {
            UploadProcessLogDTO errorStep2Msg = new UploadProcessLogDTO(UploadMode.KUS, UploadProcessStep.HTTP_REGISTER_SERVER_SEND, "[ERROR] HTTP Register Server Send STEP", "server return != 200 or data error");
            uploadProcessLog.error(errorStep2Msg.getJsonLogMsg());

            utils.failApiResultOrRegisterProcess(apiResult, null);
            return;
        }



        for (int i = 0; i < apiResult.result.watcher_files.length; i++) {

            KollusApiWatcherContentDTO item = apiResult.result.watcher_files[i];
            FileItemDTO f = utils.convertResultApiItem(item);

            if (item.error == 0) {
                /**
                 * error == 0??? ????????? ????????? ??????
                 */
                items.update(f);
                UploadProcessLogDTO step2SubMsg = new UploadProcessLogDTO(UploadMode.KUS, UploadProcessStep.HTTP_REGISTER_SERVER_SEND_SUB, i+1, "HTTP Register Server Send STEP", "???????????? : " + f.getUploadFileKey(), f);
                uploadProcessLog.info(step2SubMsg.getJsonLogMsg());
                continue;
            }

            log.error(item.message);

            UploadProcessLogDTO step2ErrMsg = new UploadProcessLogDTO(UploadMode.KUS, UploadProcessStep.HTTP_REGISTER_SERVER_SEND_SUB, i+1, "[ERROR] HTTP Register Server Send STEP", "???????????? : " + f.getUploadFileKey(), f);
            uploadProcessLog.error(step2ErrMsg.getJsonLogMsg());
            utils.failApiResultOrRegisterProcess(null, item);
        }
    }
}

package net.catenoid.watcher.upload.utils;

import net.catenoid.watcher.LogAction;
import net.catenoid.watcher.config.Config;
import net.catenoid.watcher.upload.config.LineParser;
import net.catenoid.watcher.upload.config.LinuxLineParser;
import net.catenoid.watcher.upload.config.WindowsLineParser;
import net.catenoid.watcher.upload.dto.FileItemDTO;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

public class LSParser {

    private static Logger log = Logger.getLogger(LSParser.class);

    static boolean isWindows = false;

    private static final int BUFFER_SIZE = 2048;
    private String rootPath;
    private ArrayList<FileItemDTO> files;
    private ArrayList<FileItemDTO> dirs;
    private Config conf;
    private String jobName;
    private String lsFilePath;

    public LSParser(Config config, String job, String strRoot, ArrayList<FileItemDTO> dirArray,
                    ArrayList<FileItemDTO> filesArray) {
        this.rootPath = strRoot;
        this.conf = config;
        this.files = filesArray;
        this.dirs = dirArray;
        this.jobName = job;
    }

    class InputHandler extends Thread {

        private static final int BUFFER_SIZE = 1024;
        private InputStream input_;
        private int mLines = 0;

        InputHandler(InputStream input, String name) {
            super(name);
            input_ = input;
        }

        public void run() {

            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(input_), BUFFER_SIZE);
                String line;

                while ((line = reader.readLine()) != null) {
                    onNewline(line);
                    mLines++;

                    /**
                     * 혹시 MAX_VALUE 보다 크다면 0으로 초기화
                     */
                    if (mLines == Integer.MAX_VALUE)
                        mLines = 0;
                }

            } catch (IOException e) {
            } finally {
                if (reader != null)
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }

                stopCatter();
            }
        }

        private void stopCatter() {
            if (childProcess != null) {
                childProcess.destroy();
            }
        }

        private void onNewline(String line) {
            log.debug(LogAction.SH_STDOUT + line);
        }

    }

    private Process childProcess = null;

//	public boolean run2() throws Exception {
//
//		FileList.fileList(this.rootPath, files, this.rootPath);
//
//		if (log.isDebugEnabled()) {
//			for (FileItem item : files) {
//				log.debug(item.toString());
//			}
//		}
//		return true;
//	}

    /**
     * 썸네일 획득을 위해 FFMPEG 프로세스를 실행한다.
     *
     * @return
     * @throws Exception
     */
    public void run() throws Exception {
        String os = "";
        LineParser lineParser = null;
        try {
            if (lineParser == null) {
                os = System.getProperty("os.name");
                if (os != null && os.toLowerCase().startsWith("windows")) {
                    isWindows = true;
                    lineParser = new WindowsLineParser();
                } else {
                    lineParser = new LinuxLineParser();
                }
            }
        } catch (LinkageError e) {
        }

        String lsPath = conf.getLsDir();
        String charset = conf.getLsCharset();
        String snapDir = conf.getSnap().getSnapTempDir();
        long _time = System.currentTimeMillis();
        lsFilePath = String.format("%s/%s_%d.txt", snapDir, jobName, _time);


        String cmd = lineParser.getOption(lsPath, rootPath, lsFilePath);
//        String cmd = lineParser.getOption(lsPath, rootPath, lsFilePath);// +
        // " > "
        // +
        // lsFilePath;
        BufferedReader reader = null;
        try {
            childProcess = Runtime.getRuntime().exec(cmd);
            /*
             * InputHandler errorHandler = new InputHandler(childProcess.getErrorStream(),
             * "Error Stream"); errorHandler.start(); InputHandler inputHandler = new
             * InputHandler(childProcess.getInputStream(), "Output Stream");
             * inputHandler.start();
             */

            childProcess.waitFor();
            Thread.sleep(conf.getLsSleep());

            reader = new BufferedReader(new InputStreamReader(new FileInputStream(lsFilePath), charset), BUFFER_SIZE);

            String line;

            while ((line = reader.readLine()) != null) {
                lineParser.parser(rootPath, dirs, files, line);
            }
        } catch (Exception e) {
            log.error(e.toString());
            throw e;
        } finally {
            reader.close();
            destroy();
        }

        // if(log.isDebugEnabled() == false)
        {
            File tempFile = new File(lsFilePath);
            tempFile.delete();
        }
    }

    private void destroy() {
        if (childProcess != null) {
            childProcess.destroy();
        }
    }

}
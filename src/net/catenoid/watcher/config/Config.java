package net.catenoid.watcher.config;

import java.io.*;
import java.util.Iterator;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.kollus.json_data.BaseCommand;
import com.kollus.json_data.config.BaseConfig;
import com.kollus.json_data.config.KollusApi;
import com.kollus.utils.ConfigException;
import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;

public class Config extends BaseConfig {

    private static Logger log = Logger.getLogger(Config.class);

    public static final String WATCHER_PROPERTIES_FILE = "watcher.json";

    private static Config config;

    protected Config() {
    }

    @Expose
    @SerializedName("api")
    private KollusApi kollus_api;

    public KollusApi get_kollus_api() {
        return kollus_api;
    }

    @Expose
    @SerializedName("ffmpeg.dir")
    private String ffmpeg_dir;

    @Expose
    @SerializedName("ffmpeg.auto.rotation")
    private boolean ffmpegAutoRotation = true;

    /**
     * ffmpeg 2.7이상의 경우 auto rotation을 지원한다.
     * true인 경우 ffmpeg의 auto rotation을 이용한다.
     * false : ffmpeg이 자동으로 해주지 않는 경우
     * @return
     */
    public boolean isFfmpegAutoRotation() {
        return ffmpegAutoRotation;
    }

    @Expose
    @SerializedName("ls.dir")
    private String ls_dir;

    @Expose
    @SerializedName("ls.charset")
    private String ls_charset;

    @Expose
    @SerializedName("support.ext")
    private String _support_ext;

    private String[] support_ext;

    /**
     * 감시 리스트를 한번에 전송하는 최대 크기
     */
    @Expose
    @SerializedName("watcher.send.max")
    private int watcher_send_max;

    @Expose
    @SerializedName("watchers")
    private WatcherFolder[] watchers;

    @Expose
    @SerializedName("snap")
    private Thumbnail snap;

    @Expose
    @SerializedName("start.with.run")
    private boolean start_with_run = false;

    @Expose
    @SerializedName("ignore.filename")
    private String[] ignore_filename;

    @Expose
    @SerializedName("ls.sleep")
    private long ls_sleep;

    @Expose
    @SerializedName("httpserver")
    private HttpServerConf httpd;

    /**
     * watcher.json 설정의 main_node_key
     */
    @Deprecated
    @Expose
    @SerializedName("main_node_key")
    private String main_node_key;

    @Expose
    @SerializedName("default_charset")
    private String default_charset = "euckr";

    @Expose
    @SerializedName("convmv_path")
    private String convmv_path = "/usr/bin/convmv";

    /**
     * rsync 최대 속도 지정 옵션
     */
    @Expose
    @SerializedName("rsync.bwlimit")
    private String rsync_bwlimit = null;

    @Expose
    @SerializedName("rsync.use")
    private boolean use_rsync = false;

    public WatcherFolder[] getWatchers() {
        return watchers;
    }

    public String get_ffmpeg_dir() {
        return ffmpeg_dir;
    }

    /**
     * 추가지원 확장자
     *
     * @return
     */
    public String[] getSupportExt() {
        return support_ext;
    }


    public boolean isStartWithRun() {
        return start_with_run;
    }

    public Thumbnail getSnap() {
        return snap;
    }

    public static Config getConfig() {
        if (config != null)
            return config;

        try {
            config = Config.readProperty();
        } catch (JsonSyntaxException e) {
            log.error(e.toString());
            //log.error(Utils.getStackTrace(e));
        } catch (ClientProtocolException e) {
            log.error(e.toString());
            //log.error(Utils.getStackTrace(e));
        } catch (ConfigException e) {
            log.error(e.toString());
            //log.error(Utils.getStackTrace(e));
        } catch (IOException e) {
            log.error(e.toString());
            //log.error(Utils.getStackTrace(e));
        }

        return config;
    }

    private static Config readProperty() throws ConfigException, JsonSyntaxException, ClientProtocolException, IOException {

        String path = Config.get_config_file_path();

        Reader reader;
        try {
            reader = new FileReader(path);
        } catch (FileNotFoundException e) {
            throw new ConfigException(e);
        }
        Gson gson = BaseCommand.gson(false);

        final Config cfg = gson.fromJson(reader, Config.class);
        cfg._init();

        return cfg;
    }

    private static String get_config_file_path() {
        String user_dir = System.getProperty("user.dir");
        String propFileName = WATCHER_PROPERTIES_FILE;

        String[] paths = {
                user_dir + "/" + propFileName,
                user_dir + "/bin/" + WATCHER_PROPERTIES_FILE,
                user_dir + "/conf/" + WATCHER_PROPERTIES_FILE
        };

        for(String path : paths) {
            File propFile = new File(path);
            if (propFile.exists()) {
                return path;
            }
        }
        return null;
    }

    /**
     * 초기화 함수 모음
     * @throws IOException
     * @throws ClientProtocolException
     * @throws JsonSyntaxException
     */
    private void _init() throws JsonSyntaxException, ClientProtocolException, IOException {
        setSupportExt(_support_ext);
        this.httpd._init();

        printUrls(this.kollus_api.get_module_config().watcher);
        printUrls(this.kollus_api.get_module_config().transcoder);
    }

    /**
     * 추가지원 확장자
     *
     * @param support_ext
     */
    private void setSupportExt(String support_ext) {
        String delimiter = ";";
        this.support_ext = support_ext.split(delimiter);
    }

    private void printUrls(Map<String, String> map) {
        Iterator<String> keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String name = keys.next();
            String value = map.get(name);
            log.info(String.format("URLS [%s] %s", name, value));
        }
    }

}

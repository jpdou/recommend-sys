package top.jpdou.recommend.api;

public interface EntityCollector {

    final static public String CONFIG_PATH_FETCH_BASE_URL = "entity_collector/url/base_url";
    final static public String CONFIG_PATH_FETCH_API_KEY = "entity_collector/api/secret_key";



    final static public int REQUEST_SOCKET_TIMEOUT = 3000;
    final static public int REQUEST_CONNECT_TIMEOUT = 3000;

    public void fetch();
}

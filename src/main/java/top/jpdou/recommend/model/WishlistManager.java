package top.jpdou.recommend.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.jpdou.recommend.api.EntityCollector;
import top.jpdou.recommend.api.ProductRepository;
import top.jpdou.recommend.model.entity.Product;

import java.util.Optional;

@Component
public class WishlistManager implements EntityCollector {

    final static private String CONFIG_PATH_FETCH_WISHLIST_URL = "entity_collector/url/wishlist";
    final static private String CONFIG_PATH_LAST_WISHLIST_ID = "entity_collector/wishlist/last_wishlist_id";

    @Autowired
    private ScopeConfigManager scopeConfigManger;

    @Autowired
    private ProductRepository productRepository;

    private int lastWishlistId;

    public WishlistManager()
    {
        lastWishlistId = scopeConfigManger.getValueAsInteger(CONFIG_PATH_LAST_WISHLIST_ID);
    }

    public void fetch() {

        String baseUrl = scopeConfigManger.getValue(CONFIG_PATH_FETCH_BASE_URL);
        String url = baseUrl + scopeConfigManger.getValue(CONFIG_PATH_FETCH_WISHLIST_URL).replace("{lastWishlistId}", String.valueOf(lastWishlistId));

        System.out.println("Url: " + url);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(REQUEST_SOCKET_TIMEOUT)
                .setConnectTimeout(REQUEST_CONNECT_TIMEOUT)
                .build();

        HttpGet httpget = new HttpGet(url);
        httpget.setConfig(requestConfig);
        httpget.setHeader("Authorization", "Bearer " + scopeConfigManger.getValue(CONFIG_PATH_FETCH_API_KEY));
        httpget.setHeader("Content-Type", "application/json");

        try {
            CloseableHttpResponse response = httpclient.execute(httpget);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();


                String responseText = EntityUtils.toString(entity);
                System.out.println(responseText);

                JsonParser parser = new JsonParser();
                JsonObject wishlistData = (JsonObject) parser.parse(responseText);

                response.close();
                httpclient.close();

            } else {
                System.out.println("Fetch quote failed, response status is " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Fetch quote failed, error message: " + e.getMessage());
            e.printStackTrace();
        }

        scopeConfigManger.setValue(CONFIG_PATH_LAST_WISHLIST_ID, String.valueOf(lastWishlistId));
        lastWishlistId ++;
    }

    private int getProduct(String sku)
    {
        Optional result = productRepository.findById(sku);
        if (result.isPresent()) {
            Product product = (Product) result.get();
            return product.getId();
        }
        return 0;
    }
}

package top.jpdou.recommend.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
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
import top.jpdou.recommend.api.ProductRepository;
import top.jpdou.recommend.model.entity.Product;
import top.jpdou.recommend.model.entity.Quote;
import top.jpdou.recommend.model.entity.QuoteItem;

import java.util.HashMap;

import static top.jpdou.recommend.api.EntityCollector.*;

@Component
public class ProductManager {

    private static final String CONFIG_PATH_FETCH_PRODUCT_URL = "entity_collector/url/product";

    @Autowired
    private ProductRepository repository;

    @Autowired
    private ScopeConfigManager scopeConfigManger;

    private HashMap<String, Integer> products;

    public ProductManager()
    {
        products = new HashMap<>();
    }

    public int getIdBySku(String sku)
    {
        if (!products.containsKey(sku)) {
            Product product = fetch(sku);
            if (product.getId() != null) {
                products.put(sku, product.getId());
            } else {
                products.put(sku, 0);
            }
        }
        return products.get(sku);
    }

    private Product fetch(String sku)
    {
        String baseUrl = scopeConfigManger.getValue(CONFIG_PATH_FETCH_BASE_URL);
        String url = baseUrl + scopeConfigManger.getValue(CONFIG_PATH_FETCH_PRODUCT_URL).replace("{sku}", sku);

        Product product = new Product();

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
                JsonObject productData = (JsonObject) parser.parse(responseText);

                int productId = productData.get("id").getAsInt();


                product.setId(productId);
                product.setSku(sku);

                repository.save(product);

                response.close();
                httpclient.close();
            } else {
                System.out.println("Fetch quote failed, response status is " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Fetch quote failed, error message: " + e.getMessage());
            e.printStackTrace();
        }
        return product;
    }
}

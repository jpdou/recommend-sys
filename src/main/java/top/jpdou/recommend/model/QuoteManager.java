package top.jpdou.recommend.model;

import com.google.gson.JsonElement;
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
import top.jpdou.recommend.api.QuoteItemRepository;
import top.jpdou.recommend.api.QuoteRepository;
import top.jpdou.recommend.model.entity.*;

import java.util.Optional;

@Component
public class QuoteManager implements EntityCollector {

    final static private String CONFIG_PATH_FETCH_QUOTE_URL = "entity_collector/url/quote";
    final static private String CONFIG_PATH_LAST_QUOTE_ID = "entity_collector/quote/last_quote_id";

    @Autowired
    private QuoteRepository quoteRepository;

    @Autowired
    private QuoteItemRepository quoteItemRepository;

    @Autowired
    private ScopeConfigManager scopeConfigManger;

    @Autowired
    private ProductRepository productRepository;

    private int lastQuoteId;

    public QuoteManager()
    {
        lastQuoteId = scopeConfigManger.getValueAsInteger(CONFIG_PATH_LAST_QUOTE_ID);
    }

    public void fetch() {

        String baseUrl = scopeConfigManger.getValue(CONFIG_PATH_FETCH_BASE_URL);
        String url = baseUrl + scopeConfigManger.getValue(CONFIG_PATH_FETCH_QUOTE_URL).replace("{lastQuoteId}", String.valueOf(lastQuoteId));

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
                JsonObject quoteData = (JsonObject) parser.parse(responseText);

                if (!quoteData.get("is_virtual").getAsBoolean()) { // 只记录非虚拟订单

                    JsonObject customer = (JsonObject) quoteData.get("customer");
                    int customerId = 0;
                    if (customer.get("id")!= null) {
                        customerId = customer.get("id").getAsInt();
                    }
                    String customerEmail = customer.get("email").getAsString();

                    Quote quote = new Quote();
                    quote.setId(lastQuoteId);
                    quote.setCustomerEmail(customerEmail);
                    quote.setCustomerId(customerId);
                    quote.setVirtual(false);

                    quoteRepository.save(quote);

                    for (JsonElement item : quoteData.get("items").getAsJsonArray()) {
                        JsonObject _item = (JsonObject) item;
                        if (!_item.get("is_virtual").getAsBoolean()) {

                            int itemId = _item.get("item_id").getAsInt();
                            String sku = _item.get("ku").getAsString();
                            int qty = _item.get("qty").getAsInt();

                            int productId = this.getProduct(sku);

                            QuoteItem quoteItem = new QuoteItem();
                            quoteItem.setId(itemId);
                            quoteItem.setProductId(productId);
                            quoteItem.setQty(qty);
                            quoteItem.setParentId(lastQuoteId);
                            quoteItem.setVirtual(false);

                            quoteItemRepository.save(quoteItem);
                        }
                    }
                }

                response.close();
                httpclient.close();
            } else {
                System.out.println("Fetch quote failed, response status is " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Fetch quote failed, error message: " + e.getMessage());
            e.printStackTrace();
        }
        scopeConfigManger.setValue(CONFIG_PATH_LAST_QUOTE_ID, String.valueOf(lastQuoteId));
        lastQuoteId ++;
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

package top.jpdou.recommend.model;

import com.google.gson.JsonArray;
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
import top.jpdou.recommend.model.entity.OrderItem;
import top.jpdou.recommend.model.entity.SalesOrder;

@Component
public class SalesOrderManager implements EntityCollector {

    final static public String CONFIG_PATH_FETCH_ORDER_URL = "entity_collector/url/order";

    @Autowired
    private SalesOrderRepository orderRepository;

    @Autowired
    private SalesOrder order;

    @Autowired
    private OrderItem orderItem;

    @Autowired
    private ScopeConfigManager scopeConfigManger;

    private int orderId = 1070;

    public boolean fetch() {
        String baseUrl = scopeConfigManger.getValue(CONFIG_PATH_FETCH_BASE_URL);
        String url = baseUrl + scopeConfigManger.getValue(CONFIG_PATH_FETCH_ORDER_URL).replace("{id}", String.valueOf(orderId));

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

                JsonParser parse = new JsonParser();
                JsonObject orderData = (JsonObject) parse.parse(responseText);

                if (!orderData.get("is_virtual").getAsBoolean()) { // 只记录非虚拟订单

                    int originId = orderData.get("entity_id").getAsInt();
                    String orderCurrencyCode = orderData.get("order_currency_code").getAsString();
                    String customerEmail = orderData.get("customer_email").getAsString();
                    int customerId = orderData.get("customer_id").getAsInt();
                    String status = orderData.get("status").getAsString();

                    order.setOriginId(originId);
                    order.setOrderCurrencyCode(orderCurrencyCode);
                    order.setCustomerEmail(customerEmail);
                    order.setCustomerId(customerId);
                    order.setStatus(status);
                    order.setVirtual(false);

                    orderRepository.save(order);

                    System.out.println("Order record id: " + order.getId());

                    for (JsonElement item : orderData.get("items").getAsJsonArray()) {
                        int productId = ((JsonObject) item).get("product_id").getAsInt();
                        int qty = ((JsonObject) item).get("qty_ordered").getAsInt();
                        orderItem.setProductId(productId);
                        orderItem.setQty(qty);
                    }
                }

                response.close();
                httpclient.close();
                orderId --;
                return true;
            } else {
                System.out.println("Fetch order failed, response status is " + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            System.out.println("Fetch order failed, error message: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}

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
    private OrderItemRepository orderItemRepository;

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

                    String customerEmail = orderData.get("customer_email").getAsString();
                    int customerId = 0;
                    if (orderData.get("customer_id") != null) {
                        customerId = orderData.get("customer_id").getAsInt();
                    }
                    String status = orderData.get("status").getAsString();

                    SalesOrder order = new SalesOrder();
                    order.setId(orderId);
                    order.setCustomerEmail(customerEmail);
                    order.setCustomerId(customerId);
                    order.setStatus(status);
                    order.setVirtual(false);

                    orderRepository.save(order);

                    for (JsonElement item : orderData.get("items").getAsJsonArray()) {

                        if (!((JsonObject) item).get("is_virtual").getAsBoolean()) {

                            int itemId = ((JsonObject) item).get("item_id").getAsInt();
                            int productId = ((JsonObject) item).get("product_id").getAsInt();
                            int qty = ((JsonObject) item).get("qty_ordered").getAsInt();

                            OrderItem orderItem = new OrderItem();
                            orderItem.setId(itemId);
                            orderItem.setProductId(productId);
                            orderItem.setQty(qty);
                            orderItem.setParentId(orderId);
                            orderItem.setVirtual(false);

                            orderItemRepository.save(orderItem);
                        }
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

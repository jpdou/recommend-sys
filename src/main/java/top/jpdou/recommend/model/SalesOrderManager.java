package top.jpdou.recommend.model;

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

@Component
public class SalesOrderManager implements EntityCollector {

    final static public String CONFIG_PATH_FETCH_ORDER_URL = "entity_collector/url/order";

    @Autowired
    SalesOrderRepository orderRepository;

    @Autowired
    private ScopeConfigManager scopeConfigManger;

    public boolean fetch() {
        String baseUrl = scopeConfigManger.getValue(CONFIG_PATH_FETCH_BASE_URL);
        String url = baseUrl + scopeConfigManger.getValue(CONFIG_PATH_FETCH_ORDER_URL);

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

                response.close();
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

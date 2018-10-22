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
import top.jpdou.recommend.api.CustomerRepository;
import top.jpdou.recommend.api.ProductRepository;
import top.jpdou.recommend.model.entity.Customer;
import top.jpdou.recommend.model.entity.Product;

import java.util.HashMap;
import java.util.Optional;

import static top.jpdou.recommend.api.EntityCollector.*;

@Component
public class CustomerManager {

    private static final String CONFIG_PATH_FETCH_PRODUCT_URL = "entity_collector/url/customer";

    @Autowired
    private CustomerRepository repository;

    @Autowired
    private ScopeConfigManager scopeConfigManger;

    private HashMap<Integer, Customer> customers;

    public CustomerManager()
    {
        customers = new HashMap<>();
    }

    public int getIdByEmail(String email)
    {
        // todo 通过 Magento2 api 获取 customer 信息
        return 0;
    }

    public boolean isExisted(int customerId)
    {
        if (!customers.containsKey(customerId)) {
            Optional result = repository.findById(customerId);
            if (result.isPresent()) {
                Customer customer = (Customer) result.get();
                customers.put(customer.getId(), customer);
            }
        }
        return customers.containsKey(customerId);
    }

    public void create(int customerId)
    {
        if (isExisted(customerId)) {
            return;
        }
        Customer customer = new Customer();
        customer.setId(customerId);
        repository.save(customer);
        customers.put(customerId, customer);
    }
}

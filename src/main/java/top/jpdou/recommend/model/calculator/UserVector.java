package top.jpdou.recommend.model.calculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.jpdou.recommend.api.OrderItemRepository;
import top.jpdou.recommend.model.*;
import top.jpdou.recommend.model.entity.Customer;
import top.jpdou.recommend.model.entity.OrderItem;
import top.jpdou.recommend.model.entity.SalesOrder;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class UserVector {

    private final String CONFIG_PATH_ORDER_ITEM_WEIGHT = "calculator_config/order_item/weight";

    @Autowired
    ScopeConfigManager scopeConfigManager;

    @Autowired
    CustomerManager customerManager;

    @Autowired
    ProductManager productManager;

    @Autowired
    SalesOrderManager orderManager;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    QuoteManager quoteManager;

    @Autowired
    WishlistManager wishlistManager;

    @Autowired
    ViewedHistoryManager viewedHistoryManager;

    private Customer customer;

    private HashMap<Integer, Integer> interestedProducts;   // interestedProducts<productId, weight>

    public UserVector(Customer customer)
    {
        this.customer = customer;
    }



    public void calc()
    {
        addOrderItemProducts();
    }

    private void addInterestedProduct(int productId, int weight)
    {
        if (productId == 0) {
            return;
        }
        if (interestedProducts.containsKey(productId)) {
            weight += interestedProducts.get(productId);
        }
        interestedProducts.put(productId, weight);
    }

    private void addOrderItemProducts()
    {
        ArrayList<SalesOrder> orders = orderManager.getOrdersByCustomerId(customer.getId());
        for (SalesOrder order : orders) {
            Iterable<OrderItem> orderItems = orderItemRepository.findByParentId(order.getId());
            for (OrderItem orderItem : orderItems) {
                int productId = orderItem.getProductId();
                int qty = orderItem.getQty();
                int orderItemWeight = Integer.parseInt(scopeConfigManager.getValue(CONFIG_PATH_ORDER_ITEM_WEIGHT));
                addInterestedProduct(productId, qty * orderItemWeight);
            }
        }
    }
}

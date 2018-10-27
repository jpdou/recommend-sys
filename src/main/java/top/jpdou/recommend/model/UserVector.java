package top.jpdou.recommend.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.jpdou.recommend.api.OrderItemRepository;
import top.jpdou.recommend.api.QuoteItemRepository;
import top.jpdou.recommend.api.ViewedHistoryRepository;
import top.jpdou.recommend.api.WishlistRepository;
import top.jpdou.recommend.model.*;
import top.jpdou.recommend.model.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserVector {

    private final static String CONFIG_PATH_ORDER_ITEM_WEIGHT = "calculator_config/order_item/weight";
    private final static String CONFIG_PATH_QUOTE_ITEM_WEIGHT = "calculator_config/quote_item/weight";
    private final static String CONFIG_PATH_WISHLIST_ITEM_WEIGHT = "calculator_config/wishlist_item/weight";
    private final static String CONFIG_PATH_VIEWED_HISTORY_ITEM_WEIGHT = "calculator_config/viewed_history_item/weight";

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
    QuoteItemRepository quoteItemRepository;

    @Autowired
    WishlistManager wishlistManager;

    @Autowired
    WishlistRepository wishlistRepository;

    @Autowired
    ViewedHistoryRepository viewedHistoryRepository;

    private Customer customer;

    private HashMap<Integer, HashMap<Integer, Integer>> customerProductIdsNumPool;
    private HashMap<Integer, Integer> productIdsNum;   // productIdsNum<productId, number>
    private HashMap<Integer, Integer> occurredNums; // Dw<productId, occurredNumInAll>
    private HashMap<Integer, Double> IDF_poll;

    public void calc()
    {
        customerProductIdsNumPool = new HashMap<>();

        ArrayList<Customer> allCustomer = customerManager.getAllCustomer();
        for (Customer customer : allCustomer) {

            productIdsNum = new HashMap<>();

            this.customer = customer;

            addOrderItemProducts();

            addQuoteItemProducts();

            addWishlistProducts();

            addViewedProducts();

            // todo 持久化 productIdsNum

            customerProductIdsNumPool.put(this.customer.getId(), productIdsNum);


            // 统计每个 productId 在多少个 customer productIdsNum 出现过
            for (Map.Entry<Integer, Integer> entry : productIdsNum.entrySet()) {
                int count = 1;
                if (occurredNums.containsKey(entry.getKey())) {
                    count += occurredNums.get(entry.getKey());
                }
                occurredNums.put(entry.getKey(), count);
            }
        }

        for (Map.Entry<Integer, HashMap<Integer, Integer>> customerProductIdsNumEntry: customerProductIdsNumPool.entrySet()) {
            int customerId = customerProductIdsNumEntry.getKey();
            HashMap<Integer, Integer> _productIdsNum = customerProductIdsNumEntry.getValue();

            double numSum = 0;
            for (Map.Entry<Integer, Integer> productIdNumEntry : _productIdsNum.entrySet()) {
                numSum += productIdNumEntry.getValue();
            }

            // 计算每个 customer 的所有 productId 的单文本词频 （TF）
            HashMap<Integer, Double> TF_Map = new HashMap<>();
            for (Map.Entry<Integer, Integer> productIdNumEntry : _productIdsNum.entrySet()) {
                TF_Map.put(productIdNumEntry.getKey(), productIdNumEntry.getValue() / numSum);
            }

            // 计算每个 customer 的所有 productId 的 TF_IDF = TF * IDF
            HashMap<Integer, Double> TF_IDF_Map = new HashMap<>();
            for (Map.Entry<Integer, Integer> productIdNumEntry : _productIdsNum.entrySet()) {
                double IDF = this.calcProductIDF(productIdNumEntry.getKey());
                double TF = TF_Map.get(productIdNumEntry.getKey());
                TF_IDF_Map.put(productIdNumEntry.getKey(), TF * IDF);
            }
        }


    }

    private void addInterestedProduct(int productId, int number)
    {
        if (productId == 0) {
            return;
        }
        if (productIdsNum.containsKey(productId)) {
            number += productIdsNum.get(productId);
        }
        productIdsNum.put(productId, number);
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

    private void addQuoteItemProducts()
    {
        ArrayList<Quote> quotes = quoteManager.getQuotesByCustomerId(customer.getId());
        for (Quote quote : quotes) {
            Iterable<QuoteItem> quoteItems = quoteItemRepository.findByParentId(quote.getId());
            for (QuoteItem quoteItem : quoteItems) {
                int productId = quoteItem.getProductId();
                int qty = quoteItem.getQty();
                int quoteItemWeight = Integer.parseInt(scopeConfigManager.getValue(CONFIG_PATH_QUOTE_ITEM_WEIGHT));
                addInterestedProduct(productId, qty * quoteItemWeight);
            }
        }
    }

    private void addWishlistProducts()
    {
        Iterable<Wishlist> wishlist = wishlistRepository.findByCustomerId(customer.getId());
        for (Wishlist wishlistItem : wishlist) {
            int productId = wishlistItem.getProductId();
            int qty = 1;
            int wishlistItemWeight = Integer.parseInt(scopeConfigManager.getValue(CONFIG_PATH_WISHLIST_ITEM_WEIGHT));
            addInterestedProduct(productId, qty * wishlistItemWeight);
        }
    }

    private void addViewedProducts()
    {
        Iterable<ViewedHistory> viewedHistories = viewedHistoryRepository.findByCustomerId(customer.getId());
        for (ViewedHistory history : viewedHistories) {
            int productId = history.getProductId();
            int viewedCount = history.getViewedCount();
            int viewedHistoryWeight = Integer.parseInt(scopeConfigManager.getValue(CONFIG_PATH_VIEWED_HISTORY_ITEM_WEIGHT));
            addInterestedProduct(productId, viewedCount * viewedHistoryWeight);
        }
    }

    /**
     * 计算 productId 的 Inverse Document Frequency （IDF）
     * @param productId
     * @return
     */
    private double calcProductIDF(int productId)
    {
        int occurredNum = occurredNums.get(productId);

        if (occurredNum == 0) {
            return 10;
        }

        return Math.log(customerManager.getNumOfAll() / occurredNum);
    }
}

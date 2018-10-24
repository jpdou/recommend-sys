package top.jpdou.recommend.model.calculator;

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

@Component
public class UserVector {

    private final String CONFIG_PATH_ORDER_ITEM_WEIGHT = "calculator_config/order_item/weight";
    private final String CONFIG_PATH_QUOTE_ITEM_WEIGHT = "calculator_config/quote_item/weight";
    private final String CONFIG_PATH_WISHLIST_ITEM_WEIGHT = "calculator_config/wishlist_item/weight";
    private final String CONFIG_PATH_VIEWED_HISTORY_ITEM_WEIGHT = "calculator_config/viewed_history_item/weight";

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

    private HashMap<Integer, Integer> interestedProducts;   // interestedProducts<productId, weight>

    public UserVector(Customer customer)
    {
        this.customer = customer;
    }



    public void calc()
    {
        addOrderItemProducts();

        addQuoteItemProducts();

        addWishlistProducts();

        addViewedProducts();


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
}

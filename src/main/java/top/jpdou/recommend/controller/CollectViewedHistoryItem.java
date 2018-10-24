package top.jpdou.recommend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.jpdou.recommend.api.ViewedHistoryRepository;
import top.jpdou.recommend.model.entity.ViewedHistory;

import java.util.Optional;

@RestController
public class CollectViewedHistoryItem {

    @Autowired
    private ViewedHistoryRepository viewedHistoryRepository;

    @RequestMapping("/collect")
    public void collect(
            @RequestParam(value="customer") String customer,
            @RequestParam(value="product") String product
    ) {
        try {
            int customerId = Integer.parseInt(customer);
            int productId = Integer.parseInt(product);

            Optional result = viewedHistoryRepository.findByCustomerIdAndProductId(customerId, productId);
            ViewedHistory history;
            if (!result.isPresent()) {
                history = new ViewedHistory();
                history.setCustomerId(customerId);
                history.setProductId(productId);
                history.setViewedCount(1);
            } else {
                history = (ViewedHistory) result.get();
                history.increaseViewedCount();
            }

            viewedHistoryRepository.save(history);

            System.out.println("success");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

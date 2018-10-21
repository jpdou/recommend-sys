package top.jpdou.recommend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.jpdou.recommend.api.ViewedHistoryItemRepository;
import top.jpdou.recommend.api.ViewedHistoryRepository;
import top.jpdou.recommend.model.entity.ViewedHistory;
import top.jpdou.recommend.model.entity.ViewedHistoryItem;

@RestController
public class CollectViewedHistoryItem {

    @Autowired
    private ViewedHistoryRepository viewedHistoryRepository;

    @Autowired
    private ViewedHistoryItemRepository viewedHistoryItemRepository;

    @RequestMapping("/collect")
    public void collect(
            @RequestParam(value="customer") String customer,
            @RequestParam(value="product") String product
    ) {
        try {
            int customerId = Integer.parseInt(customer);
            int productId = Integer.parseInt(product);

            ViewedHistory history = viewedHistoryRepository.findByCustomerId(customerId);

            if (history == null) {
                history = new ViewedHistory();
                history.setCustomerId(customerId);
                viewedHistoryRepository.save(history);
            }

            ViewedHistoryItem item = viewedHistoryItemRepository.findByParentIdAndProductId(history.getId(), productId);
            if (item != null) {
                item.increaseViewCount();
            } else {
                item = new ViewedHistoryItem();
                item.setParentId(history.getId());
                item.setProductId(productId);
                item.setViewCount(1);
            }
            viewedHistoryItemRepository.save(item);

            System.out.println("success");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

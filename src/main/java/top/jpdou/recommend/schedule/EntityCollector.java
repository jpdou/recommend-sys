package top.jpdou.recommend.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.jpdou.recommend.model.QuoteManager;
import top.jpdou.recommend.model.SalesOrderManager;
import top.jpdou.recommend.model.ScopeConfigManager;
import top.jpdou.recommend.model.WishlistManager;

@Component
public class EntityCollector {

    @Autowired
    private ScopeConfigManager scopeConfigManager;

    @Autowired
    private SalesOrderManager orderManager;
    @Autowired
    private QuoteManager quoteManager;

    @Autowired
    private WishlistManager wishlistManager;

    @Scheduled(fixedDelay = 1000)
    public void collect()
    {
        orderManager.fetch();

        quoteManager.fetch();

        //wishlistManager.fetch();
    }
}

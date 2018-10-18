package top.jpdou.recommend.schedule;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.jpdou.recommend.model.SalesOrderManager;
import top.jpdou.recommend.model.ScopeConfigManager;

@Component
public class EntityCollector {

    @Autowired
    private ScopeConfigManager scopeConfigManager;

    @Autowired
    private SalesOrderManager orderManager;

    private static final Logger log = LoggerFactory.getLogger(EntityCollector.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedDelay = 5000)
    public void collect()
    {
        orderManager.fetch();
    }
}

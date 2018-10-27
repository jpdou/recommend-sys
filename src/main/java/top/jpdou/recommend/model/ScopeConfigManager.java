package top.jpdou.recommend.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.jpdou.recommend.api.ScopeConfigRepository;
import top.jpdou.recommend.model.entity.ScopeConfig;

import java.util.*;

@Component
public class ScopeConfigManager {
    @Autowired
    private ScopeConfigRepository scopeConfigRepository;

    private HashMap<String, String> values;
    private ArrayList<ScopeConfig> waitingSaveScopeConfigEntities;

    public ScopeConfigManager()
    {
        values = new HashMap<>();
        waitingSaveScopeConfigEntities = new ArrayList<>();
    }

    public String getValue(String path)
    {
        if (!values.containsKey(path)) {

            Optional result = scopeConfigRepository.findById(path);

            if (result.isPresent()) {
                ScopeConfig scopeConfig = (ScopeConfig) result.get();
                String value = scopeConfig.getValue();
                values.put(path, value);
            } else {
                values.put(path, "");
            }
        }
        return values.get(path);
    }

    public String getValue(String path, String defaultVal)
    {
        if (!values.containsKey(path)) {

            Optional result = scopeConfigRepository.findById(path);

            if (result.isPresent()) {
                ScopeConfig scopeConfig = (ScopeConfig) result.get();
                String value = scopeConfig.getValue();
                values.put(path, value);
            } else {
                values.put(path, defaultVal);
            }
        }
        return values.get(path);
    }

    public void setValue(String path, String value)
    {
        values.put(path, value);

        ScopeConfig scopeConfig = new ScopeConfig();
        scopeConfig.setValue(value);
        scopeConfig.setPath(path);
        waitingSaveScopeConfigEntities.add(scopeConfig);
    }

    @Scheduled(fixedDelay = 15000)
    public void backgroundPersistence()
    {
        for (ScopeConfig scopeConfig : waitingSaveScopeConfigEntities) {
            scopeConfigRepository.save(scopeConfig);
            waitingSaveScopeConfigEntities.remove(scopeConfig);
        }
    }
}

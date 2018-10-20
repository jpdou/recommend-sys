package top.jpdou.recommend.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.jpdou.recommend.api.ScopeConfigRepository;

import java.util.*;

@Component
public class ScopeConfigManager {
    @Autowired
    private ScopeConfigRepository scopeConfigRepository;

    private HashMap<String, String> values;

    public ScopeConfigManager()
    {
        values = new HashMap<>();
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

            System.out.println(result);

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

        ScopeConfig config = new ScopeConfig();
        config.setValue(value);
        config.setPath(path);
        scopeConfigRepository.save(config);
    }
}

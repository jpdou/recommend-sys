package top.jpdou.recommend.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public Integer getValueAsInteger(String path)
    {
        return Integer.parseInt(getValue(path));
    }

    public Boolean getValueAsBoolean(String path)
    {
        return Boolean.parseBoolean(getValue(path));
    }

    public float getValueAsFloat(String path)
    {
        return Float.parseFloat(getValue(path));
    }

    public String getValue(String path)
    {
        if (!values.containsKey(path)) {

            Optional result = scopeConfigRepository.findById(path);

            if (result.isPresent()) {
                ScopeConfig scopeConfig = (ScopeConfig) result.get();
                String value = scopeConfig.getValue();
                values.put(path, value);
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

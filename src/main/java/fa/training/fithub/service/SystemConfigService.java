package fa.training.fithub.service;

import java.util.Optional;

public interface SystemConfigService {
    Optional<String> getValue(String key);
    String getString(String key, String defaultValue);
    Integer getInteger(String key, int defaultValue);
    Double getDouble(String key, double defaultValue);
    Boolean getBoolean(String key, boolean defaultValue);

}

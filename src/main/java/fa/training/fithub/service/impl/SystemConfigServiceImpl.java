package fa.training.fithub.service.impl;

import fa.training.fithub.repository.SystemConfigRepository;
import fa.training.fithub.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private SystemConfigRepository systemConfigRepository;

    @Override
    public Optional<String> getValue(String key) {
        return systemConfigRepository.findByKey(key)
                        .map(config -> config.getConfigValue());
    }

    @Override
    public String getString(String key, String defaultValue) {
        return getValue(key).orElse(defaultValue);
    }

    @Override
    public Integer getInteger(String key, int defaultValue) {
        try{
            return getValue(key)
                    .map(Integer::parseInt)
                    .orElse(defaultValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public Double getDouble(String key, double defaultValue) {
        try{
            return getValue(key)
                    .map(Double::parseDouble)
                    .orElse(defaultValue);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public Boolean getBoolean(String key, boolean defaultValue) {
        return getValue(key)
                .map(Boolean::parseBoolean)
                .orElse(defaultValue);
    }
}

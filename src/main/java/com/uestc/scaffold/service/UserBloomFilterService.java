package com.uestc.scaffold.service;

import com.uestc.scaffold.enums.UserFieldType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.Map;

/**
 * 用户布隆过滤器服务
 * 用于快速判断用户字段（用户名、邮箱、手机号）是否已存在
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserBloomFilterService {

    private final RedissonClient redissonClient;

    // 使用 EnumMap 统一管理所有用户字段的布隆过滤器
    private final Map<UserFieldType, RBloomFilter<String>> bloomFilters = new EnumMap<>(UserFieldType.class);

    // 布隆过滤器配置
    private static final long EXPECTED_INSERTIONS = 1000000L; // 预期插入100万数据
    private static final double FALSE_POSITIVE_PROBABILITY = 0.01; // 误判率1%

    /**
     * 初始化所有用户字段的布隆过滤器
     */
    @PostConstruct
    public void init() {
        for (UserFieldType fieldType : UserFieldType.values()) {
            RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(fieldType.getBloomFilterKey());
            if (!bloomFilter.isExists()) {
                bloomFilter.tryInit(EXPECTED_INSERTIONS, FALSE_POSITIVE_PROBABILITY);
                log.info("用户字段 {} 布隆过滤器初始化完成: {}", fieldType.name(), fieldType.getBloomFilterKey());
            }
            bloomFilters.put(fieldType, bloomFilter);
        }
    }

    /**
     * 检查用户指定字段的值是否可能存在
     *
     * @param fieldType 字段类型
     * @param value     字段值
     * @return true-可能存在（需进一步查库确认），false-一定不存在
     */
    public boolean mightContain(UserFieldType fieldType, String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        return bloomFilters.get(fieldType).contains(value);
    }

    /**
     * 添加用户字段值到布隆过滤器
     *
     * @param fieldType 字段类型
     * @param value     字段值
     */
    public void add(UserFieldType fieldType, String value) {
        if (value != null && !value.isEmpty()) {
            bloomFilters.get(fieldType).add(value);
            log.debug("用户字段 {} 的值 {} 已添加到布隆过滤器", fieldType.name(), value);
        }
    }
}

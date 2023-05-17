package metaverse.backend.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RedisSecurityService {
    @Autowired
    private final StringRedisTemplate stringRedisTemplate;

    @Transactional
    public void setValues(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    // 만료시간 설정 -> 자동 삭제
    @Transactional
    public void setValuesWithTimeout(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    public String getValues(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    @Transactional
    public void deleteValues(String key) {
        stringRedisTemplate.delete(key);
    }
}

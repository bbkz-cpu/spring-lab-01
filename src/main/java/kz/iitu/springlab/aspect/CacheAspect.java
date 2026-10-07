package kz.iitu.springlab.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Order(0)
public class CacheAspect {

    private static final Logger log =
            LoggerFactory.getLogger(CacheAspect.class);

    private final Map<String, Object> cache =
            new ConcurrentHashMap<>();

    @Around("kz.iitu.springlab.aspect.Pointcuts.cacheOperation()")
    public Object cacheResult(ProceedingJoinPoint pjp) throws Throwable {
        String key = pjp.getSignature().toLongString()
                + Arrays.deepToString(pjp.getArgs());

        String name = pjp.getSignature().toShortString();

        Object cached = cache.get(key);

        if (cached != null) {
            log.info("[CACHE] HIT {} args={}",
                    name, Arrays.toString(pjp.getArgs()));

            return cached;
        }

        log.info("[CACHE] MISS {} args={}",
                name, Arrays.toString(pjp.getArgs()));

        Object result = pjp.proceed();

        if (result != null) {
            cache.put(key, result);
            log.info("[CACHE] STORED {}", name);
        }

        return result;
    }
}

package br.com.graflogic.base.service.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Component;

/**
 * 
 * @author gmazz
 *
 */
@Component
public class CacheUtil {

	@Autowired
	private EhCacheCacheManager cacheManager;

	private Cache getCache(String name) {
		return cacheManager.getCache(name);
	}

	public Object findOnCache(String cacheName, String key) {
		ValueWrapper cacheObj = getCache(cacheName).get(key);

		if (null == cacheObj) {
			return null;
		}

		return cacheObj.get();

	}

	public void putOnCache(String cacheName, String key, Object value) {
		getCache(cacheName).put(key, value);
	}

	public void clearCache(String name) {
		getCache(name).clear();
	}
}
package com.kcb.pay.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * TODO redis工具类
 *
 * @author crazypenguin
 * @version 1.0.0
 * @createdate 2019/1/2
 */
@Component
public class RedisUtils {
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private ValueOperations<String, String> valueOperations;
	@Autowired
	private HashOperations<String, String, Object> hashOperations;
	@Autowired
	private ListOperations<String, Object> listOperations;
	@Autowired
	private SetOperations<String, Object> setOperations;
	@Autowired
	private ZSetOperations<String, Object> zSetOperations;
	/**
	 * 默认过期时长，单位：秒
	 */
	public final static long DEFAULT_EXPIRE = 60 * 60 * 24;
	/**
	 * 不设置过期时长
	 */
	public final static long NOT_EXPIRE = -1;

	public void set(String key, Object value, long expire) {
		valueOperations.set(key, toJson(value));
		if (expire != NOT_EXPIRE) {
			redisTemplate.expire(key, expire, TimeUnit.SECONDS);
		}
	}

	public void set(String key, Object value) {
		set(key, value, DEFAULT_EXPIRE);
	}

	public <T> T get(String key, Class<T> clazz, long expire) {
		String value = valueOperations.get(key);
		if (expire != NOT_EXPIRE) {
			redisTemplate.expire(key, expire, TimeUnit.SECONDS);
		}
		return value == null ? null : fromJson(value, clazz);
	}

	public <T> T get(String key, Class<T> clazz) {
		return get(key, clazz, NOT_EXPIRE);
	}

	public String get(String key, long expire) {
		String value = valueOperations.get(key);
		if (expire != NOT_EXPIRE) {
			redisTemplate.expire(key, expire, TimeUnit.SECONDS);
		}
		return value;
	}

	public String get(String key) {
		return get(key, NOT_EXPIRE);
	}


	public void delete(String key) {
		redisTemplate.delete(key);
	}

	/**
	 * 写入map类型
	 *
	 * @param key
	 * @param map
	 */
	public void setMap(String key, Map<String, Object> map) {
		redisTemplate.opsForHash().putAll(key, map);
	}

	/**
	 * 获取map类型
	 *
	 * @param key
	 * @return
	 */
	public Map<Object, Object> getMap(String key) {
		return redisTemplate.opsForHash().entries(key);
	}

	/**
	 * 给已有mao添加值
	 *
	 * @param hashKey
	 * @param key
	 * @param value
	 */
	public void put(String hashKey, String key, Object value) {
		redisTemplate.opsForHash().put(hashKey, key, value);
	}

	/**
	 * 根据hash来获取map中指定key值
	 *
	 * @param hashKey
	 * @param key
	 * @return
	 */
	public Object getValueOfMap(String hashKey, String key) {
		return redisTemplate.opsForHash().get(hashKey, key);
	}

	/**
	 * Object转成JSON数据
	 */
	private String toJson(Object object) {
		if (object instanceof Integer || object instanceof Long || object instanceof Float ||
				object instanceof Double || object instanceof Boolean || object instanceof String) {
			return String.valueOf(object);
		}
		return JSONObject.toJSONString(object);
	}

	/**
	 * JSON数据，转成Object
	 */
	private <T> T fromJson(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);
	}

	/**
	 * 设置失效时间
	 *
	 * @param key
	 * @param expire
	 */
	public void expire(String key, long expire) {
		redisTemplate.expire(key, expire, TimeUnit.SECONDS);
	}

	/**
	 * 获取失效时间
	 *
	 * @param key
	 */
	public long ttl(String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}
}

package io.lottery.common.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

/**
 * @ClassName: RedisService
 * @Description: Redis操作类
 */
@Service
public class RedisService {

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	@Resource(name = "stringRedisTemplate")
	private ValueOperations<String, String> valOpsStr;

	/**
	 * @Description :设置超时key,自增，用于统计
	 * 
	 */
	public void increment(String key, long value) {
		valOpsStr.increment(key, value);
	}

	/**
	 * @Description :存储
	 */
	public void set(String key, String value) {
		valOpsStr.set(key, value);
	}

	/**
	 * @Description :获取
	 */
	public String get(String key) {
		return valOpsStr.get(key);
	}

	/**
	 * @Description :根据key移除
	 */
	public void remove(String key) {
		stringRedisTemplate.delete(key);
	}

	/**
	 * @Description :设置超时key,单位为秒钟
	 * 
	 */
	public void set(String key, String value, int offset) {
		valOpsStr.set(key, value, offset, TimeUnit.SECONDS);
	}

	/**
	 * @Description :根据前缀得到value
	 * 
	 */
	public List<String> getByPrefix(String prefix) {
		Set<String> keys = stringRedisTemplate.keys(prefix + "*");
		if (keys == null || keys.size() == 0) {
			return null;
		}
		List<String> list = new ArrayList<>();
		for (String string : keys) {
			list.add(valOpsStr.get(string));
		}
		return list;
	}

	/**
	 * @Description :根据前缀得到value
	 */
	public void delByPrefix(String prefix) {
		Set<String> keys = stringRedisTemplate.keys(prefix + "*");
		if (keys == null || keys.size() == 0) {
			return;
		}
		for (String key : keys) {
			stringRedisTemplate.delete(key);
		}
	}

	/**
	 * 存储set集合
	 * 
	 * @param key
	 * @param value
	 */
	public void setHashSet(String key, String value) {
		stringRedisTemplate.opsForSet().add(key, value);
	}

	/**
	 * 获取存储的set集合
	 * 
	 * @param key
	 * @return
	 */
	public Set<String> getHashSet(String key) {
		return stringRedisTemplate.opsForSet().members(key);
	}

	/**
	 * 根据key删除set集合中的某一条
	 * 
	 * @param key
	 * @param values
	 */
	public void removeHashSet(String key, String values) {
		stringRedisTemplate.opsForSet().remove(key, values);
	}

	/**
	 * 根据前缀获取set集合
	 * 
	 * @param prefix
	 * @return
	 */
	public Set<String> getSetByPrefix(String prefix) {
		Set<String> keyset = stringRedisTemplate.keys(prefix + "*");
		if (keyset == null || keyset.size() == 0) {
			return null;
		}
		Set<String> reipset = new HashSet<String>();
		for (String keys : keyset) {
			Set<String> ipset = stringRedisTemplate.opsForSet().members(keys);
			reipset.addAll(ipset);
		}
		return reipset;
	}

	/**
	 * 
	 * @desc 往redis里面放map键值队
	 * @author abo
	 * @date 2018年7月9日 下午3:05:13
	 * @param key
	 * @param hashKey
	 *            日期作为键或者彩种做key
	 * @param value
	 */
	public void setHashMap(String key, Object hashKey, Object value) {
		stringRedisTemplate.opsForHash().put(key, hashKey, value);
	}

	/**
	 * 
	 * @desc 往redis里面放map键值队
	 * @author abo
	 * @date 2018年7月9日 下午3:11:06
	 * @param key
	 * @param hashKey
	 *            日期作为键或者彩种做key
	 * @return
	 */
	public Object getHashMap(String key, Object hashKey) {
		return stringRedisTemplate.opsForHash().get(key, hashKey);
	}

	public List<Object> getHashMapList(String key) {
		Set<Object> hashkeys = stringRedisTemplate.opsForHash().keys(key);
		return stringRedisTemplate.opsForHash().multiGet(key, hashkeys);
	}

	/**
	 * 
	 * @desc 根据key获取整个hashmap
	 * @author abo
	 * @date 2018年7月11日 下午5:30:32
	 * @param key
	 * @return
	 */
	public Map<Object, Object> getHashMaps(String key) {
		return stringRedisTemplate.opsForHash().entries(key);
	}

	/**
	 * 模糊查询以sameKey开通的hashKey
	 * 
	 * @param key
	 * @param sameKey
	 * @return
	 */
	public Set<String> getHashKeys(String key, String sameKey) {
		Set<Object> set = stringRedisTemplate.opsForHash().keys(key);
		System.out.println("set.size=" + set.size());
		Set<String> keySet = new HashSet<String>();
		for (Object key1 : set) {
			String keyStr = String.valueOf(key1);
			if (keyStr.contains(sameKey)) {
				keySet.add(keyStr);
			}
		}
		System.out.println("keySet.size=" + keySet.size());
		return keySet;
	}

	/**
	 * 删除hash中的key值
	 * 
	 * @param key
	 * @param hashKeys
	 */
	public void deleteHashKeys(String key, Object... hashKeys) {
		stringRedisTemplate.opsForHash().delete(key, hashKeys);
	}

	/**
	 * 删除hash中的key值
	 * 
	 * @param key
	 * @param hashKeys
	 */
	public void deleteHashKeys(String key, Set<String> hashKeys) {
		if (StringUtils.isNotBlank(key) && hashKeys != null && hashKeys.size() > 0) {
			stringRedisTemplate.opsForHash().delete(key, hashKeys.toArray());
		}
	}

	/**
	 * 刪除key
	 * 
	 * @param key
	 */
	public void delete(String key) {
		stringRedisTemplate.delete(key);
	}

}

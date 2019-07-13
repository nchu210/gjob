/********************************************
 * 文件名称: JedisCacheBase.java
 * 系统名称: 
 * 模块名称:
 * 软件版权: 恒生电子股份有限公司
 * 功能说明: 
 * 系统版本: 
 * 开发人员: 
 * 开发时间: 
 * 审核人员:
 * 相关文档:
 * 修改记录: 修改日期    修改人员    修改说明
 *********************************************/

package com.gjob.base.redis;

import java.util.List;
import java.util.Map;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.BinaryClient.LIST_POSITION;

public class JedisCacheBase {

	static public int retryCount = 50;

	static public void hmset(String key, Map<String, String> valueMap) {
		if (valueMap != null && valueMap.size() != 0) {
			Jedis jedis = null;
			JedisCluster jedisCluster = null;
			try {
				if (JedisFactory.getInstance().isCluster) {
					jedisCluster = JedisFactory.getInstance().getJedisCluster();
					jedisCluster.hmset(key, valueMap);
				} else {

					jedis = JedisFactory.getInstance().getJedis();
					int i = 0;
					while (i < retryCount) {
						i++;
						String watchState = jedis.watch(key);
						if (!watchState.equals("OK")) {
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
							}
							continue;
						}
						Transaction multi = jedis.multi();
						multi.hmset(key, valueMap);
						if (multi.exec() == null) {
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
							}
							continue;
						}
						jedis.unwatch();
						break;
					}
					if (i == retryCount)
						System.err.println(key + " hmset failure!");
					System.err.println(key + " hmset failure!");

				}
			} finally {
				if (jedis != null)
					JedisFactory.getInstance().returnResource(jedis);
			}
		}
	}

	static public void clear(String key) {
		Jedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				jedisCluster = JedisFactory.getInstance().getJedisCluster();
				jedisCluster.del(key);
				System.out.println(
						"JedisCacheBase.clear清除缓存, key=" + key
								+ " Jedis Cluster");
			} else {
				jedis = JedisFactory.getInstance().getJedis();

				jedis.del(key);
				if (!JedisFactory.getInstance().isSentinels()) {
					System.out.println(
							"JedisCacheBase.clear清除缓存, key=" + key + " 服务器信息："
									+ JedisFactory.getInstance().getIp() + ":"
									+ JedisFactory.getInstance().getPort());
				} else {
					System.out.println(
							"JedisCacheBase.clear清除缓存, key=" + key
									+ " Sentinel Cluster");
				}
			}
		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
		}
	}

	static public String hmget(String key, String secondKey) {
		Jedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				jedisCluster = JedisFactory.getInstance().getJedisCluster();
				List<String> list = jedisCluster.hmget(key, secondKey);
				if (list != null && list.get(0) != null) {
					return list.get(0);
				} else {
					System.out.println(
							"JedisCacheBase.hmget获取缓存为空, key=" + key
									+ ",secondKey=" + secondKey
									+ " Jedis Cluster");
					return null;
				}
			} else {
				jedis = JedisFactory.getInstance().getJedis();
				List<String> list = jedis.hmget(key, secondKey);
				if (list != null && list.get(0) != null) {
					return list.get(0);
				} else {
					if (!JedisFactory.getInstance().isSentinels()) {
						System.out.println(
								"JedisCacheBase.hmget获取缓存为空, key=" + key
										+ ",secondKey=" + secondKey + " 服务器信息："
										+ JedisFactory.getInstance().getIp()
										+ ":"
										+ JedisFactory.getInstance().getPort());
					} else {
						System.out.println(
								"JedisCacheBase.hmget获取缓存为空, key=" + key
										+ ",secondKey=" + secondKey
										+ " Sentinel Cluster");
					}
					return null;
				}
			}
		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
		}
	}

	static public void rpush(String key, List<String> valueList) {
		if (valueList != null && valueList.size() != 0) {
			Jedis jedis = null;
			JedisCluster jedisCluster = null;
			try {
				if (JedisFactory.getInstance().isCluster()) {
					jedisCluster = JedisFactory.getInstance().getJedisCluster();
					for (String value : valueList)
						jedisCluster.rpush(key, value);
				} else {

					jedis = JedisFactory.getInstance().getJedis();
					int i = 0;
					while (i < retryCount) {
						i++;
						String watchState = jedis.watch(key);
						if (!watchState.equals("OK")) {
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
							}
							continue;
						}
						Transaction multi = jedis.multi();
						for (String value : valueList)
							multi.rpush(key, value);
						if (multi.exec() == null) {
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
							}
							continue;
						}
						jedis.unwatch();
						break;
					}
					if (i == retryCount)
						System.err.println(key + " hmset failure!");
				}
			} finally {
				if (jedis != null)
					JedisFactory.getInstance().returnResource(jedis);
			}
		}
	}

	static public List<String> lrange(String key) {
		Jedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				jedisCluster = JedisFactory.getInstance().getJedisCluster();
				List<String> list = jedisCluster.lrange(key, 0, -1);
				if (list.size() == 0) {
					System.out.println(
							"JedisCacheBase.lrange获取list缓存为空, key=" + key
									+ " Jedis Cluster");
				}
				return list;
			} else {
				jedis = JedisFactory.getInstance().getJedis();
				List<String> list = jedis.lrange(key, 0, -1);
				if (list.size() == 0) {
					if (!JedisFactory.getInstance().isSentinels()) {
						System.out.println(
								"JedisCacheBase.lrange获取list缓存为空, key=" + key
										+ " 服务器信息："
										+ JedisFactory.getInstance().getIp()
										+ ":"
										+ JedisFactory.getInstance().getPort());
					} else {
						System.out.println(
								"JedisCacheBase.lrange获取list缓存为空, key=" + key
										+ " Sentinel Cluster");
					}
				}
				return list;
			}
		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
		}
	}

	static public Boolean exists(String key, String secondKey) {
		Jedis jedis = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				JedisCluster jedisCluster = JedisFactory.getInstance()
						.getJedisCluster();
				List<String> list = jedisCluster.hmget(key, secondKey);
				if (list != null) {
					return list.get(0) != null;
				} else {
					return false;
				}
			} else {
				jedis = JedisFactory.getInstance().getJedis();
				List<String> list = jedis.hmget(key, secondKey);
				if (list != null) {
					return list.get(0) != null;
				} else {
					return false;
				}

			}
		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
		}
	}

	static public void set(String key, String value) {
		if (!value.isEmpty()) {
			Jedis jedis = null;
			JedisCluster jedisCluster = null;
			try {
				if (JedisFactory.getInstance().isCluster()) {
					jedisCluster = JedisFactory.getInstance().getJedisCluster();
					jedisCluster.set(key, value);
				} else {
					jedis = JedisFactory.getInstance().getJedis();
					int i = 0;
					while (i < retryCount) {
						i++;
						String watchState = jedis.watch(key);
						if (!watchState.equals("OK")) {
							try {
								Thread.sleep(5);
							} catch (InterruptedException e) {
							}
							continue;
						}
						jedis.set(key, value);
						jedis.unwatch();
						break;
					}
					if (i == retryCount)
						System.err.println(
								key + " set failure!");
				}
			} finally {
				if (jedis != null)
					JedisFactory.getInstance().returnResource(jedis);
			}
		}
	}

	static public String get(String key) {
		Jedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				jedisCluster = JedisFactory.getInstance().getJedisCluster();
				String value = jedisCluster.get(key);
				if (value == null) {
					System.out.println(
							"JedisCacheBase.get获取缓存为空, key=" + key
									+ " Jedis Cluster");
				}
				return value;
			} else {
				jedis = JedisFactory.getInstance().getJedis();
				String value = jedis.get(key);
				if (value == null) {
					if (!JedisFactory.getInstance().isSentinels()) {
						System.out.println(
								"JedisCacheBase.get获取缓存为空, key=" + key
										+ " 服务器信息："
										+ JedisFactory.getInstance().getIp()
										+ ":"
										+ JedisFactory.getInstance().getPort());
					} else {
						System.out.println(
								"JedisCacheBase.get获取缓存为空, key=" + key
										+ " Sentinel Cluster");
					}
				}
				return value;
			}
		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
		}
	}

	static public String getInfo(String section) {
		Jedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				jedisCluster = JedisFactory.getInstance().getJedisCluster();
				String info = "";
				if (section==null) {
					info = jedisCluster.info();
				} else {
					info = jedisCluster.info(section);
				}
				return info;
			} else {
				jedis = JedisFactory.getInstance().getJedis();
				String info = "";
				if (section==null) {
					info = jedis.info();
				} else {
					info = jedis.info(section);
				}
				return info;
			}

		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
		}
	}

	static public void hset(String key, String secondKey, String value) {
		Jedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				jedisCluster = JedisFactory.getInstance().getJedisCluster();
				int i = 0;
				while (i < retryCount) {
					i++;
					Long ret = jedisCluster.hset(key, secondKey, value);
					if (ret != 0 && ret != 1) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
						}
						continue;
					}
					break;
				}
				if (i == retryCount)
					System.err.println(key + " hset failure!");
			} else {
				jedis = JedisFactory.getInstance().getJedis();
				int i = 0;
				while (i < retryCount) {
					i++;
					Long ret = jedis.hset(key, secondKey, value);
					if (ret != 0 && ret != 1) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
						}
						continue;
					}
					break;
				}
				if (i == retryCount)
					System.err.println(key + " hset failure!");
			}
		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
		}
	}

	static public String hget(String key, String secondKey) {
		Jedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				jedisCluster = JedisFactory.getInstance().getJedisCluster();
				String val = jedisCluster.hget(key, secondKey);
				if (val == null) {
					System.out.println(
							"JedisCacheBase.hget获取缓存为空, key=" + key
									+ ",secondKey=" + secondKey
									+ " Jedis Cluster");
				}
				return val;
			} else {
				jedis = JedisFactory.getInstance().getJedis();
				String val = jedis.hget(key, secondKey);
				if (val == null) {
					if (!JedisFactory.getInstance().isSentinels()) {
						System.out.println(
								"JedisCacheBase.hget获取缓存为空, key=" + key
										+ ",secondKey=" + secondKey + " 服务器信息："
										+ JedisFactory.getInstance().getIp()
										+ ":"
										+ JedisFactory.getInstance().getPort());
					} else {
						System.out.println(
								"JedisCacheBase.hget获取缓存为空, key=" + key
										+ ",secondKey=" + secondKey
										+ " Sentinel Cluster");
					}

				}
				return val;
			}
		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
		}
	}

	/**
	 * 删除hashmap中的一个元素
	 * 
	 * @param key
	 *            redis键
	 * @param secondKey
	 *            hash键
	 */
	static public void hdel(String key, String secondKey) {
		Jedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				jedisCluster = JedisFactory.getInstance().getJedisCluster();
				Long ret = jedisCluster.hdel(key, secondKey);
			} else {
				jedis = JedisFactory.getInstance().getJedis();
				int i = 0;
				while (i < retryCount) {
					i++;
					Long ret = jedis.hdel(key, secondKey);
					if (ret != 0 && ret != 1) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
						}
						continue;
					}
					break;
				}
				if (i == retryCount)
					System.err.println(key + " hdel failure!");
			}
		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
		}
	}

	/**
	 * 获取整个hashmap
	 * 
	 * @param key
	 * @return
	 */
	static public Map<String, String> hgetAll(String key) {
		Jedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				jedisCluster = JedisFactory.getInstance().getJedisCluster();
				Map<String, String> map = jedisCluster.hgetAll(key);
				if (map == null) {
					System.out.println(
							"JedisCacheBase.hgetAll获取缓存为空, key=" + key
									+ "  Jedis Cluster");
				}
				return map;
			} else {
				jedis = JedisFactory.getInstance().getJedis();
				Map<String, String> map = jedis.hgetAll(key);
				if (map == null) {
					if (!JedisFactory.getInstance().isSentinels()) {
						System.out.println(
								"JedisCacheBase.hgetAll获取缓存为空, key=" + key
										+ " 服务器信息："
										+ JedisFactory.getInstance().getIp()
										+ ":"
										+ JedisFactory.getInstance().getPort());
					} else {
						System.out.println(
								"JedisCacheBase.hgetAll获取缓存为空, key=" + key
										+ " Sentinel Cluster");
					}
				}
				return map;
			}
		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
		}
	}

	/**
	 * 删除list中值为value的元素（从左往右删除一个元素）
	 * 
	 * @param key
	 * @param value
	 */
	static public void lrem(String key, String value) {
		Jedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				Long ret = null;
				jedisCluster = JedisFactory.getInstance().getJedisCluster();
				ret = jedisCluster.lrem(key, 1, value);
			} else {
				Long ret = null;
				jedis = JedisFactory.getInstance().getJedis();
				int i = 0;
				while (i < retryCount) {
					i++;
					ret = jedis.lrem(key, 1, value);
					if (ret == null) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
						}
						continue;
					}
					break;
				}
				if (i == retryCount)
					System.err.println(key + " lrem failure!");
			}
		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
		}
	}

	/**
	 * 删除list中valueList对应值得多个元素（从左往右删除一个元素）
	 * 
	 * @param key
	 * @param valueList
	 */
	static public void lrem(String key, List<String> valueList) {
		Jedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				jedisCluster = JedisFactory.getInstance().getJedisCluster();
				for (String value : valueList)
					jedisCluster.lrem(key, 1, value);
			} else {

				Long ret = null;
				jedis = JedisFactory.getInstance().getJedis();
				int i = 0;
				while (i < retryCount) {
					i++;
					String watchState = jedis.watch(key);
					if (!watchState.equals("OK")) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
						}
						continue;
					}
					Transaction multi = jedis.multi();
					for (String value : valueList)
						multi.lrem(key, 1, value);
					if (multi.exec() == null) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
						}
						continue;
					}
					jedis.unwatch();
					break;
				}
				if (i == retryCount)
					System.err.println(key + " lrem list failure!");
			}
		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
		}
	}

	/**
	 * 设置list中index下标对应的值
	 * 
	 * @param key
	 * @param index
	 * @param value
	 */
	static public void lset(String key, long index, String value) {
		Jedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				jedisCluster = JedisFactory.getInstance().getJedisCluster();
				String ret = jedisCluster.lset(key, index, value);
			} else {
				jedis = JedisFactory.getInstance().getJedis();
				int i = 0;
				while (i < retryCount) {
					i++;
					String ret = jedis.lset(key, index, value);
					if (!"OK".equals(ret)) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
						}
						continue;
					}
					break;
				}
				if (i == retryCount)
					System.err.println(key + " lset failure!");
			}
		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
		}
	}

	/**
	 * 更新，jedis无法获取下标，所以删除原值后在列表最后添加新值
	 * 
	 * @param key
	 * @param value
	 *            原值
	 * @param newValue
	 *            新值
	 * @return
	 */
	static public boolean lupdate(String key, String value, String newValue) {
		boolean successful = false;
		Jedis jedis = null;
		JedisCluster jedisCluster = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				jedisCluster = JedisFactory.getInstance().getJedisCluster();
				jedisCluster.rpush(key, newValue);
				jedisCluster.lrem(key, 1, value);
				successful = true;
			} else {
				jedis = JedisFactory.getInstance().getJedis();
				int i = 0;
				while (i < retryCount) {
					i++;
					String watchState = jedis.watch(key);
					if (!watchState.equals("OK")) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
						}
						continue;
					}
					Transaction multi = jedis.multi();
					multi.rpush(key, newValue);
					multi.lrem(key, 1, value);
					if (multi.exec() == null) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
						}
						continue;
					}
					jedis.unwatch();
					break;
				}
				if (i == retryCount)
					System.err.println(key + " lupdate failure!");
				else
					successful = true;
			}
		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
			return successful;
		}
	}

	/**
	 * 更新，保持原顺序
	 * 
	 * @param key
	 * @param value
	 *            原值
	 * @param newValue
	 *            新值
	 * @return
	 */
	static public boolean lupdateSorted(String key, String value,
			String newValue) {
		boolean successful = false;
		JedisCluster jedisCluster = null;
		Jedis jedis = null;
		try {
			if (JedisFactory.getInstance().isCluster()) {
				jedisCluster = JedisFactory.getInstance().getJedisCluster();
				jedisCluster.linsert(key, LIST_POSITION.AFTER, value, newValue);
				jedisCluster.lrem(key, 1, value);
				successful = true;
			} else {
				jedis = JedisFactory.getInstance().getJedis();
				int i = 0;
				while (i < retryCount) {
					i++;
					String watchState = jedis.watch(key);
					if (!watchState.equals("OK")) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
						}
						continue;
					}
					Transaction multi = jedis.multi();
					multi.linsert(key, LIST_POSITION.AFTER, value, newValue);
					multi.lrem(key, 1, value);
					if (multi.exec() == null) {
						try {
							Thread.sleep(5);
						} catch (InterruptedException e) {
						}
						continue;
					}
					jedis.unwatch();
					break;
				}
				if (i == retryCount)
					System.err.println(key + " lupdateSorted failure!");
				else
					successful = true;
			}
		} finally {
			if (jedis != null)
				JedisFactory.getInstance().returnResource(jedis);
			return successful;
		}
	}

}

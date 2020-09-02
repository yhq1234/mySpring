package com.example.utils;

import com.alibaba.druid.support.json.JSONParser;
import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.databind.util.JSONPObject;
import io.netty.util.internal.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 */
@Component
public class RedisStringTemlateUtils {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 设置key过期时间，一秒为单位
     * @param key
     * @param time
     * @return
     */
    public boolean expired(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除keys对应的记录,可以是多个key
     * @param keys
     */
    @SuppressWarnings("unchecked")
    public void del(String... keys) {
        if (keys != null && keys.length > 0) {
            if (keys.length == 1) {
                redisTemplate.delete(keys[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(keys));
            }
        }

    }
    /**
     * 判断key是否存在
     * @param key
     * @return
     */
    public boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * 根据key获取数据
     * @param  key 取值key  map_key redis队列名
     * @param t
     * @param <T>
     * @return
     */
    public  <T>T getStringbean(String key,Class<T> t){
        String json = get(key);
        T t1 = null;
        try {
            t1 = t.newInstance();
            if(!StringUtils.isEmpty(json)){
                t1 = (T) JSONUtils.parse(json);
            }else{
                t1 = null;
            }

        } catch (InstantiationException | IllegalAccessException e) {
            t1 = null;
        }
        return t1;
    }
    /**
     * 根据key获取记录
     * @param key
     * @return
     */
    public String get(String key) {
        Object o = redisTemplate.opsForValue().get(key);
        return o!= null?o.toString():null;
    }
    /**
     * 添加记录,如果记录已存在将覆盖原有的value
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, String value) {

        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key
     *            键
     * @param value
     *            值
     * @param time
     *            时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, String value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time,TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递增
     *
     * @param key 键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key  键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }

    // ================================Map=================================



    /**
     * 根据key获取数据
     * @param  key 取值key  map_key redis队列名
     * @return
     * @throws Exception
     */
    public  <T>T getMapbean(String key,String map_key,Class<T> t){
        long i =hlen(map_key);
        String json = "";
        T t1 = null;
        try {

            if(i > 0){
                t1 = t.newInstance();
                json = hget(map_key,key);
                if(!StringUtils.isEmpty(json)){
                    t1 = (T) JSONUtils.parse(json);
                } else{
                    t1 = null;
                }

            }
        } catch (InstantiationException | IllegalAccessException e) {
            t1 = null;
        }
        return t1;
    }

    /**
     * 从hash中删除指定的存储
     * @param key
     * @param fieid
     */
    public  void  hdel(String key, String fieid) {
        redisTemplate.opsForHash().delete(key,fieid);
    }


    /**
     * HashGet
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public String hget(String key,String item){
        Object o = redisTemplate.opsForHash().get(key, item);
        return o != null?o.toString():null;
    }

    /**
     * 获取hashKey对应的所有键值
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object,Object> hgetAll(String key){
        return redisTemplate.opsForHash().entries(key);
    }



    /**
     * HashSet
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String,String> map){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String,String> map, long time){
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if(time>0){
                expired(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,String value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset(String key,String item,Object value,long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if(time>0){
                expired(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel(String key, Object... item){
        redisTemplate.opsForHash().delete(key,item);
    }

    /**
     * 判断hash表中是否有该项的值
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean getmapkey(String key, String item){
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     * @return
     */
    public double hincr(String key, String item,double by){
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     * @return
     */
    public double hdecr(String key, String item,double by){
        return redisTemplate.opsForHash().increment(key, item,-by);
    }


    /**
     * 获取hash中存储的个数，类似Map中size方法
     * @param   key
     * @return long 存储的个数
     * */
    public  long hlen(String key) {
        return redisTemplate.opsForHash().size(key);
    }



    //============================set=============================
    /**
     * 根据key获取Set中的所有值
     * @param key 键
     */
    public Set<String> sGet(String key){
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey(String key,Object value){
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet(String key, String...values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime(String key,long time,String...values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if(time>0) {expired(key, time);}
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     * @param key 键
     * @return
     */
    public long sGetSetSize(String key){
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object ...values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    //===============================list=================================

    /**
     * 获取list缓存的内容
     * @param key 键
     * @param start 开始
     * @param end 结束  0 到 -1代表所有值
     * @return
     */
    public List<String> ltrim(String key, long start, long end){
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     * @param key 键
     * @return
     */
    public long llen(String key){
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public String lindex(String key,long index){
        try {
            Object o = redisTemplate.opsForList().index(key, index);
            return  o != null?o.toString():null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 向List尾部追加记录
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean rpush(String key, String value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * l向List尾部追加记录
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean rpush(String key, String value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {expired(key, time);}
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 向List头部追加记录
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean lpush(String key, String value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * l向List头部追加记录
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean lpush(String key, String value, long time) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            if (time > 0){ expired(key, time);}
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public String lpop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String rpop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @return
     */
    public boolean lSet(String key, List<String> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     * @return
     */
    public boolean lSet(String key, List<String> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0){ expired(key, time);}
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     * @param key 键
     * @param index 索引
     * @param value 值
     * @return
     */
    public boolean lUpdateIndex(String key, long index,String value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove(String key,long count,Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 取出并移除第一条数据
     * @param  list_key 取值key  map_key redis队列名
     * @return
     * @throws Exception
     */
    public  <T>T getListbean(String list_key,Class<T> t){
        String json = "";
        T t1 = null;

        try {
            t1 = t.newInstance();
            if (exists(list_key)) {
                Long i =llen(list_key);
                if(i > 0){
                    json = lindex(list_key,0);
                    if(!StringUtils.isEmpty(json)){
                        t1 = (T) JSONUtils.parse(json);
                    } else{
                        t1 = null;
                    }
                }else{
                    t1=null;
                }
            }else{
                t1=null;
            }
        } catch (Exception e) {
            t1 = null;
        }

        return t1;
    }


    /**
     * 获取指定范围的记录，可以做为分页使用
     * @param  key
     * @param  start
     * @param  end
     * @return List
     * */
    public List<String> lrange(String key, long start, long end) {
        List<String> list = redisTemplate.opsForList().range(key, start, end);
        return list;
    }


}

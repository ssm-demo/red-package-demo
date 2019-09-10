package cn.zy.rp.service.impl;

import cn.zy.rp.dao.RedPacketDao;
import cn.zy.rp.dao.UserRedPacketDao;
import cn.zy.rp.pojo.RedPackage;
import cn.zy.rp.pojo.UserRedPacket;
import cn.zy.rp.service.RedisRedPacketService;
import cn.zy.rp.service.UserRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

@Service
public class UserRedPacketServiceImpl implements UserRedPacketService
{
    @Autowired
    private RedPacketDao redPacketDao;

    @Autowired
    UserRedPacketDao userRedPacketDao;

    @Autowired
    RedisRedPacketService redisRedPacketService;

    @Autowired
    RedisTemplate redisTemplate;

    private static final int FAILED = 0;

    // Lua 脚本
    String lua_script ="local listKey = 'red_packet_list_'..KEYS[1] \n" +
        "local redPacket = 'red_packet_'..KEYS[1] \n" +
        "print(redPacket) \n" +
        "local stock = tonumber(redis.call('hget', redPacket, 'stock')) \n" +
        "if stock <= 0 then return 0 end \n" +
        "stock = stock - 1 \n" +
        "redis.call('hset', redPacket, 'stock', tostring(stock)) \n" +
        "redis.call('rpush', listKey, ARGV[1]) \n" +
        "if stock == 0 then return 2 end \n" +
        "return 1";

    // Lua 脚本
    String stock_script ="local listKey = 'red_packet_list_'..KEYS[1] \n" +
        "local redPacket = 'red_packet_'..KEYS[1] \n" +
        "print(redPacket) \n" +
        "local stock = tonumber(redis.call('hget', redPacket, 'stock')) \n" +
        "return stock";

    // Lua 脚本
    String red_packet__script ="local listKey = 'red_packet_list_'..KEYS[1] \n" +
        "local redPacket = 'red_packet_'..KEYS[1] \n" +
        "return redPacket ";


    // 在缓存Lua脚本后，使用该变量保存Redis返回的32位的SHA1编码，使用它执行缓存的Lua脚本
    String lua_sha = null;

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED)
    public int grabRedPacket(Long redPacketId, Long userId)
    {
        // 获取红包信息
        RedPackage redPackage = redPacketDao.getRedPacket(redPacketId);
        // 库存大于0
       if (redPackage.getStock() > 0)
        {
            redPacketDao.dereaseStock(redPacketId);

            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setAmount(redPackage.getUnitAmount());
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            userRedPacket.setNote("抢红包" + redPackage.getNote());
            return userRedPacketDao.grabRedPacket(userRedPacket);
       }
        return FAILED;
    }

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED)
    public int grabRedPacketBypessimisticlocking(Long redPacketId, Long userId)
    {
        RedPackage redPackage = redPacketDao.getRedPacketForUpdate(redPacketId);
        // 库存大于0
        if (redPackage.getStock() > 0)
        {
            redPacketDao.dereaseStock(redPacketId);

            UserRedPacket userRedPacket = new UserRedPacket();
            userRedPacket.setAmount(redPackage.getUnitAmount());
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            userRedPacket.setNote("抢红包" + redPackage.getNote());
            return userRedPacketDao.grabRedPacket(userRedPacket);
        }
        return FAILED;

    }

    @Override
    @Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRED)
    public int grabRedPacketByOptimisticLock(Long redPacketId, Long userId)
    {
        int times=0;
        while(true)
        {
            // 重试3次，还是没成功则失败
            if(times>=3)
            {
                return FAILED;
            }
            // 获取红包信息
            RedPackage redPackage = redPacketDao.getRedPacket(redPacketId);
            // 库存大于0
            if (redPackage.getStock() > 0)
            {
                int update =redPacketDao.dereaseStockByVersion(redPacketId,redPackage.getVersion());
                if(update == 0)
                {
                    times++;
                    continue;
                }

                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setAmount(redPackage.getUnitAmount());
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setUserId(userId);
                userRedPacket.setNote("抢红包" + redPackage.getNote());
                return userRedPacketDao.grabRedPacket(userRedPacket);
            }
            // 没库存了直接失败
            else
            {
                return FAILED;
            }
        }

    }

    @Override
    public Long grabRedPacketByRedis(Long redPacketId, Long userId)
    {
        Long result = null;
        // 获取底层Redis操作对象
        Jedis jedis = (Jedis)redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
        try
        {
            if (StringUtils.isEmpty(lua_sha))
            {
                lua_sha = jedis.scriptLoad(lua_script);
            }
            System.err.println("stock:" + jedis.eval(red_packet__script,1,redPacketId + ""));
            System.err.println("stock:" + jedis.eval(stock_script,1,redPacketId + ""));
            result = (Long)jedis.evalsha(lua_sha,1 ,redPacketId+"", userId+"_" + System.currentTimeMillis());
            if(result == 2)
            {
                // 获取单个小红包金额
                String unitAmountStr = jedis.hget("red_packet_" + redPacketId,"unit_amount");
                // 触发保存数据库操作
                Double unitAmount = Double.parseDouble(unitAmountStr);
                System.err.println("thread_name = " + Thread.currentThread().getName());
                redisRedPacketService.saveUserRedPacketByRedis(redPacketId,unitAmount);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(null != jedis && jedis.isConnected())
            {
                jedis.close();
            }
        }
        return result;
    }

}

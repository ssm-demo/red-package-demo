package cn.zy.rp.service.impl;

import cn.zy.rp.pojo.UserRedPacket;
import cn.zy.rp.service.RedisRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RedisRedPacketServiceImpl implements RedisRedPacketService
{
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    DataSource dataSource;

    public static final String LIST_PREFIX = "red_packet_list_";

    private static final int TIME_SIZE = 1000;


    @Override
    @Async  // 开启新线程运行
    public void saveUserRedPacketByRedis(Long redPacketId, Double unitAmount)
    {
        System.err.println("开始保存数据");

        int count = 0;

        // 获取列表操作对象
        BoundListOperations ops = redisTemplate.boundListOps(LIST_PREFIX + redPacketId);
        Long size = ops.size();
        // 1000为批次进程存储
        Long times = size % TIME_SIZE ==0? size/ TIME_SIZE : size/ TIME_SIZE + 1;
        for(int i = 0; i < times; i++)
        {
            List userIdList = null;
            if(i == 0)
            {
                userIdList = ops.range(i*TIME_SIZE,(i+1)*TIME_SIZE);
            }
            else
            {
                userIdList = ops.range(i*TIME_SIZE+1,(i+1)*TIME_SIZE);
            }

            List<UserRedPacket> userRedPacketList = new ArrayList<>();
            // 保存红包
            for(int j = 0; j < userIdList.size() ; j++)
            {
                String args = userIdList.get(j).toString();
                String[] arr = args.split("_");
                if(null == arr || arr.length < 2)
                {
                    continue;
                }
                Long userId = Long.parseLong(arr[0]);
                Long time = Long.parseLong(arr[1]);

                UserRedPacket userRedPacket = new UserRedPacket();
                userRedPacket.setRedPacketId(redPacketId);
                userRedPacket.setUserId(userId);
                userRedPacket.setAmount(unitAmount);
                userRedPacket.setGrabTime(new Timestamp(time));
                userRedPacket.setNote(userId +" grab the redPacket " + redPacketId);
                userRedPacketList.add(userRedPacket);
            }
            count += executeBatch(userRedPacketList);
        }

        // 删除redis列表
        redisTemplate.delete(LIST_PREFIX+ redPacketId);

    }

    /**
     * 批量运行
     * @param userRedPackets
     * @return
     */
    private int executeBatch(List<UserRedPacket> userRedPackets)
    {
        try(Connection connection = dataSource.getConnection())
        {
            connection.setAutoCommit(false);
            Statement stmt = connection.createStatement();
            for(UserRedPacket userRedPacket : userRedPackets)
            {
                String decreaseStockSql = "update t_red_packet set stock = stock-1 where id =" + userRedPacket.getRedPacketId();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String grabRedPacketSql = "insert into t_user_red_packet(red_packet_id, user_id, amount, grab_time, note) values ("
                    +  userRedPacket.getRedPacketId() +" ," + userRedPacket.getUserId() + "," + userRedPacket.getAmount() + ",'"
                    + dateFormat.format(userRedPacket.getGrabTime()) +"','" + userRedPacket.getNote() +"')";
                stmt.addBatch(decreaseStockSql);
                stmt.addBatch(grabRedPacketSql);
            }

            int[] count =  stmt.executeBatch();
            connection.commit();
            return count.length /2;
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }
        return 0;

    }
}

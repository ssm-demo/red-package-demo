package cn.zy.rp.dao;

import cn.zy.rp.pojo.UserRedPacket;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRedPacketDao
{

    /**
     * 保存抢红包信息
     * @param userRedPacket
     * @return
     */
    public int grabRedPacket(UserRedPacket userRedPacket);
}

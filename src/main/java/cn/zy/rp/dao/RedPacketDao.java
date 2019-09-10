package cn.zy.rp.dao;

import cn.zy.rp.pojo.RedPackage;
import org.springframework.stereotype.Repository;


@Repository
public interface RedPacketDao
{
    /**
     * 获取红包信息
     * @param id
     * @return
     */
    public RedPackage getRedPacket(Long id);

    /**
     * 扣减红包库存
     * @param id
     * @return
     */
    public int dereaseStock(Long id);


    public RedPackage getRedPacketForUpdate(Long id);

    public int dereaseStockByVersion(Long id, Long version);

}

package cn.zy.rp.service;

import cn.zy.rp.pojo.RedPackage;

public interface RedPacketService
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


}

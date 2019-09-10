package cn.zy.rp.service;

public interface UserRedPacketService
{
    /**
     * 保存抢红包信息----多并发下会出现超抢
     * @param redPacketId
     * @param userId
     * @return
     */
    public int grabRedPacket(Long redPacketId, Long userId);

    /**
     * 使用悲观锁实现抢红包---会加锁 性能影响较大 不会超抢
     * @param redPacketId
     * @param userId
     * @return
     */
    public int grabRedPacketBypessimisticlocking(Long redPacketId, Long userId);

    /**
     *  使用乐观锁实现抢红包
     * @param redPacketId
     * @param userId
     * @return
     */
    public int grabRedPacketByOptimisticLock(Long redPacketId, Long userId);

    /**
     * 使用redis实现抢红包
     * @param redPacket
     * @param userId
     * @return
     */
    public Long grabRedPacketByRedis(Long redPacket, Long userId);

}

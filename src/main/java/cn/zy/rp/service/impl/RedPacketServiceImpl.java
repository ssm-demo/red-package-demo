package cn.zy.rp.service.impl;

import cn.zy.rp.dao.RedPacketDao;
import cn.zy.rp.pojo.RedPackage;
import cn.zy.rp.service.RedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RedPacketServiceImpl implements RedPacketService
{
    @Autowired
    RedPacketDao redPacketDao;

    @Override
    @Transactional(isolation=Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public RedPackage getRedPacket(Long id)
    {
       return redPacketDao.getRedPacket(id);
    }

    @Override
    @Transactional(isolation=Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int dereaseStock(Long id)
    {
       return redPacketDao.dereaseStock(id);
    }
}

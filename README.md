# red-package-demo
用ssm实现高并发抢红包业务
http://localhost:8080/userRedPacket/index=http://localhost:8080/==>index.jsp
// 获取红包具体信息
http://localhost:8080/userRedPacket/getRedPackage?redPacketId={id}

// 触发抢红包
http://localhost:8080/grabRedPacket.jsp---不考虑超发的普通抢红包

//悲观锁实现抢红包---性能较低，不会超发
http://localhost:8080/userRedPacket/grabRedPacketBypessimisticlocking?redPacketId={id}&uesrId={userId}

// 乐观锁-版本比较实现抢红包--容易失败，引入重试机制-性能较悲观锁优 不会超发
http://localhost:8080/userRedPacket/grabRedPacketByOptimisticLock?redPacketId={id}&uesrId={userId}

// Redis+Lua实现 较复杂 性能好 利用lua的原子性保证事务
http://localhost:8080/userRedPacket/grabRedPacketByRedis?redPacketId={id}&uesrId={userId}

package cn.zy.rp.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

public class UserRedPacket implements Serializable
{

    private static final long serialVersionUID = -7641138811348361617L;

    private Long id;
    // 红包id
    private Long redPacketId;
    // 用户id
    private Long userId;
    // 抢得红包的时间
    private Timestamp grabTime;

    // 红包金额
    private Double amount;
    // 备注
    private String note;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getRedPacketId()
    {
        return redPacketId;
    }

    public void setRedPacketId(Long redPacketId)
    {
        this.redPacketId = redPacketId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Timestamp getGrabTime()
    {
        return grabTime;
    }

    public void setGrabTime(Timestamp grabTime)
    {
        this.grabTime = grabTime;
    }

    public Double getAmount()
    {
        return amount;
    }

    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

    public String getNote()
    {
        return note;
    }

    public void setNote(String note)
    {
        this.note = note;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb =
            new StringBuilder("UserRedPacket{");
        sb.append("id=").append(id);
        sb.append(", redPacketId=").append(redPacketId);
        sb.append(", userId=").append(userId);
        sb.append(", grabTime=").append(grabTime);
        sb.append(", amount=").append(amount);
        sb.append(", note='").append(note).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

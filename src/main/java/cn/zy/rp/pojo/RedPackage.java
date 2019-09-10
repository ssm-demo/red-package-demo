package cn.zy.rp.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

public class RedPackage implements Serializable
{
    private static final long serialVersionUID = 3335655592929159482L;

    // 红包id
    private Long id;
    // 发红包的用户id
    private Long uesrId;
    // 红包总金额
    private Double amount;
    // 发放时间
    private Timestamp sendDate;
    // 红包总数量
    private Long total;
    // 库存--多少个红包剩余
    private Long stock;
    // 单个红包的金额
    private Double unitAmount;
    // 版本号
    private Long version;

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

    public Long getUesrId()
    {
        return uesrId;
    }

    public void setUesrId(Long uesrId)
    {
        this.uesrId = uesrId;
    }

    public Double getAmount()
    {
        return amount;
    }

    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

    public Timestamp getSendDate()
    {
        return sendDate;
    }

    public void setSendDate(Timestamp sendDate)
    {
        this.sendDate = sendDate;
    }

    public Long getTotal()
    {
        return total;
    }

    public void setTotal(Long total)
    {
        this.total = total;
    }

    public Long getStock()
    {
        return stock;
    }

    public void setStock(Long stock)
    {
        this.stock = stock;
    }

    public Double getUnitAmount()
    {
        return unitAmount;
    }

    public void setUnitAmount(Double unitAmount)
    {
        this.unitAmount = unitAmount;
    }

    public Long getVersion()
    {
        return version;
    }

    public void setVersion(Long version)
    {
        this.version = version;
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
            new StringBuilder("RedPackage{");
        sb.append("id=").append(id);
        sb.append(", uesrId=").append(uesrId);
        sb.append(", amount=").append(amount);
        sb.append(", sendDate=").append(sendDate);
        sb.append(", total=").append(total);
        sb.append(", stock=").append(stock);
        sb.append(", unitAmount=").append(unitAmount);
        sb.append(", version=").append(version);
        sb.append(", note='").append(note).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

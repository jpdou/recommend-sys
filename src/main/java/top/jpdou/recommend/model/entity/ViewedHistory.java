package top.jpdou.recommend.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ViewedHistory {
    @Id
    private Integer id;
    private Integer customerId;
    private Integer productId;
    private Integer viewedCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getViewedCount() {
        return viewedCount;
    }

    public void setViewedCount(Integer viewedCount) {
        this.viewedCount = viewedCount;
    }

    public void increaseViewedCount()
    {
        viewedCount++;
    }

    public void increaseViewedCount(int qty)
    {
        viewedCount += qty;
    }
}

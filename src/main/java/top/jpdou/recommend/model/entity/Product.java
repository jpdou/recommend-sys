package top.jpdou.recommend.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Product {

    private Integer id;
    @Id
    private String sku;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
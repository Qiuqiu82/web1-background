package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@TableName("coscart")
public class CoscartEntity implements Serializable {
    @TableId
    private Long id;
    private Long userId;
    private String userTable;
    private Long productId;
    private String productName;
    private String productCover;
    private String specs;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private Integer checked;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserTable() { return userTable; }
    public void setUserTable(String userTable) { this.userTable = userTable; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getProductCover() { return productCover; }
    public void setProductCover(String productCover) { this.productCover = productCover; }
    public String getSpecs() { return specs; }
    public void setSpecs(String specs) { this.specs = specs; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Integer getChecked() { return checked; }
    public void setChecked(Integer checked) { this.checked = checked; }
    public Date getAddtime() { return addtime; }
    public void setAddtime(Date addtime) { this.addtime = addtime; }
}

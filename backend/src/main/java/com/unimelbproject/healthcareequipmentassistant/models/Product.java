package com.unimelbproject.healthcareequipmentassistant.models;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@Table(name = "product")

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @Column(name = "parent_sku")
    private String parentSku;

    @Column(name = "parent_id")
    private Long parentId;

    //@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String type;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "tax_status")
    private String taxStatus;

    @Column(name = "tax_class")
    private String taxClass;

    @Column(name = "in_stock")
    private Boolean inStock;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "weight_kg", precision = 10, scale = 3)
    private BigDecimal weightKg;

    @Column(name = "length_cm", precision = 10, scale = 3)
    private BigDecimal lengthCm;

    @Column(name = "width_cm", precision = 10, scale = 3)
    private BigDecimal widthCm;

    @Column(name = "height_cm", precision = 10, scale = 3)
    private BigDecimal heightCm;

    @Column(name = "shipping_class")
    private String shippingClass;

    private String brands;

    @Column(columnDefinition = "TEXT")
    private String images;

    // Attribute 1
    @Column(name = "Attribute 1 name")
    private String attribute1Name;

    @Column(name = "Attribute 1 value(s)", columnDefinition = "TEXT")
    private String attribute1Values;

    @Column(name = "Attribute 1 visible")
    private Boolean attribute1Visible;

    @Column(name = "Attribute 1 global")
    private Boolean attribute1Global;

    @Column(name = "Attribute 1 default")
    private String attribute1Default;

    // Attribute 2
    @Column(name = "Attribute 2 name")
    private String attribute2Name;

    @Column(name = "Attribute 2 value(s)", columnDefinition = "TEXT")
    private String attribute2Values;

    @Column(name = "Attribute 2 visible")
    private Boolean attribute2Visible;

    @Column(name = "Attribute 2 global")
    private Boolean attribute2Global;

    // Attribute 3
    @Column(name = "Attribute 3 name")
    private String attribute3Name;

    @Column(name = "Attribute 3 value(s)", columnDefinition = "TEXT")
    private String attribute3Values;

    @Column(name = "Attribute 3 visible")
    private Boolean attribute3Visible;

    @Column(name = "Attribute 3 global")
    private Boolean attribute3Global;

    /** Tiered prices for partner levels 1–4 */
    @Column(name = "price1", precision = 10, scale = 2)
    private BigDecimal price1;

    @Column(name = "price2", precision = 10, scale = 2)
    private BigDecimal price2;

    @Column(name = "price3", precision = 10, scale = 2)
    private BigDecimal price3;

    @Column(name = "price4", precision = 10, scale = 2)
    private BigDecimal price4;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParentSku(){
        return parentSku;
    }

    public void setParentSku(String parentSku) {
        this.parentSku = parentSku;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTaxStatus() {
        return taxStatus;
    }

    public void setTaxStatus(String taxStatus) {
        this.taxStatus = taxStatus;
    }

    public String getTaxClass() {
        return taxClass;
    }

    public void setTaxClass(String taxClass) {
        this.taxClass = taxClass;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getLengthCm() {
        return lengthCm;
    }

    public void setLengthCm(BigDecimal lengthCm) {
        this.lengthCm = lengthCm;
    }

    public BigDecimal getWidthCm() {
        return widthCm;
    }

    public void setWidthCm(BigDecimal widthCm) {
        this.widthCm = widthCm;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public String getShippingClass() {
        return shippingClass;
    }

    public void setShippingClass(String shippingClass) {
        this.shippingClass = shippingClass;
    }

    public String getBrands() {
        return brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public BigDecimal getPrice1() { return price1; }
    public void setPrice1(BigDecimal price1) { this.price1 = price1; }

    public BigDecimal getPrice2() { return price2; }
    public void setPrice2(BigDecimal price2) { this.price2 = price2; }

    public BigDecimal getPrice3() { return price3; }
    public void setPrice3(BigDecimal price3) { this.price3 = price3; }

    public BigDecimal getPrice4() { return price4; }
    public void setPrice4(BigDecimal price4) { this.price4 = price4; }


    public Boolean getAttribute1Global() {
        return attribute1Global;
    }

    public void setAttribute1Global(Boolean attribute1Global) {
        this.attribute1Global = attribute1Global;
    }

    public Boolean getAttribute1Visible() {
        return attribute1Visible;
    }

    public void setAttribute1Visible(Boolean attribute1Visible) {
        this.attribute1Visible = attribute1Visible;
    }

    public String getAttribute1Default() {
        return attribute1Default;
    }

    public void setAttribute1Default(String attribute1Default) {
        this.attribute1Default = attribute1Default;
    }

    public String getAttribute1Name() {
        return attribute1Name;
    }

    public void setAttribute1Name(String attribute1Name) {
        this.attribute1Name = attribute1Name;
    }

    public String getAttribute1Values() {
        return attribute1Values;
    }

    public void setAttribute1Values(String attribute1Values) {
        this.attribute1Values = attribute1Values;
    }

    public Boolean getAttribute2Global() {
        return attribute2Global;
    }

    public void setAttribute2Global(Boolean attribute2Global) {
        this.attribute2Global = attribute2Global;
    }

    public Boolean getAttribute2Visible() {
        return attribute2Visible;
    }

    public void setAttribute2Visible(Boolean attribute2Visible) {
        this.attribute2Visible = attribute2Visible;
    }

    public String getAttribute2Name() {
        return attribute2Name;
    }

    public void setAttribute2Name(String attribute2Name) {
        this.attribute2Name = attribute2Name;
    }

    public String getAttribute2Values() {
        return attribute2Values;
    }

    public void setAttribute2Values(String attribute2Values) {
        this.attribute2Values = attribute2Values;
    }

    public Boolean getAttribute3Global() {
        return attribute3Global;
    }

    public void setAttribute3Global(Boolean attribute3Global) {
        this.attribute3Global = attribute3Global;
    }

    public Boolean getAttribute3Visible() {
        return attribute3Visible;
    }

    public void setAttribute3Visible(Boolean attribute3Visible) {
        this.attribute3Visible = attribute3Visible;
    }

    public String getAttribute3Name() {
        return attribute3Name;
    }

    public void setAttribute3Name(String attribute3Name) {
        this.attribute3Name = attribute3Name;
    }

    public String getAttribute3Values() {
        return attribute3Values;
    }

    public void setAttribute3Values(String attribute3Values) {
        this.attribute3Values = attribute3Values;
    }
}

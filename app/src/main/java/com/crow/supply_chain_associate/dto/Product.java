package com.crow.supply_chain_associate.dto;

public class Product {
    private String name;
    private String description;
    private float price;
    private String barCode;
    private float reorderQty;
    private float packedHeight;
    private float packedWeight;
    private float packedWidth;
    private float packedDepth;
    private Category category;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }


    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public float getReorderQty() {
        return reorderQty;
    }

    public void setReorderQty(float reorderQty) {
        this.reorderQty = reorderQty;
    }

    public float getPackedHeight() {
        return packedHeight;
    }

    public void setPackedHeight(float packedHeight) {
        this.packedHeight = packedHeight;
    }

    public float getPackedWeight() {
        return packedWeight;
    }

    public void setPackedWeight(float packedWeight) {
        this.packedWeight = packedWeight;
    }

    public float getPackedWidth() {
        return packedWidth;
    }

    public void setPackedWidth(float packedWidth) {
        this.packedWidth = packedWidth;
    }

    public float getPackedDepth() {
        return packedDepth;
    }

    public void setPackedDepth(float packedDepth) {
        this.packedDepth = packedDepth;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}

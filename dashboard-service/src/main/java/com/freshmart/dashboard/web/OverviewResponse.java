package com.freshmart.dashboard.web;

import java.time.LocalDateTime;
import java.util.List;

public class OverviewResponse {
    private String shopName;
    private String currency;
    private double taxRate;
    private int customerCount;
    private int productCount;
    private int orderCount;
    private long revenue;
    private List<ProductView> lowStock;
    private List<OrderView> recentOrders;
    private String instanceNote;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public int getCustomerCount() {
        return customerCount;
    }

    public void setCustomerCount(int customerCount) {
        this.customerCount = customerCount;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public long getRevenue() {
        return revenue;
    }

    public void setRevenue(long revenue) {
        this.revenue = revenue;
    }

    public List<ProductView> getLowStock() {
        return lowStock;
    }

    public void setLowStock(List<ProductView> lowStock) {
        this.lowStock = lowStock;
    }

    public List<OrderView> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<OrderView> recentOrders) {
        this.recentOrders = recentOrders;
    }

    public String getInstanceNote() {
        return instanceNote;
    }

    public void setInstanceNote(String instanceNote) {
        this.instanceNote = instanceNote;
    }

    public static class ProductView {
        private Long id;
        private String name;
        private String category;
        private Long price;
        private Integer stock;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Long getPrice() {
            return price;
        }

        public void setPrice(Long price) {
            this.price = price;
        }

        public Integer getStock() {
            return stock;
        }

        public void setStock(Integer stock) {
            this.stock = stock;
        }
    }

    public static class OrderView {
        private Long id;
        private String customerName;
        private String productName;
        private Integer quantity;
        private Long totalAmount;
        private String status;
        private LocalDateTime createdAt;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Long getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Long totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
    }
}

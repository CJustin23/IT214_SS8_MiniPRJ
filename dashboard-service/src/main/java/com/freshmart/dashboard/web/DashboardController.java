package com.freshmart.dashboard.web;

import com.freshmart.dashboard.client.OrderClient;
import com.freshmart.dashboard.client.ProductClient;
import com.freshmart.dashboard.client.UserClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final UserClient userClient;
    private final ProductClient productClient;
    private final OrderClient orderClient;

    @Value("${app.shop-name:FreshMart}")
    private String shopName;

    @Value("${app.currency:VND}")
    private String currency;

    @Value("${app.tax-rate:0}")
    private double taxRate;

    @Value("${server.port:8084}")
    private String port;

    public DashboardController(UserClient userClient, ProductClient productClient, OrderClient orderClient) {
        this.userClient = userClient;
        this.productClient = productClient;
        this.orderClient = orderClient;
    }

    @GetMapping("/overview")
    public OverviewResponse overview() {
        List<UserClient.CustomerDto> customers = userClient.findAll();
        List<ProductClient.ProductDto> products = productClient.findAll();
        List<OrderClient.OrderDto> orders = orderClient.findAll();

        Map<Long, String> customerNames = customers.stream()
                .collect(Collectors.toMap(UserClient.CustomerDto::getId, UserClient.CustomerDto::getFullName));
        Map<Long, String> productNames = products.stream()
                .collect(Collectors.toMap(ProductClient.ProductDto::getId, ProductClient.ProductDto::getName));

        OverviewResponse response = new OverviewResponse();
        response.setShopName(shopName);
        response.setCurrency(currency);
        response.setTaxRate(taxRate);
        response.setCustomerCount(customers.size());
        response.setProductCount(products.size());
        response.setOrderCount(orders.size());
        response.setRevenue(orders.stream().mapToLong(o -> o.getTotalAmount() == null ? 0 : o.getTotalAmount()).sum());
        response.setLowStock(products.stream()
                .filter(p -> p.getStock() != null && p.getStock() <= 10)
                .map(this::toProductView)
                .toList());
        response.setRecentOrders(orders.stream()
                .sorted(Comparator.comparing(OrderClient.OrderDto::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(8)
                .map(order -> toOrderView(order, customerNames, productNames))
                .toList());
        response.setInstanceNote(hostLabel());
        return response;
    }

    private OverviewResponse.ProductView toProductView(ProductClient.ProductDto product) {
        OverviewResponse.ProductView view = new OverviewResponse.ProductView();
        view.setId(product.getId());
        view.setName(product.getName());
        view.setCategory(product.getCategory());
        view.setPrice(product.getPrice());
        view.setStock(product.getStock());
        return view;
    }

    private OverviewResponse.OrderView toOrderView(OrderClient.OrderDto order,
                                                   Map<Long, String> customerNames,
                                                   Map<Long, String> productNames) {
        OverviewResponse.OrderView view = new OverviewResponse.OrderView();
        view.setId(order.getId());
        view.setCustomerName(customerNames.getOrDefault(order.getCustomerId(), "KH #" + order.getCustomerId()));
        view.setProductName(productNames.getOrDefault(order.getProductId(), "SP #" + order.getProductId()));
        view.setQuantity(order.getQuantity());
        view.setTotalAmount(order.getTotalAmount());
        view.setStatus(order.getStatus());
        view.setCreatedAt(order.getCreatedAt());
        return view;
    }

    private String hostLabel() {
        try {
            return InetAddress.getLocalHost().getHostName() + ":" + port;
        } catch (Exception ex) {
            return "dashboard:" + port;
        }
    }
}

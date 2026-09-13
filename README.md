# FreshMart — Mini Project Microservices (IT214)

Domain: **cửa hàng thực phẩm mini FreshMart**. Phạm vi kỹ thuật chỉ dùng Session 02, 03, 05, 06: Database-per-service, Config Server, Eureka, API Gateway, Load Balancing, FeignClient.

## Kiến trúc

| Thành phần | Port | Vai trò |
|---|---|---|
| config-server | 8888 | Cấu hình tập trung (native classpath) |
| eureka-server | 8761 | Service Registry / Discovery |
| api-gateway | 8080 | Điểm truy cập duy nhất + UI tổng quan |
| user-service | 8081 | Khách hàng — DB `userdb` |
| product-service | 8082 | Sản phẩm / tồn kho — DB `productdb` |
| order-service | 8083 | Đơn hàng — DB `orderdb` |
| dashboard-service | 8084 | Tổng hợp 1 màn hình (Feign đồng bộ) |

Client (trình duyệt) **chỉ** gọi `http://localhost:8080`. Gateway định tuyến `lb://...`. Feign gọi theo **tên service** trên Eureka, không hard-code host/port.

Yêu cầu “khoai” được giải bằng **dashboard-service** (composition service): một request `GET /api/dashboard/overview` gọi user + product + order rồi gộp JSON trả về.

```
Browser  →  API Gateway :8080  →  dashboard-service
                                      ├─ Feign → user-service     (H2 userdb)
                                      ├─ Feign → product-service  (H2 productdb)
                                      └─ Feign → order-service    (H2 orderdb)
                                                   ├─ Feign → user-service
                                                   └─ Feign → product-service
```

## Chạy dự án

Cần **JDK 17+** và **Maven**.

```powershell
cd C:\Users\Windows\Downloads\IT214_Session08
mvn -q clean package -DskipTests
.\run-all.ps1
```

Thứ tự trong script: Config Server → Eureka → các service nghiệp vụ → Gateway.

- UI tổng quan: http://localhost:8080
- Eureka: http://localhost:8761
- Config (ví dụ): http://localhost:8888/user-service/default
- API: http://localhost:8080/api/dashboard/overview

Tạo thêm đơn (vẫn qua một địa chỉ):

```powershell
curl.exe -X POST http://localhost:8080/api/orders -H "Content-Type: application/json" -d "{\"customerId\":4,\"productId\":3,\"quantity\":2}"
```

### Nhân bản instance (không sửa code service khác)

Mở terminal mới, chạy thêm product-service ở cổng khác:

```powershell
mvn -pl product-service spring-boot:run "-Dspring-boot.run.arguments=--server.port=8182"
```

Eureka hiện 2 instance `product-service`. Gateway và Feign dùng `lb://product-service` + Spring Cloud LoadBalancer — **không** cần sửa cấu hình user/order/dashboard/gateway.

## Trả lời câu hỏi Mục 6 (để bảo vệ)

**1. Ai gộp dữ liệu khi tuân thủ Database-per-service?**  
Không phải client (client sẽ phải biết nhiều service — trái “một địa chỉ”). Không nhồi aggregation vào API Gateway (gateway chỉ routing/load-balance). Nhóm chọn **dashboard-service**: service chuyên composition, gọi đồng bộ các service nguồn rồi trả 1 payload.

**2. Gọi đồng bộ nhiều service thì sao nếu một service chậm?**  
Thời gian phản hồi ≈ tổng (hoặc max nếu song song; hiện tại Feign tuần tự nên gần bằng **tổng** latency). Một service timeout/down làm **cả màn hình tổng quan lỗi**. Đây là giới hạn đã ghi nhận: trong phạm vi Session 06 chưa dùng circuit breaker / timeout nâng cao / message queue.

**3. RestTemplate hay FeignClient?**  
**FeignClient.** Khai báo theo interface, gắn `name` Eureka, LoadBalancer tự gắn, code gọn hơn RestTemplate + `@LoadBalanced`. RestTemplate vẫn đúng Session 06 nhưng dài dòng hơn cho nhiều endpoint.

**4. Một địa chỉ duy nhất? Gateway biết service bằng cách nào?**  
**Spring Cloud Gateway** port 8080. `uri: lb://product-service` — LoadBalancer hỏi Eureka danh sách instance hiện tại, không hard-code IP.

**5. Eureka + Load Balancing khi thêm/bớt instance?**  
Service mới start → đăng ký Eureka → client (Gateway/Feign) fetch registry định kỳ → LoadBalancer chia tải. Gọi bằng **logical name**, không sửa YAML/code nơi khác.

**6. Đưa gì vào Config Server?**  
Port, datasource từng service, `eureka.client.service-url`, tham số nghiệp vụ `app.shop-name`, `app.tax-rate`. Đổi thuế/tên shop ở một file `application.yml` của config-server. Lưu ý lab: service đọc config lúc **khởi động**; muốn nhận ngay khi đang chạy thì cần `/actuator/refresh` (vẫn trong họ Spring Cloud Config). Không phải rebuild từng service khi đổi thông số.

## Ranh giới dữ liệu

- `user-service` không có bảng đơn/sản phẩm.
- `order-service` chỉ lưu `customerId`, `productId` — **không join** DB khác.
- `product-service` là nguồn sự thật của giá và tồn kho.
- Schema H2 mỗi service độc lập; đổi cột `customers` không bắt `orderdb` sửa.

## Ngoài phạm vi (cố ý không làm)

Message queue, Saga, CQRS, circuit breaker, JWT/OAuth. Khi tạo đơn, order-service gọi Feign trừ kho rồi lưu đơn — nếu bước sau lỗi có thể lệch dữ liệu; nhóm ghi nhận đây là hạn chế của giao tiếp đồng bộ thuần REST trong phạm vi đã học.

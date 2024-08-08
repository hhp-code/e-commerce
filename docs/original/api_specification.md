# E-커머스 API 명세

## 1. 개요

상품 조회, 주문, 결제, 잔액 관리, 인기 상품 추천 기능, 쿠폰 관리에 대한 API 명세입니다.

### 1.1 기본 URL
```
https://api.ecommerce-example.com
```

### 1.2 인증
모든 API 요청에는 Authorization 헤더에 Bearer 토큰이 필요합니다.

### 1.3 공통 응답 형식
```json
{
  "success": "boolean",
  "message": "string",
  "data": "object"
}
```

### 1.4 공통 에러 코드
- 400: 잘못된 요청
- 401: 인증 실패
- 403: 권한 없음
- 404: 리소스 없음
- 500: 서버 내부 오류

## 2. API 엔드포인트

### 2.1 잔액 관리
#### 2.1.1 사용자 정보 조회

- **Endpoint**: GET /users/{id}
- **Authorization**: Bearer Token 필요
- **Response**:
```json
{
  "id": "long",
  "username": "string",
  "balance": "bigdecimal",
  "isDeleted": "boolean",
  "createdAt": "localdatetime"
}
```
#### 2.1.2 사용자 잔액 충전

- **Endpoint**: POST /balance/{id}/charge
- **Authorization**: Bearer Token 필요
- **Request Body**:
```json
{
  "amount": "bigdecimal"
}
```
- **Response**:
```json
{
  "id": "long",
  "balance": "bigdecimal"
}
```
- **Error**:
    - 400 Bad Request: 충전 금액이 유효하지 않은 경우
      ```json
      {
        "success": false,
        "message": "충전 금액은 1,000원 이상 1,000,000원 이하여야 합니다.",
        "errorCode": "INVALID_CHARGE_AMOUNT"
      }
      ```
    - 401 Unauthorized: 인증 실패
    - 500 Internal Server Error: 서버 오류

#### 2.1.3 잔액 조회

- **Endpoint**: GET /balance/{userId}
- **Authorization**: Bearer Token 필요
- **Path Parameters**:
    - userId: long
- **Response**:
    - Status Code: 200 OK
    - Body:
      ```json
      {
        "success": true,
        "message": "잔액 조회 성공",
        "data": {
          "userId": "long",
          "balance": "bigdecimal",
          "lastUpdated": "datetime"
        }
      }
      ```
- **Error**:
    - 401 Unauthorized: 인증 실패
    - 404 Not Found: 사용자를 찾을 수 없음
    - 500 Internal Server Error: 서버 오류

### 2.2  상품 조회

#### 2.2.1 상품 목록 조회

- **Endpoint**: GET /products
- **Authorization**: Bearer Token 필요
- **Response**:
```json
{
  "products": [
    {
      "id": "long",
      "name": "string",
      "price": "bigdecimal",
      "availableStock": "integer",
      "reservedStock": "integer",
      "lastUpdated": "localdatetime",
      "isDeleted": "boolean",
      "createdAt": "localdatetime"
    }
  ]
}
```
#### 2.2.2 인기 상품 목록 조회

- **Endpoint**: GET /products/popular
- **Authorization**: Bearer Token 필요
- **Response**:
    - Status Code: 200 OK
    - Body:
      ```json
      {
        "success": true,
        "message": "인기 상품 조회 성공",
        "data": {
          "popularProducts": [
            {
              "id": "long",
              "name": "string",
              "price": "bigdecimal",
              "salesCount": "int"
            }
          ],
          "lastUpdated": "datetime"
        }
      }
      ```
- **Error**:
    - 401 Unauthorized: 인증 실패
    - 500 Internal Server Error: 서버 오류
#### 2.2.3 상품 상세 조회

- **Endpoint**: GET /products/{id}
- **Authorization**: Bearer Token 필요
- **Response**:
```json
{
  "id": "long",
  "name": "string",
  "price": "bigdecimal",
  "availableStock": "integer",
  "reservedStock": "integer",
  "lastUpdated": "localdatetime",
  "isDeleted": "boolean",
  "createdAt": "localdatetime"
}
```
### 2.3 장바구니 관리
#### 2.3.1 장바구니 조회

- **Endpoint**: GET /carts/{id}
- **Authorization**: Bearer Token 필요
- **Response**:
```json
{
  "id": "long",
  "lastUpdated": "localdatetime",
  "expirationDate": "localdatetime",
  "items": [
    {
      "id": "long",
      "quantity": "integer",
      "addedDate": "localdatetime",
      "product": {
        "id": "long",
        "name": "string",
        "price": "bigdecimal"
      }
    }
  ]
}
```
- **Error**:
  - 401 Unauthorized: 인증 실패
  - 404 Not Found: 사용자를 찾을 수 없음
  - 500 Internal Server Error: 서버 오류

#### 2.3.2 장바구니에 상품 추가

- **Endpoint**: POST /carts/{cartId}/items
- **Authorization**: Bearer Token 필요
- **Request Body**:
```json
{
  "productId": "long",
  "quantity": "integer"
}
```
- **Response**:
```json
{
  "id": "long",
  "quantity": "integer",
  "addedDate": "localdatetime",
  "product": {
    "id": "long",
    "name": "string",
    "price": "bigdecimal"
  }
}
```
- **Error**:
    - 400 Bad Request: 상품 수량이 유효하지 않은 경우
      ```json
      {
        "success": false,
        "message": "상품 수량은 1개 이상 10개 이하여야 합니다.",
        "errorCode": "INVALID_PRODUCT_QUANTITY"
      }
      ```
    - 401 Unauthorized: 인증 실패
    - 404 Not Found: 상품을 찾을 수 없음
    - 500 Internal Server Error: 서버 오류

#### 2.3.3 장바구니에서 상품 삭제

- **Endpoint**: DELETE /cart/items/{productId}
- **Authorization**: Bearer Token 필요
- **Path Parameters**:
    - productId: long
- **Query Parameters**:
    - userId: long
- **Response**:
    - Status Code: 200 OK
    - Body:
      ```json
      {
        "success": true,
        "message": "상품이 장바구니에서 삭제되었습니다.",
        "data": {
          "cartId": "long",
          "totalItems": "int"
        }
      }
      ```
- **Error**:
    - 401 Unauthorized: 인증 실패
    - 404 Not Found: 상품을 찾을 수 없음
    - 500 Internal Server Error: 서버 오류



#### 2.3.4 장바구니 상품 수량 변경

- **Endpoint**: PATCH /cart/items/{productId}
- **Authorization**: Bearer Token 필요
- **Path Parameters**:
    - productId: long
- **Request Body**:
  ```json
  {
    "userId": "long",
    "quantity": "int"
  }
  ```
- **Response**:
    - Status Code: 200 OK
    - Body:
      ```json
      {
        "success": true,
        "message": "상품 수량이 변경되었습니다.",
        "data": {
          "productId": "long",
          "newQuantity": "int",
          "totalAmount": "bigdecimal"
        }
      }
      ```
- **Error**:
    - 400 Bad Request: 상품 수량이 유효하지 않은 경우
    - 401 Unauthorized: 인증 실패
    - 404 Not Found: 상품을 찾을 수 없음
    - 500 Internal Server Error: 서버 오류
### 2.4 주문 관리

#### 2.4.1 주문 생성

- **Endpoint**: POST /orders
- **Authorization**: Bearer Token 필요
- **Request Body**:
```json
{
  "userId": "long",
  "items": [
    {
      "productId": "long",
      "productName": "string",
      "quantity": "integer",
      "price": "bigdecimal"
    }
  ]
}
```
- **Response**:
```json
{
  "id": "long",
  "orderDate": "localdatetime",
  "regularPrice": "bigdecimal",
  "salePrice": "bigdecimal",
  "sellingPrice": "bigdecimal",
  "status": "string",
  "isDeleted": "boolean",
  "deletedAt": "localdatetime",
  "items": [
    {
      "id": "long",
      "quantity": "integer",
      "price": "bigdecimal"
    }
  ]
}
```

#### 2.4.2 주문 상세 조회

- **Endpoint**: GET /orders/{id}
- **Authorization**: Bearer Token 필요
- **Response**:
```json
{
  "id": "long",
  "orderDate": "localdatetime",
  "regularPrice": "bigdecimal",
  "salePrice": "bigdecimal",
  "sellingPrice": "bigdecimal",
  "status": "string",
  "isDeleted": "boolean",
  "deletedAt": "localdatetime",
  "items": [
    {
      "id": "long",
      "quantity": "integer",
      "price": "bigdecimal",
      "product": {
        "id": "long",
        "name": "string"
      }
    }
  ]
}
```
#### 2.5.1 쿠폰 생성

- **Endpoint**: POST /coupons
- **Authorization**: Bearer Token 필요
- **Request Body**:
```json
{
  "code": "string",
  "discountAmount": "bigdecimal",
  "remainingQuantity": "integer",
  "validFrom": "localdatetime",
  "validTo": "localdatetime",
  "isActive": "boolean"
}
```
- **Response**:
```json
{
  "id": "long",
  "code": "string",
  "discountAmount": "bigdecimal",
  "remainingQuantity": "integer",
  "validFrom": "localdatetime",
  "validTo": "localdatetime",
  "isActive": "boolean"
}
```
#### 2.5.2 사용자 쿠폰 발급

- **Endpoint**: POST /users/{userId}/coupons
- **Authorization**: Bearer Token 필요
- **Request Body**:
```json
{
  "couponId": "long"
}
```
- **Response**:
```json
{
  "id": "long",
  "isUsed": "boolean",
  "usedAt": "localdatetime",
  "coupon": {
    "id": "long",
    "code": "string",
    "discountAmount": "bigdecimal"
  }
}
```
### 2.5.3 쿠폰 발급 요청

- **Endpoint**: POST /coupons/{couponId}/issue
- **Authorization**: Bearer Token 필요
- **Description**: 사용자가 특정 쿠폰의 발급을 요청합니다.
- **Path Parameters**:
  - couponId: long
- **Response**:

```json
{
  "success": "boolean",
  "message": "string",
  "userCouponId": "long"
}
```

### 2.5.4 사용자 쿠폰 조회

- **Endpoint**: GET /users/{userId}/coupons
- **Authorization**: Bearer Token 필요
- **Description**: 특정 사용자의 발급받은 쿠폰 목록을 조회합니다.
- **Path Parameters**:
  - userId: long
- **Response**:

```json
{
  "coupons": [
    {
      "id": "long",
      "couponId": "long",
      "couponName": "string",
      "discountAmount": "number",
      "issuedAt": "datetime",
      "expiresAt": "datetime",
      "isUsed": "boolean"
    }
  ]
}
```

### 2.5.5 쿠폰 사용

- **Endpoint**: POST /users/{userId}/coupons/{userCouponId}/use
- **Authorization**: Bearer Token 필요
- **Description**: 사용자가 발급받은 쿠폰을 사용합니다.
- **Path Parameters**:
  - userId: long
  - userCouponId: long
- **Response**:

```json
{
  "success": "boolean",
  "message": "string"
}
```

### 2.5.6 쿠폰 정보 조회 

- **Endpoint**: GET /coupons/{couponId}
- **Authorization**: Bearer Token 필요
- **Description**: 특정 쿠폰의 상세 정보를 조회합니다.
- **Path Parameters**:
  - couponId: long
- **Response**:

```json
{
  "id": "long",
  "name": "string",
  "code": "string",
  "discountAmount": "number",
  "quantity": "number",
  "remainingQuantity": "number",
  "startDateTime": "datetime",
  "endDateTime": "datetime",
  "createdAt": "datetime",
  "issuedCount": "number"
}
```













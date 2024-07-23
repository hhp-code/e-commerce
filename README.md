# 이커머스 프로젝트
## 0. 최신 브랜치
for-review 브랜치에 최신 코드가 업데이트되어있으니, 참조해주시면 감사하겠습니다.
## 1. 프로젝트 소개
통상적인 이커머스 시스템에서 발생할수 있는 문제상황들을 상정하고 그에대한 아아키텍처적 고민과 함께, 이를 해결하기 위한 코드를 작성하는 프로젝트입니다.
## 2. 프로젝트 구조
```angular2html
api
    ├── controller
        ├── domain
            ├── coupon
                ├── dto
                CouponController
            ├── order
                ├── dto
                OrderController
            ├── product
                ├── dto
                Productcontroller
            ├── user
                ├── dto
                UserCouponController
                UserPointController
                UserController
        ├── usecase
            coupon
            order
            product
            user
    ├── exception
    ├── filter
    ├── scheduler
        CouponQueueManager
config
domain
├── coupon
    ├── repository
    ├── service
        ├── repository
├── order
    ├── repository
    ├── service
        ├── repository
├── product
    ├── repository
    ├── service
        ├── repository
├── user
    ├── repository
    ├── service
        ├── repository

```
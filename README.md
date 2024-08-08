### 스텝 15
- DB : Mysql 8.4
- 주요 성능향상이 야기되는 쿼리를 산정 후, 인덱스 사용 과 사용하지 않는것을 분석함으로 효율적인 데이터 판단.
- 주문데이터 1만건, 사용자데이터 2만건, 상품데이터 2만건, 쿠폰데이터 1만건씩 생성 인입.

1. Order 엔티티에 대한 인덱싱 성능 검증
```angular2html
OrderStatus 에 대한 인덱스 검증 결과 

type:
적용 전: ALL 적용 후: ref
테이블 전체 스캔(ALL)에서 인덱스를 사용한 참조(ref)로 변경되었음을 의미합니다.

possible_keys와 key:
적용 전: null 적용 후: idx_order_status
인덱스 적용 후 사용 가능한 키와 실제 사용된 키가 idx_order_status로 변경되었습니다.

key_len:
적용 전: null적용 후: 1023
인덱스 키의 길이가 지정되었습니다.
ref:
적용 전: null 적용 후: const
인덱스를 사용하여 상수 값과 비교하고 있음을 나타냅니다.

rows:
적용 전: 9492 적용 후: 2357
검사해야 할 예상 행의 수가 크게 감소했습니다.

filtered:
적용 전: 10.0 적용 후: 100.0
필터링된 행의 비율이 증가했습니다.

Extra:
적용 전: Using where 적용 후: null
WHERE 조건을 사용한 추가 필터링이 필요 없어졌습니다.
```
2. User 엔티티에 대한 인덱싱 성능 검증
```angular2html
UserName 에 대한 인덱스 검증 결과
type:
적용 전: ALL 적용 후: ref
테이블 전체 스캔(ALL)에서 인덱스를 사용한 참조(ref)로 변경되었음을 의미합니다.

possible_keys와 key:
적용 전: null 적용 후: idx_username
인덱스 적용 후 사용 가능한 키와 실제 사용된 키가 idx_username으로 변경되었습니다.

key_len:
적용 전: null 적용 후: 1023
인덱스 키의 길이가 지정되었습니다.

ref:
적용 전: null 적용 후: const
인덱스를 사용하여 상수 값과 비교하고 있음을 나타냅니다.

rows:
적용 전: 19878 적용 후: 1
검사해야 할 예상 행의 수가 크게 감소했습니다.

filtered:
적용 전: 10.0 적용 후: 100.0
필터링된 행의 비율이 증가했습니다.

Extra:
적용 전: Using where 적용 후: null
WHERE 조건을 사용한 추가 필터링이 필요 없어졌습니다.
```
3. Product 엔티티에 대한 인덱싱 성능 검증
```angular2html
Product의 name 에 대한 인덱스 검증 결과

1. type:
- 적용 전: ALL 적용 후: ref
인덱스 적용 후, 테이블 전체 스캔(ALL)에서 인덱스를 사용한 참조(ref)로 변경되었습니다. 

2. possible_keys와 key:
- 적용 전: null 적용 후: idx_product_name
인덱스 적용 후, 사용 가능한 키와 실제 사용된 키가 idx_product_name으로 변경되었습니다.

3. key_len:
- 적용 전: null 적용 후: 1023
인덱스 키의 길이가 지정되었습니다. 이는 VARCHAR 필드의 최대 길이를 나타냅니다.

4. ref:
- 적용 전: null 적용 후: const
인덱스를 사용하여 상수 값과 비교하고 있음을 나타냅니다.

5. rows:
- 적용 전: 19418 적용 후: 1
검사해야 할 예상 행의 수가 크게 감소했습니다. 

6. filtered:
- 적용 전: 10.0 적용 후: 100.0
필터링된 행의 비율이 증가했습니다.

Extra:
- 적용 전: Using where 적용 후: null
WHERE 조건을 사용한 추가 필터링이 필요 없어졌습니다.

```
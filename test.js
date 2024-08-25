import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080/api';

export const options = {
    scenarios: {
        constant_load: {
            executor: 'constant-arrival-rate',
            rate: 200,
            timeUnit: '1s',
            duration: '5m',
            preAllocatedVUs: 100,
            maxVUs: 300,
        },
    }
};
export default function () {
    const createOrderPayload = JSON.stringify({
        userId: 1,
        items: {
            1: 1
        },
    });
    const createOrderResponse = http.post(`${BASE_URL}/orders`, createOrderPayload, {
        headers: { 'Content-Type': 'application/json' },
    });
    check(createOrderResponse, { '주문 생성 성공': (r) => r.status === 200 });

    if (createOrderResponse.status !== 200) {
        console.log(`주문 생성 실패: ${createOrderResponse.status}, ${createOrderResponse.body}`);
        return;  // 이후 단계 실행 중지
    }

    const orderId = JSON.parse(createOrderResponse.body).id;
    const userId = JSON.parse(createOrderResponse.body).userId;



    const getOrderResponse = http.get(`${BASE_URL}/orders/${orderId}`);

    check(getOrderResponse, {
        '주문 조회 성공': (r) => r.status === 200,
    });
    const payOrderPayload = JSON.stringify({
        userId : userId,
        orderId: orderId,
    });

    const payOrderResponse = http.post(`${BASE_URL}/orders/payments`, payOrderPayload, {
        headers: { 'Content-Type': 'application/json' },
    });

    check(payOrderResponse, {
        '주문 결제 성공': (r) => r.status === 200,
    });

    sleep(1);



    // 주문 목록 조회
    const listOrdersPayload = JSON.stringify({
        customerId: 1
    });
    let res = http.post(`${BASE_URL}/orders/list`, listOrdersPayload, {
        headers: { 'Content-Type': 'application/json' }
    });
    check(res, { '주문 목록 조회 성공': (r) => r.status === 200 });
    sleep(1);


}
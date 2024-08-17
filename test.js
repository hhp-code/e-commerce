import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080/api';

export const options = {
    stages: [
        { duration: '30s', target: 10 },
        { duration: '1m', target: 50 },
        { duration: '2m', target: 50 },
        { duration: '30s', target: 0 },
    ],
};

export default function () {

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
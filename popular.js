import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://localhost:8080/api';

export const options = {
    scenarios: {
        load_test: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '1m', target: 50 },
                { duration: '3m', target: 50 },
                { duration: '1m', target: 0 },
            ],
            gracefulRampDown: '30s',
            startTime: '0s',
        },
        endurance_test: {
            executor: 'constant-vus',
            vus: 30,
            duration: '30m',
            startTime: '5m30s',
        },
        stress_test: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '2m', target: 100 },
                { duration: '5m', target: 100 },
                { duration: '2m', target: 200 },
                { duration: '5m', target: 200 },
                { duration: '2m', target: 0 },
            ],
            gracefulRampDown: '30s',
            startTime: '35m30s',
        },
        spike_test: {
            executor: 'ramping-arrival-rate',
            startRate: 50,
            timeUnit: '1s',
            preAllocatedVUs: 50,
            maxVUs: 500,
            stages: [
                { duration: '1m', target: 50 },
                { duration: '30s', target: 500 },
                { duration: '1m', target: 50 },
            ],
            startTime: '51m30s',
        },
    },
};
export default function () {
    //인기상품 조회
    const res = http.get(`${BASE_URL}/products/popular`);
    check(res, { '인기상품 조회 성공': (r) => r.status === 200 });
}
package com.ecommerce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce",
        "spring.datasource.username=admin",
        "spring.datasource.password=admin"
})
public class UserIndexTest {

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    void testUsernameIndexPerformance() {
        // 인덱스 적용 전 쿼리 실행 계획
        List<Map<String, Object>> beforeIndex = getQueryPlan(
                "SELECT * FROM users WHERE username = 'testuser'"
        );
        System.out.println("쿼리 실행 계획 (username 인덱스 적용 전):");
        printQueryPlan(beforeIndex);

        // 인덱스 생성
        jdbcTemplate.execute("CREATE INDEX idx_username ON users (username)");

        // 인덱스 적용 후 쿼리 실행 계획
        List<Map<String, Object>> afterIndex = getQueryPlan(
                "SELECT * FROM users WHERE username = 'testuser'"
        );
        System.out.println("쿼리 실행 계획 (username 인덱스 적용 후):");
        printQueryPlan(afterIndex);

        // 인덱스 삭제
        jdbcTemplate.execute("DROP INDEX idx_username ON users");

        // 인덱스 적용 후 성능 향상 검증
        assertTrue(isPerformanceImproved(beforeIndex, afterIndex));
    }

    private List<Map<String, Object>> getQueryPlan(String sql) {
        return jdbcTemplate.queryForList("EXPLAIN " + sql);
    }

    private void printQueryPlan(List<Map<String, Object>> queryPlan) {
        for (Map<String, Object> row : queryPlan) {
            System.out.println(row);
        }
    }

    private boolean isPerformanceImproved(List<Map<String, Object>> before, List<Map<String, Object>> after) {
        BigInteger beforeRows = (BigInteger) before.get(0).get("rows");
        BigInteger afterRows = (BigInteger) after.get(0).get("rows");
        return afterRows.compareTo(beforeRows) < 0;
    }
}

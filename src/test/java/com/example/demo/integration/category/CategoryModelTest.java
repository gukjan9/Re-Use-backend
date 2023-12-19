package com.example.demo.integration.category;

import com.example.demo.category.dto.CategoryBundleResponseDto;
import com.example.demo.category.service.CategoryService;
import com.example.demo.utils.LoadEnvironmentVariables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest
@LoadEnvironmentVariables
public class CategoryModelTest {
    @Autowired
    private CategoryService categoryService;

    @Retention(RetentionPolicy.RUNTIME)
    @SqlGroup({
            @Sql({
                    "classpath:data/testcase-category.sql"
            }),
            @Sql(
                    scripts = "classpath:truncate-testcases.sql",
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
            )
    })
    @interface LoadTestCaseCategory {}

    @LoadTestCaseCategory
    @Test
    @DisplayName("[정상 작동] readCategoryRecursively")
    void readCategoryRecursively() {
        // given

        // when
        ResponseEntity<List<CategoryBundleResponseDto>> result = categoryService.readCategoryRecursively();

        // then
        for (CategoryBundleResponseDto dto : result.getBody()) {
            if(dto.getLargeCategoryId() == 1) {
                assertThat(dto.getChildren())
                        .hasSize(2)
                        .allSatisfy(child -> {
                            assertThat(child.getParentId()).isEqualTo(1);
                            assertThat(child.getMiddleCategoryName()).isIn("man shirt", "man pants");
                        });

            } else if (dto.getLargeCategoryId() == 2) {
                assertThat(dto.getChildren())
                        .hasSize(2)
                        .allSatisfy(child -> {
                            assertThat(child.getParentId()).isEqualTo(2);
                            assertThat(child.getMiddleCategoryName()).isIn("woman shirt", "woman pants");
                        });

            }
        }
    }
}

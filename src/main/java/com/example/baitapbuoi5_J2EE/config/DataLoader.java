package com.example.baitapbuoi5_J2EE.config;

import com.example.baitapbuoi5_J2EE.model.Category;
import com.example.baitapbuoi5_J2EE.service.CategoryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner loadCategories(CategoryService categoryService) {
        return args -> {
            if (categoryService.getAllCategories().isEmpty()) {
                List<String> names = List.of(
                    "Điện tử",
                    "Thời trang",
                    "Đồ gia dụng",
                    "Thực phẩm",
                    "Sách & Văn phòng phẩm",
                    "Thể thao & Du lịch",
                    "Mỹ phẩm & Sức khỏe"
                );
                for (String name : names) {
                    Category c = new Category();
                    c.setName(name);
                    categoryService.saveCategory(c);
                }
            }
        };
    }
}

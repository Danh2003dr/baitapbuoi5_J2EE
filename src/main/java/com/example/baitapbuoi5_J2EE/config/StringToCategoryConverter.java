package com.example.baitapbuoi5_J2EE.config;

import com.example.baitapbuoi5_J2EE.model.Category;
import com.example.baitapbuoi5_J2EE.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToCategoryConverter implements Converter<String, Category> {

    @Autowired
    private CategoryService categoryService;

    @Override
    public Category convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        try {
            Integer id = Integer.parseInt(source.trim());
            return categoryService.getCategoryById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

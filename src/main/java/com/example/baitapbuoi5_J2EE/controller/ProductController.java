package com.example.baitapbuoi5_J2EE.controller;

import com.example.baitapbuoi5_J2EE.model.Product;
import com.example.baitapbuoi5_J2EE.service.CategoryService;
import com.example.baitapbuoi5_J2EE.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "product/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/add";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") @Valid Product product,
                             BindingResult result,
                             Model model,
                             @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "product/add";
        }
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String originalName = imageFile.getOriginalFilename();
                if (originalName != null && originalName.contains("..")) {
                    originalName = originalName.replace("..", "");
                }
                if (originalName == null || originalName.isBlank()) {
                    originalName = "image";
                }
                String filename = UUID.randomUUID().toString() + "_" + originalName;
                Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
                if (!Files.exists(dir)) {
                    Files.createDirectories(dir);
                }
                Path filePath = dir.resolve(filename);
                imageFile.transferTo(filePath.toFile());
                product.setImage("/images/" + filename);
            } catch (IOException e) {
                // Nếu có URL ảnh thì vẫn lưu sản phẩm, không chặn
                String imageUrl = product.getImage();
                if (imageUrl == null || imageUrl.isBlank() || "https://example.com/image.jpg".equals(imageUrl.trim())) {
                    model.addAttribute("categories", categoryService.getAllCategories());
                    model.addAttribute("imageError", "Không thể lưu ảnh. Thử lại hoặc nhập URL.");
                    return "product/add";
                }
                // Giữ URL hiện tại, lưu sản phẩm bình thường
            }
        }
        // Nếu chỉ nhập URL (không upload file) thì giữ nguyên; nếu URL là placeholder thì xóa
        String img = product.getImage();
        if (img != null && ("https://example.com/image.jpg".equals(img.trim()) || img.isBlank())) {
            product.setImage(null);
        }
        productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "product/add";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id) {
        productService.deleteProduct(id);
        return "redirect:/products";
    }
}

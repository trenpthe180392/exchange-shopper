package com.exchangeshopper.controller.admin;

import com.exchangeshopper.entity.Product;
import com.exchangeshopper.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/admin/products")
public class ProductAdminController {

    @Autowired
    private ProductService productService;

    @GetMapping("/add")
    public String showAddForm() {
        return "admin/product-form";
    }

    @PostMapping("/add")
    public String addProduct(@RequestParam String name,
                             @RequestParam String description,
                             @RequestParam double price,
                             @RequestParam int quantity,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             RedirectAttributes ra) {
        try {
            // Đường dẫn thư mục lưu ảnh
            String uploadDir = "src/main/resources/static/images/products/";
            String filename = imageFile.getOriginalFilename();
            Path path = Paths.get(uploadDir + filename);

            // Tạo thư mục nếu chưa có
            Files.createDirectories(path.getParent());

            // Ghi file ảnh
            Files.write(path, imageFile.getBytes());

            // Tạo sản phẩm
            Product p = new Product();
            p.setName(name);
            p.setDescription(description);
            p.setPrice(price);
            p.setQuantity(quantity);
            p.setImageUrl("/images/products/" + filename); // dùng cho <img th:src>

            productService.save(p);
            ra.addFlashAttribute("success", "Đã thêm sản phẩm mới!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi upload ảnh!");
            e.printStackTrace();
        }

        return "redirect:/admin/products";
    }
    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/product-list"; // đảm bảo bạn có file templates/admin/product-list.html
    }
}

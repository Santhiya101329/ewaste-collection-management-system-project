package com.ewaste.controller;

import com.ewaste.model.Category;
import com.ewaste.model.User;
import com.ewaste.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/admin/categories")
    public String manageCategories(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        if (!"ADMIN".equals(user.getRole())) return "redirect:/dashboard";

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("category", new Category());
        return "manage-categories";
    }

    @PostMapping("/admin/categories/add")
    public String addCategory(@ModelAttribute Category category, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";

        categoryService.addCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null || !"ADMIN".equals(user.getRole())) return "redirect:/login";

        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }
}
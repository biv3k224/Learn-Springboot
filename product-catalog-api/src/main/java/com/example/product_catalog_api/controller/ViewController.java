package com.example.product_catalog_api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/")
    public String index() {
        return "index";
    }


    @GetMapping("/products/new")
    public String newProductForm(){
        return "product-form";
    }

    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Long id, Model model){
        model.addAttribute("productId", id);
        return "product-form";
    }
}

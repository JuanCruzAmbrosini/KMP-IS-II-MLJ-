package com.thecodereveal.shopease.config;

import com.thecodereveal.shopease.entities.*;
import com.thecodereveal.shopease.repositories.CategoryRepository;
import com.thecodereveal.shopease.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        // 1. Women Category
        Category womenCategory = Category.builder()
                .name("Women")
                .code("WOMEN")
                .description("Women's Clothing")
                .build();

        List<CategoryType> womenTypes = new ArrayList<>();
        CategoryType womenSweatshirt = CategoryType.builder().name("Hoodie/Sweatshirt").code("SWEATSHIRT").description("Women Hoodies & Sweatshirts").category(womenCategory).build();
        CategoryType womenTshirt = CategoryType.builder().name("T-shirt").code("TSHIRTS").description("Women T-shirts").category(womenCategory).build();
        CategoryType womenDress = CategoryType.builder().name("Dresses").code("DRESSES").description("Women Dresses").category(womenCategory).build();
        CategoryType womenJeans = CategoryType.builder().name("Jeans").code("JEANS").description("Women Jeans").category(womenCategory).build();
        CategoryType womenCropTop = CategoryType.builder().name("Crop Top").code("CROPTOP").description("Women Crop Tops").category(womenCategory).build();
        CategoryType womenShorts = CategoryType.builder().name("Shorts").code("SHORTS").description("Women Shorts").category(womenCategory).build();

        womenTypes.addAll(Arrays.asList(womenSweatshirt, womenTshirt, womenDress, womenJeans, womenCropTop, womenShorts));
        womenCategory.setCategoryTypes(womenTypes);
        womenCategory = categoryRepository.save(womenCategory);

        // 2. Men Category
        Category menCategory = Category.builder()
                .name("Men")
                .code("MEN")
                .description("Men's Clothing")
                .build();

        List<CategoryType> menTypes = new ArrayList<>();
        CategoryType menJeans = CategoryType.builder().name("Jeans").code("JEANS").description("Men Jeans").category(menCategory).build();
        CategoryType menShirts = CategoryType.builder().name("Shirts").code("SHIRTS").description("Men Shirts").category(menCategory).build();
        CategoryType menTshirts = CategoryType.builder().name("T-shirts").code("TSHIRTS").description("Men T-shirts").category(menCategory).build();
        CategoryType menHoodies = CategoryType.builder().name("Hoodie's").code("HOODIES").description("Men Hoodies").category(menCategory).build();
        CategoryType menShorts = CategoryType.builder().name("Shorts").code("SHORTS").description("Men Shorts").category(menCategory).build();

        menTypes.addAll(Arrays.asList(menJeans, menShirts, menTshirts, menHoodies, menShorts));
        menCategory.setCategoryTypes(menTypes);
        menCategory = categoryRepository.save(menCategory);

        // 3. Kids Category
        Category kidsCategory = Category.builder()
                .name("Kids")
                .code("KIDS")
                .description("Kids Clothing")
                .build();

        List<CategoryType> kidsTypes = new ArrayList<>();
        CategoryType kidsTshirts = CategoryType.builder().name("T-shirts").code("TSHIRTS").description("Kids T-shirts").category(kidsCategory).build();
        CategoryType kidsShorts = CategoryType.builder().name("Shorts").code("SHORTS").description("Kids Shorts").category(kidsCategory).build();
        kidsTypes.addAll(Arrays.asList(kidsTshirts, kidsShorts));
        kidsCategory.setCategoryTypes(kidsTypes);
        kidsCategory = categoryRepository.save(kidsCategory);

        // Seed sample products
        createProduct("Black Sweatshirt",
                "100% Bio-washed Cotton – makes the fabric extra soft & silky. Flexible ribbed crew neck. Infinite range of HD prints.",
                new BigDecimal("49.99"), "Nike", 4.5f, true, "black-sweatshirt",
                womenCategory, womenSweatshirt,
                "https://codedev.b-cdn.net/products/sweatshirt-black.jpg",
                Arrays.asList("Black", "Blue"), Arrays.asList("S", "M", "L"));

        createProduct("White T-shirt",
                "A stylish white t-shirt for women made of premium soft cotton fabric.",
                new BigDecimal("29.99"), "TrendyWear", 4.5f, true, "white-t-shirt",
                womenCategory, womenTshirt,
                "https://codedev.b-cdn.net/products/tshirt-white.jpg",
                Arrays.asList("White"), Arrays.asList("S", "M", "L"));

        createProduct("Purple Hoodie",
                "A cozy purple hoodie for women with warm fleece lining and front pouch pocket.",
                new BigDecimal("69.99"), "Nike", 4.6f, false, "purple-hoodie",
                womenCategory, womenSweatshirt,
                "https://codedev.b-cdn.net/products/hoodie-purple.jpg",
                Arrays.asList("Purple"), Arrays.asList("S", "M", "L"));

        createProduct("Leaves Pattern White Dress",
                "A beautiful leaves pattern white dress for women, ideal for casual and summer outings.",
                new BigDecimal("79.99"), "SummerVibes", 4.7f, true, "leaves-pattern-white-dress",
                womenCategory, womenDress,
                "https://codedev.b-cdn.net/products/dress-white-leaves.jpg",
                Arrays.asList("White"), Arrays.asList("S", "M", "L"));

        createProduct("Women's Skinny Jeans",
                "Trendy women's skinny jeans with high stretch denim for an elegant fit.",
                new BigDecimal("59.99"), "ChicStyle", 4.8f, true, "womens-skinny-jeans",
                womenCategory, womenJeans,
                "https://codedev.b-cdn.net/products/jeans-blue.jpeg",
                Arrays.asList("Blue"), Arrays.asList("S", "M", "L"));

        createProduct("Men Classic Denim Jeans",
                "Classic straight fit jeans for men crafted from durable stretch denim.",
                new BigDecimal("64.99"), "Levis", 4.7f, true, "men-classic-jeans",
                menCategory, menJeans,
                "https://codedev.b-cdn.net/products/jeans-blue.jpeg",
                Arrays.asList("Blue", "Black"), Arrays.asList("M", "L", "XL"));

        createProduct("Men Casual Cotton Shirt",
                "Premium breathable cotton shirt perfect for work and weekend outings.",
                new BigDecimal("39.99"), "Zara", 4.6f, true, "men-casual-shirt",
                menCategory, menShirts,
                "https://codedev.b-cdn.net/products/tshirt-white.jpg",
                Arrays.asList("White", "Navy"), Arrays.asList("S", "M", "L", "XL"));

        createProduct("Men Pullover Fleece Hoodie",
                "Warm and comfortable pullover hoodie designed for everyday lifestyle and sports.",
                new BigDecimal("54.99"), "Puma", 4.8f, false, "men-pullover-hoodie",
                menCategory, menHoodies,
                "https://codedev.b-cdn.net/products/sweatshirt-black.jpg",
                Arrays.asList("Black", "Grey"), Arrays.asList("M", "L", "XL"));
    }

    private void createProduct(String name, String description, BigDecimal price, String brand,
                               float rating, boolean isNewArrival, String slug,
                               Category category, CategoryType categoryType,
                               String imageUrl, List<String> colors, List<String> sizes) {
        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .brand(brand)
                .rating(rating)
                .isNewArrival(isNewArrival)
                .slug(slug)
                .category(category)
                .categoryType(categoryType)
                .build();

        List<Resources> resources = new ArrayList<>();
        Resources primaryRes = Resources.builder()
                .name(slug + "-1.jpg")
                .url(imageUrl)
                .type("image")
                .isPrimary(true)
                .product(product)
                .build();
        resources.add(primaryRes);
        product.setResources(resources);

        List<ProductVariant> variants = new ArrayList<>();
        for (String color : colors) {
            for (String size : sizes) {
                ProductVariant variant = ProductVariant.builder()
                        .color(color)
                        .size(size)
                        .stockQuantity(10)
                        .product(product)
                        .build();
                variants.add(variant);
            }
        }
        product.setProductVariants(variants);

        productRepository.save(product);
    }
}


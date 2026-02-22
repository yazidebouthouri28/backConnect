package tn.esprit.projetPi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit.projetPi.dto.CategoryDTO;
import tn.esprit.projetPi.dto.CreateCategoryRequest;
import tn.esprit.projetPi.entities.Category;
import tn.esprit.projetPi.exception.DuplicateResourceException;
import tn.esprit.projetPi.exception.ResourceNotFoundException;
import tn.esprit.projetPi.repositories.CategoryRepository;
import tn.esprit.projetPi.repositories.ProductRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> getActiveCategories() {
        return categoryRepository.findByIsActiveOrderBySortOrderAsc(true).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> getRootCategories() {
        return categoryRepository.findByParentIdIsNull().stream()
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> getSubcategories(String parentId) {
        return categoryRepository.findByParentIdAndIsActiveOrderBySortOrderAsc(parentId, true).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO getCategoryById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return toDTO(category);
    }

    public CategoryDTO getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return toDTO(category);
    }

    public CategoryDTO getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + name));
        return toDTO(category);
    }

    public List<CategoryDTO> getFeaturedCategories() {
        return categoryRepository.findByIsFeatured(true).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CategoryDTO> searchCategories(String query) {
        return categoryRepository.searchByName(query).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CategoryDTO createCategory(CreateCategoryRequest request) {
        String slug = generateSlug(request.getName());
        if (categoryRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Category with similar name already exists");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setSlug(slug);
        category.setImage(request.getImage());
        category.setIcon(request.getIcon());
        category.setParentId(request.getParentId());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        category.setSeoTitle(request.getSeoTitle());
        category.setSeoDescription(request.getSeoDescription());
        category.setMetaKeywords(request.getMetaKeywords());
        category.setIsActive(true);
        category.setIsFeatured(false);
        category.setProductCount(0);
        category.setCreatedAt(LocalDateTime.now());

        // Set level and path based on parent
        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
            category.setLevel((parent.getLevel() != null ? parent.getLevel() : 0) + 1);
            category.setPath((parent.getPath() != null ? parent.getPath() : "/" + parent.getSlug()) + "/" + slug);
        } else {
            category.setLevel(0);
            category.setPath("/" + slug);
        }

        Category saved = categoryRepository.save(category);
        log.info("Category created: {}", saved.getName());
        return toDTO(saved);
    }

    public CategoryDTO createCategory(CategoryDTO dto) {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName(dto.getName());
        request.setDescription(dto.getDescription());
        request.setImage(dto.getImage());
        request.setIcon(dto.getIcon());
        request.setParentId(dto.getParentId());
        request.setSortOrder(dto.getSortOrder());
        request.setSeoTitle(dto.getSeoTitle());
        request.setSeoDescription(dto.getSeoDescription());
        return createCategory(request);
    }

    public CategoryDTO updateCategory(String id, CategoryDTO dto) {
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName(dto.getName());
        request.setDescription(dto.getDescription());
        request.setImage(dto.getImage());
        request.setIcon(dto.getIcon());
        request.setParentId(dto.getParentId());
        request.setSortOrder(dto.getSortOrder());
        request.setSeoTitle(dto.getSeoTitle());
        request.setSeoDescription(dto.getSeoDescription());
        return updateCategory(id, request);
    }

    public CategoryDTO updateCategory(String id, CreateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (request.getName() != null) {
            category.setName(request.getName());
            String newSlug = generateSlug(request.getName());
            if (!newSlug.equals(category.getSlug()) && categoryRepository.existsBySlug(newSlug)) {
                throw new DuplicateResourceException("Category with similar name already exists");
            }
            category.setSlug(newSlug);
        }
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getImage() != null) category.setImage(request.getImage());
        if (request.getIcon() != null) category.setIcon(request.getIcon());
        if (request.getSortOrder() != null) category.setSortOrder(request.getSortOrder());
        if (request.getSeoTitle() != null) category.setSeoTitle(request.getSeoTitle());
        if (request.getSeoDescription() != null) category.setSeoDescription(request.getSeoDescription());
        if (request.getMetaKeywords() != null) category.setMetaKeywords(request.getMetaKeywords());
        category.setUpdatedAt(LocalDateTime.now());

        return toDTO(categoryRepository.save(category));
    }

    public CategoryDTO toggleCategoryStatus(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setIsActive(!Boolean.TRUE.equals(category.getIsActive()));
        category.setUpdatedAt(LocalDateTime.now());
        return toDTO(categoryRepository.save(category));
    }

    public CategoryDTO toggleFeatured(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        category.setIsFeatured(!Boolean.TRUE.equals(category.getIsFeatured()));
        category.setUpdatedAt(LocalDateTime.now());
        return toDTO(categoryRepository.save(category));
    }

    public void deleteCategory(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Check if category has products
        long productCount = productRepository.countByCategoryId(id);
        if (productCount > 0) {
            throw new IllegalStateException("Cannot delete category with " + productCount + " products. Please reassign products first.");
        }

        // Check if category has subcategories
        List<Category> subcategories = categoryRepository.findByParentId(id);
        if (!subcategories.isEmpty()) {
            throw new IllegalStateException("Cannot delete category with subcategories. Please delete subcategories first.");
        }

        categoryRepository.deleteById(id);
        log.info("Category {} deleted", category.getName());
    }

    public void updateProductCount(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            long count = productRepository.countByCategoryId(categoryId);
            category.setProductCount((int) count);
            categoryRepository.save(category);
        }
    }

    public void updateProductCount(String categoryId, int count) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category != null) {
            category.setProductCount(count);
            categoryRepository.save(category);
        }
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

    private CategoryDTO toDTO(Category category) {
        CategoryDTO dto = CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .image(category.getImage())
                .icon(category.getIcon())
                .parentId(category.getParentId())
                .level(category.getLevel())
                .path(category.getPath())
                .isActive(category.getIsActive())
                .isFeatured(category.getIsFeatured())
                .sortOrder(category.getSortOrder())
                .productCount(category.getProductCount())
                .seoTitle(category.getSeoTitle())
                .seoDescription(category.getSeoDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();

        // Load children if exist
        List<Category> children = categoryRepository.findByParentId(category.getId());
        if (!children.isEmpty()) {
            dto.setChildren(children.stream().map(this::toDTO).collect(Collectors.toList()));
        }

        return dto;
    }
}

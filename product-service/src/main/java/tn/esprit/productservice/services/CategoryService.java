package tn.esprit.productservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.productservice.entities.Category;
import tn.esprit.productservice.exception.DuplicateResourceException;
import tn.esprit.productservice.exception.ResourceNotFoundException;
import tn.esprit.productservice.repositories.CategoryRepository;

import java.util.List;
import java.util.UUID;

/**
 * Category Service
 *
 * FIX: Every read method is annotated @Transactional(readOnly = true).
 * This keeps the Hibernate session open until the method returns, so lazy
 * proxies (parent, products, subcategories) can be safely accessed by the
 * mapper WITHIN the same call stack.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "'all'")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "'active'")
    public List<Category> getActiveCategories() {
        return categoryRepository.findByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "'root'")
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    @Transactional(readOnly = true)
    public List<Category> getSubcategories(UUID parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Category getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public Category createCategory(Category category, UUID parentId) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new DuplicateResourceException("Category with this name already exists");
        }
        if (parentId != null) {
            Category parent = getCategoryById(parentId);
            category.setParent(parent);
        }
        Category saved = categoryRepository.save(category);
        log.info("Category created: {} ({})", saved.getName(), saved.getId());
        return saved;
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public Category updateCategory(UUID id, Category categoryDetails) {
        Category category = getCategoryById(id);
        if (categoryDetails.getName() != null) {
            if (!category.getName().equals(categoryDetails.getName()) &&
                    categoryRepository.existsByName(categoryDetails.getName())) {
                throw new DuplicateResourceException("Category with this name already exists");
            }
            category.setName(categoryDetails.getName());
        }
        if (categoryDetails.getDescription() != null) category.setDescription(categoryDetails.getDescription());
        if (categoryDetails.getImage() != null) category.setImage(categoryDetails.getImage());
        if (categoryDetails.getDisplayOrder() != null) category.setDisplayOrder(categoryDetails.getDisplayOrder());
        if (categoryDetails.getIsActive() != null) category.setIsActive(categoryDetails.getIsActive());
        return categoryRepository.save(category);
    }

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(UUID id) {
        Category category = getCategoryById(id);
        category.setIsActive(false);
        categoryRepository.save(category);
        log.info("Category soft-deleted: {} ({})", category.getName(), category.getId());
    }
}
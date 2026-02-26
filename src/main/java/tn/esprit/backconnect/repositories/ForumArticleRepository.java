package tn.esprit.projetintegre.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.projetintegre.entities.ForumArticle;

import java.util.List;

@Repository
public interface ForumArticleRepository extends JpaRepository<ForumArticle, Long> {
    Page<ForumArticle> findByIsPublishedTrue(Pageable pageable);
    Page<ForumArticle> findByAuthorId(Long authorId, Pageable pageable);
    Page<ForumArticle> findByCategory(String category, Pageable pageable);
    List<ForumArticle> findByIsPinnedTrue();
    
    @Query("SELECT a FROM ForumArticle a WHERE a.isPublished = true AND (LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ForumArticle> searchArticles(String keyword, Pageable pageable);
}

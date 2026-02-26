package tn.esprit.projetintegre.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.projetintegre.entities.ForumComment;

import java.util.List;

@Repository
public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {
    
    List<ForumComment> findByArticleId(Long articleId);
    
    List<ForumComment> findByArticleIdAndParentIsNull(Long articleId);
    
    List<ForumComment> findByAuthorId(Long authorId);
    
    List<ForumComment> findByParentId(Long parentId);
    
    List<ForumComment> findByIsApprovedTrue();
    
    Integer countByArticleId(Long articleId);
}

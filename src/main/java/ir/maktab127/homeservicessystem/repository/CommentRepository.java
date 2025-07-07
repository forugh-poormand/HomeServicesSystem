package ir.maktab127.homeservicessystem.repository;

import ir.maktab127.homeservicessystem.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    public Optional<Comment> findByOrderId(Long orderId);

}

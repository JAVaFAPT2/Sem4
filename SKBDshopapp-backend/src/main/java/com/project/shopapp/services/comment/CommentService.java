package com.project.shopapp.services.comment;

import com.project.shopapp.dtos.CommentDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Comment;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.CommentRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.dtos.responses.comment.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentService implements ICommentService {
  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final ProductRepository productRepository;

  private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

  @Override
  @Transactional
  public Comment insertComment(CommentDTO commentDTO) throws DataNotFoundException {
    User user = getUser(commentDTO.getUserId());
    Product product = getProduct(commentDTO.getProductId());

    Comment newComment = Comment.builder()
            .user(user)
            .product(product)
            .content(commentDTO.getContent())
            .build();

    logger.info("Inserting new comment for product ID: {}", product.getId());

    return commentRepository.save(newComment);
  }

  private User getUser(Long userId) throws DataNotFoundException {
    return userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException("Cannot find User with id: " + userId));
  }

  private Product getProduct(Long productId) throws DataNotFoundException {
    return productRepository.findById(productId)
            .orElseThrow(() -> new DataNotFoundException("Cannot find Product with id: " + productId));
  }

  @Override
  @Transactional
  public void deleteComment(Long commentId) throws DataNotFoundException {
    if (!commentRepository.existsById(commentId)) {
      throw new DataNotFoundException("Cannot find Comment with id: " + commentId);
    }
    commentRepository.deleteById(commentId);
    logger.info("Deleted comment with ID: {}", commentId);
  }

  @Override
  @Transactional
  public void updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException {
    Comment existingComment = commentRepository.findById(id)
            .orElseThrow(() -> new DataNotFoundException("Comment not found with id: " + id));

    existingComment.setContent(commentDTO.getContent());
    commentRepository.save(existingComment);

    logger.info("Updated comment with ID: {}", id);
  }

  @Override
  public List<CommentResponse> getCommentsByUserAndProduct(Long userId, Long productId) {
    List<Comment> comments = commentRepository.findByUserIdAndProductId(userId, productId);
    return comments.stream()
            .map(CommentResponse::fromComment)
            .collect(Collectors.toList());
  }

  @Override
  public List<CommentResponse> getCommentsByProduct(Long productId) {
    List<Comment> comments = commentRepository.findByProductId(productId);
    return comments.stream()
            .map(CommentResponse::fromComment)
            .collect(Collectors.toList());
  }
}
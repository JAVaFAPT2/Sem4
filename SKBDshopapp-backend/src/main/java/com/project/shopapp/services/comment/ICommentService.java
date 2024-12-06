package com.project.shopapp.services.comment;

import com.project.shopapp.dtos.CommentDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Comment;
import com.project.shopapp.dtos.responses.comment.CommentResponse;

import java.util.List;

public interface ICommentService {
  Comment insertComment(CommentDTO comment) throws DataNotFoundException;

  void deleteComment(Long commentId) throws DataNotFoundException;

  void updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException;

  List<CommentResponse> getCommentsByUserAndProduct(Long userId, Long productId);

  List<CommentResponse> getCommentsByProduct(Long productId);
}

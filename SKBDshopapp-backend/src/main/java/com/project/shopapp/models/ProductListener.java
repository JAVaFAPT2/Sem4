package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class ProductListener {
  private static final Logger logger = LoggerFactory.getLogger(ProductListener.class);

  @PrePersist
  public void prePersist(Product product) {
    logger.info("prePersist: {}", product);
  }

  @PostPersist // after save
  public void postPersist(Product product) {
    logger.info("postPersist: {}", product);
  }

  @PreUpdate
  public void preUpdate(Product product) {
    logger.info("preUpdate: {}", product);
  }

  @PostUpdate
  public void postUpdate(Product product) {
    logger.info("postUpdate: {}", product);
  }

  @PreRemove
  public void preRemove(Product product) {
    logger.info("preRemove: {}", product);
  }

  @PostRemove
  public void postRemove(Product product) {
    logger.info("postRemove: {}", product);
  }
}
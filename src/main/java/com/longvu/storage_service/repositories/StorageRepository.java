package com.longvu.storage_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.longvu.storage_service.entities.FileEntity;
import com.longvu.storage_service.entities.UserEntity;

@Repository
public interface StorageRepository extends JpaRepository<FileEntity, String> {
    Optional<FileEntity> findByFileName(String fileName);

    boolean existsByFileName(String fileName);

    List<FileEntity> getByUser(UserEntity user);
}

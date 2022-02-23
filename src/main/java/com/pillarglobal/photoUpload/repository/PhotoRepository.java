package com.pillarglobal.photoUpload.repository;

import com.pillarglobal.photoUpload.model.PhotoInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<PhotoInfo, Integer> {

}

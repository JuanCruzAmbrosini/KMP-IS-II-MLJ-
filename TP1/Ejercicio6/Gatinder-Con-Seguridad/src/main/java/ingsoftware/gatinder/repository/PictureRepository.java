package ingsoftware.gatinder.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ingsoftware.gatinder.entity.Picture;

public interface PictureRepository extends JpaRepository<Picture, Long> {}

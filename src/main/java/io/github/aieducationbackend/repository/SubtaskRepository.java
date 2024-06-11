package io.github.aieducationbackend.repository;

import io.github.aieducationbackend.entity.Subtask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {
}

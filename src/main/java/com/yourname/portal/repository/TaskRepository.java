package com.yourname.portal.repository;

import com.yourname.portal.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Ensure this line is present so the Service can find it!
    List<Task> findByStatus(String status);
}
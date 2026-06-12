package com.yourname.portal.repository;

import com.yourname.portal.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    // Find all assignments for a specific user ID
    java.util.List<Assignment> findByAssignedToId(Long staffId);
}
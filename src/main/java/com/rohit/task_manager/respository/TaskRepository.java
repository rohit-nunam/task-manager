package com.rohit.task_manager.respository;

import com.rohit.task_manager.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE (COALESCE(:userId, t.assignedTo.id) = t.assignedTo.id) AND " +
            "(COALESCE(:firstName, t.assignedTo.firstName) = t.assignedTo.firstName) AND " +
            "(COALESCE(:expectedEnd, t.expectedEndDateTime) = t.expectedEndDateTime) AND " +
            "(COALESCE(:status, t.status.name) = t.status.name) AND t.isDeleted = false AND t.assignedTo.isDeleted = false")
    Page<Task> searchTasks(@Param("userId") UUID userId, @Param("firstName") String firstName,
                           @Param("expectedEnd") Instant expectedEnd, @Param("status") String status, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE (:userId IS NULL OR t.assignedTo.id = :userId) AND " +
            "(:status IS NULL OR t.status.name = :status) AND (:priority IS NULL OR t.priority.name = :priority) AND " +
            "t.isDeleted = false AND t.assignedTo.isDeleted = false")
    Page<Task> filterTasks(
            @Param("userId") UUID userId,
            @Param("status") String status,
            @Param("priority") String priority,
            Pageable pageable);

}

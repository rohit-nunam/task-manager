package com.rohit.task_manager.respository;

import com.rohit.task_manager.domain.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT t FROM Task t WHERE " +
            "(:userId IS NULL OR t.assignedTo.id = :userId) AND " +
            "(:firstName IS NULL OR LOWER(t.assignedTo.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:expectedEnd IS NULL OR t.expectedEndDateTime = :expectedEnd) AND " +
            "(:status IS NULL OR t.status.name = :status) AND " +
            "t.isDeleted = false")
    Page<Task> searchTasks(@Param("userId") UUID userId, @Param("firstName") String firstName,
                           @Param("expectedEnd") Instant expectedEnd, @Param("status") String status, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE " +
            "(:userId IS NULL OR t.assignedTo.id = :userId) AND " +
            "(COALESCE(:statuses, NULL) IS NULL OR t.status.name IN :statuses) AND " +
            "(COALESCE(:priorities, NULL) IS NULL OR t.priority.name IN :priorities) AND " +
            "t.isDeleted = false")
    Page<Task> filterTasks(
            @Param("userId") UUID userId, @Param("statuses") List<String> statuses,
            @Param("priorities") List<String> priorities, Pageable pageable);


}

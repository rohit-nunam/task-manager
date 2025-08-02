package com.rohit.task_manager.respository;

import com.rohit.task_manager.domain.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

    Page<Story> findByAssignedToIdAndIsDeletedFalse(UUID userId, Pageable pageable);

    @Query("SELECT s FROM Story s WHERE s.status.name = 'IN_PROGRESS' AND s.priority.name = 'LOW' AND s.isDeleted = false")
    List<Story> findActiveStories();

}

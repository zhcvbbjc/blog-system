package com.blog.repository;

import com.blog.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("""
        select c from Conversation c
        join ConversationParticipant cp
          on cp.conversationId = c.id
        where cp.userId = :userId
          and c.type = 'AI'
        order by c.updatedAt desc
    """)
    List<Conversation> findAiConversationsByUserId(Long userId);

    @Query("""
        select count(c) from Conversation c
        join ConversationParticipant cp
          on cp.conversationId = c.id
        where cp.userId = :userId
          and c.type = 'AI'
    """)
    long countAiByUserId(Long userId);

    @Modifying
    @Query("""
        update Conversation c
        set c.updatedAt = :time
        where c.id = :id
    """)
    void updateUpdatedAt(Long id, LocalDateTime time);
}


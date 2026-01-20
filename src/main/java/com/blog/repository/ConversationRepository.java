package com.blog.repository;

import com.blog.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("""
        select c from Conversation c
        join ConversationParticipant cp
          on cp.conversationId = c.id
        where cp.userId = :userId
          and c.type = 'AI'
          and c.isDeleted = false
        order by c.updatedAt desc
    """)
    List<Conversation> findAiConversationsByUserId(Long userId);

    @Query("""
        select c from Conversation c
        join ConversationParticipant cp
          on cp.conversationId = c.id
        where cp.userId = :userId
          and c.type = 'AI'
          and cp.role = 'OWNER'
          and c.isDeleted = false
        order by c.updatedAt desc
    """)
    List<Conversation> findAiConversationsByOwnerUserId(Long userId);

    @Query("""
        select count(c) from Conversation c
        join ConversationParticipant cp
          on cp.conversationId = c.id
        where cp.userId = :userId
          and c.type = 'AI'
          and c.isDeleted = false
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


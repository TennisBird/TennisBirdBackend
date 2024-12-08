package org.tennis_bird.core.repositories.chat;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tennis_bird.core.entities.chat.ChatMemberEntity;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

public interface ChatMemberRepository extends JpaRepository<ChatMemberEntity, Long> {
    @Query("SELECT c FROM ChatMemberEntity c WHERE c.chatEntity.id = :chatId")
    Optional<ChatMemberEntity> findByChatId(@Param("chatId") Long chatId);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM ChatMemberEntity c WHERE c.chatEntity.id = :chatId AND c.member.id = :personId")
    void deleteByChatIdAndPersonId(@Param("chatId") Long chatId, @Param("personId") UUID personId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ChatMemberEntity c WHERE c.chatEntity.id = :chatId AND c.member.id = :personId")
    boolean existsByChatIdAndPersonId(@Param("chatId") Long chatId, @Param("personId") UUID personId);
}


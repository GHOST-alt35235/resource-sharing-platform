package cn.edu.wynu.mapper;

import cn.edu.wynu.model.entity.ChatSession;
import cn.edu.wynu.model.entity.ChatMessage;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ChatMapper {

    @Select("SELECT * FROM chat_session WHERE (user1_id = #{userId1} AND user2_id = #{userId2}) OR (user1_id = #{userId2} AND user2_id = #{userId1}) LIMIT 1")
    ChatSession findSession(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Select("SELECT * FROM chat_session WHERE id = #{sessionId}")
    ChatSession findSessionById(Long sessionId);

    @Select("SELECT * FROM chat_session WHERE user1_id = #{userId} OR user2_id = #{userId} ORDER BY last_time DESC")
    List<ChatSession> findByUserId(Long userId);

    @Insert("INSERT INTO chat_session(user1_id, user2_id, user1_nickname, user2_nickname, create_time) " +
            "VALUES(#{user1Id}, #{user2Id}, #{user1Nickname}, #{user2Nickname}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertSession(ChatSession session);

    @Update("UPDATE chat_session SET last_message = #{lastMessage}, last_time = NOW() WHERE id = #{id}")
    int updateSessionLastMessage(@Param("id") Long id, @Param("lastMessage") String lastMessage);

    @Select("SELECT * FROM chat_message WHERE session_id = #{sessionId} ORDER BY create_time DESC LIMIT #{limit} OFFSET #{offset}")
    List<ChatMessage> findMessagesBySessionId(@Param("sessionId") Long sessionId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    @Insert("INSERT INTO chat_message(session_id, sender_id, sender_nickname, receiver_id, receiver_nickname, content, file_id, type, status, create_time) " +
            "VALUES(#{sessionId}, #{senderId}, #{senderNickname}, #{receiverId}, #{receiverNickname}, #{content}, #{fileId}, #{type}, 1, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertMessage(ChatMessage message);

    @Update("UPDATE chat_message SET status = 0 WHERE id = #{id}")
    int markAsRead(Long id);
}

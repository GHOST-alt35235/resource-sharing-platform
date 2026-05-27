package cn.edu.wynu.service;

import cn.edu.wynu.model.entity.ChatSession;
import cn.edu.wynu.model.entity.ChatMessage;
import java.util.List;
import java.util.Map;

public interface ChatService {
    ChatSession getOrCreateSession(Long targetUserId);
    List<ChatSession> getMySessions();
    Map<String, Object> getMessages(Long sessionId, Integer page, Integer size);
    ChatMessage sendMessage(Long sessionId, String content, Long fileId, Integer type);
}

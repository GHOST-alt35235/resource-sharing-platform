package cn.edu.wynu.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.edu.wynu.mapper.ChatMapper;
import cn.edu.wynu.mapper.UserMapper;
import cn.edu.wynu.model.entity.ChatSession;
import cn.edu.wynu.model.entity.ChatMessage;
import cn.edu.wynu.model.entity.User;
import cn.edu.wynu.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public ChatSession getOrCreateSession(Long targetUserId) {
        Long userId = StpUtil.getLoginIdAsLong();
        ChatSession session = chatMapper.findSession(userId, targetUserId);

        if (session == null) {
            User user1 = userMapper.findById(userId);
            User user2 = userMapper.findById(targetUserId);

            session = new ChatSession();
            session.setUser1Id(userId);
            session.setUser2Id(targetUserId);
            session.setUser1Nickname(user1.getNickname());
            session.setUser2Nickname(user2.getNickname());
            chatMapper.insertSession(session);
        }
        return session;
    }

    @Override
    public List<ChatSession> getMySessions() {
        Long userId = StpUtil.getLoginIdAsLong();
        return chatMapper.findByUserId(userId);
    }

    @Override
    public Map<String, Object> getMessages(Long sessionId, Integer page, Integer size) {
        if (page == null) page = 1;
        if (size == null) size = 20;
        int offset = (page - 1) * size;

        List<ChatMessage> messages = chatMapper.findMessagesBySessionId(sessionId, offset, size);

        Map<String, Object> result = new HashMap<>();
        result.put("messages", messages);
        result.put("page", page);
        result.put("size", size);
        result.put("hasMore", messages.size() == size);

        return result;
    }

    @Override
    @Transactional
    public ChatMessage sendMessage(Long sessionId, String content, Long fileId, Integer type) {
        Long userId = StpUtil.getLoginIdAsLong();
        User sender = userMapper.findById(userId);
        ChatSession session = chatMapper.findSessionById(sessionId);

        ChatMessage message = new ChatMessage();
        message.setSessionId(sessionId);
        message.setSenderId(userId);
        message.setSenderNickname(sender.getNickname());
        message.setContent(content);
        message.setFileId(fileId);
        message.setType(type != null ? type : 1);
        message.setStatus(1);

        if (session != null) {
            if (session.getUser1Id().equals(userId)) {
                message.setReceiverId(session.getUser2Id());
                message.setReceiverNickname(session.getUser2Nickname());
            } else {
                message.setReceiverId(session.getUser1Id());
                message.setReceiverNickname(session.getUser1Nickname());
            }
            chatMapper.updateSessionLastMessage(sessionId, content);
        }

        chatMapper.insertMessage(message);
        return message;
    }
}

package cn.edu.wynu.controller;

import cn.edu.wynu.common.AjaxResult;
import cn.edu.wynu.model.entity.ChatSession;
import cn.edu.wynu.model.entity.ChatMessage;
import cn.edu.wynu.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/session/{targetUserId}")
    public AjaxResult getOrCreateSession(@PathVariable Long targetUserId) {
        ChatSession session = chatService.getOrCreateSession(targetUserId);
        return AjaxResult.success(session);
    }

    @GetMapping("/sessions")
    public AjaxResult getMySessions() {
        List<ChatSession> sessions = chatService.getMySessions();
        return AjaxResult.success(sessions);
    }

    @GetMapping("/messages/{sessionId}")
    public AjaxResult getMessages(@PathVariable Long sessionId,
                                  @RequestParam(required = false) Integer page,
                                  @RequestParam(required = false) Integer size) {
        Map<String, Object> result = chatService.getMessages(sessionId, page, size);
        return AjaxResult.success(result);
    }

    @PostMapping("/message")
    public AjaxResult sendMessage(@RequestBody Map<String, Object> params) {
        Long sessionId = Long.valueOf(params.get("sessionId").toString());
        String content = (String) params.get("content");
        Long fileId = params.get("fileId") != null ? Long.valueOf(params.get("fileId").toString()) : null;
        Integer type = params.get("type") != null ? Integer.valueOf(params.get("type").toString()) : 1;

        ChatMessage message = chatService.sendMessage(sessionId, content, fileId, type);
        return AjaxResult.success("发送成功", message);
    }
}

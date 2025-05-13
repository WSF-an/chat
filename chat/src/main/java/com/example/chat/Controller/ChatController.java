package com.example.chat.Controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.chat.dto.ChatList;
import com.example.chat.dto.ChatMessage;
import com.example.chat.dto.User;
import com.example.chat.service.ChatListService;
import com.example.chat.service.ChatMessageService;
import com.example.chat.service.Userservice;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class ChatController {

    @Autowired
    private Userservice userservice;

    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private ChatListService chatlistservice;

    @PostMapping("/chat/{conversationID}/message")
    public String charAI(@PathVariable Long conversationID, @RequestBody Map<String, String> payload, HttpServletRequest request){
        // 校验用户是否登录
        User user = userservice.getLoginUser(request);
        if(user == null){
            log.info("user is null: 用户未登录");
            return "用户未登录";
        }
        long userId = user.getId();

        // 获取 session 中的多会话历史
        HttpSession session = request.getSession();
        Map<Long, List<String>> multiSessionHistory =
                (Map<Long, List<String>>) session.getAttribute("multi_history");

        if (multiSessionHistory == null) {
            multiSessionHistory = new HashMap<>();
        }

        List<String> history = multiSessionHistory.computeIfAbsent(conversationID, k -> new ArrayList<>());

        String message = payload.get("message");
        if (message == null || message.trim().isEmpty()) {
            return "消息不能为空";
        }

        history.add("用户：" + message);

        // 模拟 AI 回复
        //String reply = "AI：" + message + "（我已经收到了）";
        // 调用 Python 模型接口
        String reply;
        try {
            RestTemplate restTemplate = new RestTemplate();
            String flaskUrl = "http://localhost:5000/chat"; // 改为你的 Python 服务地址
            Map<String, String> requestData = new HashMap<>();
            requestData.put("query", message);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestData, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(flaskUrl, entity, Map.class);
            reply = (String) response.getBody().get("reply");
        } catch (Exception e) {
            log.error("调用AI接口失败", e);
            reply = "AI：对不起，我现在无法回答你的问题。";
        }

        history.add(reply);

        // 将数据存入数据库
        chatMessageService.addChatMessage(message, conversationID,userId);

        session.setAttribute("multi_history", multiSessionHistory);
        return reply;
    }


    @GetMapping("/chat/{conversationID}/history")
    public List<ChatMessage> history(@PathVariable Long conversationID, HttpServletRequest request){
        //可删
        HttpSession session = request.getSession(false);
        if(session == null){
            log.info("session is null:无会话记录");
        }
        // 校验用户是否登录
        User user = userservice.getLoginUser(request);
        if(user == null){
            log.info("user is null: 用户未登录");
            return new ArrayList<>();
        }
        long userId = user.getId();
        /*Map<Long, List<String>> multiSessionHistory = (Map<Long, List<String>>) session.getAttribute("multi_history");
        List<String> list = multiSessionHistory.get(conversationID);*/
        List<ChatMessage> history = chatMessageService.getChatMessages(conversationID,userId) ;
        return history;
    }
    @PostMapping("/chat/add_list")
    public  void addList(@RequestBody ChatList chatlist, HttpServletRequest request){
        User user = userservice.getLoginUser(request);
        if(user==null){
            log.info("user is null:用户未登录");
        }
        long userId = user.getId();
        chatlist.setUserId(userId);
        chatlistservice.addChatList(chatlist.getUserId(),chatlist.getConversationId(),chatlist.getDescription());
    }
    /*
    @GetMapping("/{userId}/getlist")
    public List<Long> getlist(@PathVariable Long userId,HttpServletRequest request){}  这里可以不用发userId
     */
    @GetMapping("/chat/get_list")
    public List<ChatList> getlist(HttpServletRequest request){
        User user = userservice.getLoginUser(request);
        if(user==null){
            log.info("user is null:用户未登录");
        }
        long userId = user.getId();
        List<ChatList> chatLists = chatlistservice.getChatList(userId);
        return chatLists;
    }

    @GetMapping("/chat/download")
    public void download(HttpServletResponse response) throws Exception {
        String filePath = System.getProperty("user.dir") + "/file/";
        String realPath = filePath + "handler.zip";

        if (!FileUtil.exist(realPath)) {
            throw new RuntimeException("文件不存在: " + realPath);
        }

        // 设置响应类型
        response.setContentType("application/octet-stream");
        // 设置下载文件名
        response.setHeader("Content-Disposition", "attachment;filename=handler.zip");

        // 读取并写出文件
        byte[] bytes = FileUtil.readBytes(realPath);
        ServletOutputStream os = response.getOutputStream();
        os.write(bytes);
        os.flush();
        os.close();
    }
    @PostMapping("/chat/send-to-python")
    public ResponseEntity<String> sendToPython(@RequestParam("file") MultipartFile file) {
        try {
            // 临时保存文件
            Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), file.getOriginalFilename());
            file.transferTo(tempPath.toFile());

            // 构造 JSON 请求体
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("file_path", tempPath.toString());
            requestBody.put("server_name", "default_server"); // 写死

            // 设置 HTTP 请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            // 调用 Python 接口
            String pythonUrl = "http://localhost:5000/serve";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.postForEntity(pythonUrl, request, String.class);

            return ResponseEntity.ok("Python返回：" + response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("调用失败：" + e.getMessage());
        }
    }




}

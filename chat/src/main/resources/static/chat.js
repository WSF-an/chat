let display_sonIcon = document.getElementById('sonIcon')
let hidden_sonIcon = document.getElementById('sonHiddenIcon')
let left_layout_id = document.getElementById('left_layout_id')
let myself_datetime = document.getElementById('datatime') // 注意：页面中未找到 id 为 datatime 的元素
let chatHistory = document.getElementById('chat_history');

let chatData = []; // 每条消息的格式 { id, sender, content, time, conversation , replyTo，conversationID}
let messageIdCounter = 0;
// 当前会话（默认会话名 "会话1"）
let currentChat = {
    name: '会话1',
    id: generateConversationId() // 初始会话ID
};
// 用于存储所有会话名称（以便左侧列表展示）
let conversationList = [currentChat];


function renderChatMessages() {
    const chatHistory = document.getElementById('chat_history');
    chatHistory.innerHTML = '';

    // 根据 conversationID 过滤消息
    const messages = chatData.filter(message => message.conversationID === currentChat.id);


    messages.forEach(message => {
        const messageContainer = document.createElement('div');
        messageContainer.className = 'chat_item';

        // 创建消息元信息（时间、复制按钮、删除按钮）
        const messageMeta = document.createElement('div');
        messageMeta.className = 'message_meta';

        const timeSpan = document.createElement('span');
        timeSpan.className = 'message_time';
        timeSpan.textContent = message.time;

        const copyBtn = addCopyButton(message.content); // 新增：复制按钮
        const deleteBtn = addDeleteButton(message.id);  // 原有的删除按钮

        // 如果是用户消息，添加编辑按钮
        if (message.sender === 'user') {
            const editBtn = addEditButton(message.id, message.content);
            messageMeta.appendChild(timeSpan);
            messageMeta.appendChild(copyBtn);
            messageMeta.appendChild(editBtn); // 新增：编辑按钮
            messageMeta.appendChild(deleteBtn);
        } else {
            const shouldShowReload = !message.hasNoParent; // 检查是否有关联用户消息

            messageMeta.appendChild(timeSpan);
            messageMeta.appendChild(copyBtn);
            if (shouldShowReload) { // 只有关联用户消息存在时才显示重新加载
                const reloadBtn = addReloadButton(message.id);
                messageMeta.appendChild(reloadBtn);
            }
            messageMeta.appendChild(deleteBtn);
        }
        // 创建消息内容区域
        const messageContent = document.createElement('div');
        messageContent.className = 'message_content';

        // 创建头像
        const avatar = document.createElement('div');
        avatar.className = message.sender === 'user' ? 'my_img_son1' : 'chat_img_son';

        // 创建消息气泡
        const messageBubble = document.createElement('div');
        messageBubble.className = message.sender === 'user' ? 'myself_chat' : 'gpt_chat';
        messageBubble.textContent = message.content;

        // 根据发送者决定布局
        const messageWrapper = document.createElement('div');
        messageWrapper.className = message.sender === 'user' ? 'user_message_container' : 'ai_message_container';

        messageWrapper.appendChild(messageMeta);
        messageContent.appendChild(message.sender === 'user' ? messageBubble : avatar);
        messageContent.appendChild(message.sender === 'user' ? avatar : messageBubble);
        messageWrapper.appendChild(messageContent);

        messageContainer.appendChild(messageWrapper);
        chatHistory.appendChild(messageContainer);
    });

    chatHistory.scrollTop = chatHistory.scrollHeight;
}


// 发送消息：构建消息数据，更新全局数据，然后动态渲染当前会话的消息列表
function sendMessage() {
    const inputElement = document.querySelector('.ipt');
    const messageText = inputElement.value.trim();
    if (!messageText) return;

    const now = new Date();
    const dateTime = `${now.getFullYear()}/${String(now.getMonth() + 1).padStart(2, '0')}/${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`;

    // 如果是第一条消息，更新会话名称但不改变ID
    if (!chatData.find(msg => msg.conversationID === currentChat.id)) {
        const newConversationName = messageText.substring(0, 7);
        const index = conversationList.findIndex(conv => conv.id === currentChat.id);
        if (index !== -1) {
            conversationList[index].name = newConversationName;
            currentChat.name = newConversationName;
        }
        updateConversationListDOM();
    }

    const userMessage = {
        id: messageIdCounter++,
        sender: 'user',
        content: messageText,
        time: dateTime,
        conversation: currentChat.name,
        conversationID: currentChat.id
    };

    chatData.push(userMessage);
    renderChatMessages();
    inputElement.value = '';

    // 发送到后端
    axios.post('/api/chat', { message: messageText })
        .then(function(response) {
            const backendReply = response.data.reply || '未收到回复';
            const nowReply = new Date();
            const replyTime = `${nowReply.getFullYear()}/${String(nowReply.getMonth() + 1).padStart(2, '0')}/${String(nowReply.getDate()).padStart(2, '0')} ${String(nowReply.getHours()).padStart(2, '0')}:${String(nowReply.getMinutes()).padStart(2, '0')}:${String(nowReply.getSeconds()).padStart(2, '0')}`;

            const replyMessage = {
                id: messageIdCounter++,
                sender: 'backend',
                content: backendReply,
                time: replyTime,
                conversation: currentChat.name,
                replyTo: userMessage.id ,// 新增关联字段
                conversationID: currentChat.id
            };

            chatData.push(replyMessage);
            renderChatMessages();
            //sendHistory();
        })
        .catch(function(error) {
            console.error('请求错误：', error);
        });
}

function addDeleteButton(messageId) {
    const deleteBtn = document.createElement('button');
    deleteBtn.innerHTML = '🗑️';
    deleteBtn.className = 'delete-btn';
    deleteBtn.onclick = function() {
        // 1. 如果是用户消息，找到对应的 AI 回复
        const deletedMsg = chatData.find(msg => msg.id === messageId);
        let aiReplyId = null;

        if (deletedMsg?.sender === 'user') {
            aiReplyId = chatData.find(msg => msg.replyTo === messageId)?.id;
        }

        // 2. 删除消息
        chatData = chatData.filter(msg => msg.id !== messageId);

        // 3. 如果有对应的 AI 回复，更新其按钮组
        if (aiReplyId) {
            updateAIMessageButtons(aiReplyId);
        }

        renderChatMessages();
        //sendHistory();
    };
    return deleteBtn;
}
function updateAIMessageButtons(messageId) {
    const messageIndex = chatData.findIndex(msg => msg.id === messageId);
    if (messageIndex === -1) return;

    // 标记该消息已无关联用户消息
    chatData[messageIndex].hasNoParent = true;
}

// 保存聊天历史到后端（把 chatData 全部传过去，实际业务中可按需求调整）
function sendHistory() {
    axios.post('/api/history', { history: chatData })
        .then(function(response) {
            console.log('聊天历史成功发送到 /api/history');
        })
        .catch(function(error) {
            console.error('发送历史记录时出错：', error);
            console.log(chatData);
        });
}

// 左侧会话列表中新增会话项，点击后切换
function createNewChat() {
    const newConversationId = generateConversationId();
    const newConversationName = '会话' + (conversationList.length + 1);

    const newConversation = {
        name: newConversationName,
        id: newConversationId
    };

    conversationList.push(newConversation);
    currentChat = newConversation;
    updateConversationListDOM();
    renderChatMessages();
}

// 根据 conversationList 重新渲染左侧会话列表DOM
function updateConversationListDOM() {
    const chatListContainer = document.getElementById('chat_list_container');
    chatListContainer.innerHTML = '';

    conversationList.forEach(convo => {
        const newChatItem = document.createElement('div');
        newChatItem.className = 'new_chat_text';
        newChatItem.setAttribute('data-convid', convo.id);
        newChatItem.innerHTML = `<div class="no-select" style="flex:8;color:#262626;">${convo.name}</div>`;
        newChatItem.onclick = function() {
            currentChat = convo;
            document.querySelectorAll('.new_chat_text').forEach(item =>
                item.classList.remove('active-chat')
            );
            newChatItem.classList.add('active-chat');
            renderChatMessages();
        };

        if (convo.id === currentChat.id) {
            newChatItem.classList.add('active-chat');
        }
        chatListContainer.appendChild(newChatItem);
    });
}

// 切换会话函数（通过左侧列表调用）仅需要切换 currentChat 并重新渲染消息
function switchChat(conversationId) {
    const conversation = conversationList.find(conv => conv.id === conversationId);
    if (conversation) {
        currentChat = conversation;
        renderChatMessages();
        updateConversationListDOM();
    }
}
// 获取历史记录函数：从后端加载 chatData，并重构会话列表和当前会话消息
function fetchHistory(userId) {
    axios.post('/api/get_history', { userId: userId })
        .then(response => {
            const historyData = response.data.history;
            if (Array.isArray(historyData)) {
                chatData = historyData;
                const maxId = chatData.reduce((max, message) => Math.max(max, message.id), -1);
                messageIdCounter = maxId + 1;

                // 构建会话列表
                const convMap = new Map();
                chatData.forEach(message => {
                    if (!convMap.has(message.conversationID)) {
                        convMap.set(message.conversationID, {
                            id: message.conversationID,
                            name: message.conversation || '未命名会话'
                        });
                    }
                });

                conversationList = Array.from(convMap.values());
                currentChat = conversationList[0] || {
                    name: '会话1',
                    id: generateConversationId()
                };
                updateConversationListDOM();
                renderChatMessages();
            }
        })
        .catch(error => {
            console.error('获取历史记录时出错：', error);
        });
}
// 页面加载时初始化
document.addEventListener('DOMContentLoaded', function() {
    const userInfo = JSON.parse(sessionStorage.getItem('userInfo'));
    const userId = userInfo ? userInfo.id : 'defaultUser';
    const username = userInfo ? userInfo.uname : 'defaultUser';
    document.getElementById('username_display').textContent = username;
    // 初始化会话列表DOM
    updateConversationListDOM();
    // 加载聊天历史
    fetchHistory(userId);
});
function addCopyButton(textToCopy) {
    const copyBtn = document.createElement('button');
    copyBtn.innerHTML = '📋'; // Unicode 复制图标
    copyBtn.className = 'copy-btn';

    copyBtn.onclick = function() {
        navigator.clipboard.writeText(textToCopy)
            .then(() => {
                // 短暂显示“已复制”反馈（改用 ✓）
                copyBtn.innerHTML = '✓';
                setTimeout(() => {
                    copyBtn.innerHTML = '📋';
                }, 1000);
            })
            .catch(err => {
                console.error('复制失败:', err);
            });
    };
    return copyBtn;
}
function addEditButton(messageId, currentText) {
    const editBtn = document.createElement('button');
    editBtn.innerHTML = '✏️';
    editBtn.className = 'edit-btn';
    editBtn.onclick = function() {
        // 找到要编辑的消息气泡
        const messageBubble = this.closest('.chat_item').querySelector('.myself_chat');

        // 如果已经在编辑状态，则不做任何操作
        if (messageBubble.dataset.editing === 'true') return;

        // 标记为编辑状态
        messageBubble.dataset.editing = 'true';
        const originalContent = messageBubble.textContent;

        // 创建编辑界面
        messageBubble.innerHTML = `
            <textarea class="inline-edit">${originalContent}</textarea>
            <div class="inline-edit-buttons">
                <button class="inline-save">保存</button>
                <button class="inline-cancel">取消</button>
            </div>
        `;

        // 自动聚焦并选中所有文本
        const textarea = messageBubble.querySelector('.inline-edit');
        textarea.focus();
        textarea.select();

        // 保存按钮点击事件
        messageBubble.querySelector('.inline-save').onclick = async function() {
            const newText = textarea.value.trim();
            if (newText && newText !== originalContent) {
                await processMessageUpdate(messageId, newText, originalContent);
            }
            restoreOriginalMessage(messageBubble, originalContent);
        };

        // 取消按钮点击事件
        messageBubble.querySelector('.inline-cancel').onclick = function() {
            restoreOriginalMessage(messageBubble, originalContent);
        };

        // 按Enter保存，ESC取消
        textarea.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' && e.ctrlKey) {
                e.preventDefault();
                messageBubble.querySelector('.inline-save').click();
            } else if (e.key === 'Escape') {
                messageBubble.querySelector('.inline-cancel').click();
            }
        });
    };
    return editBtn;
}

// 处理消息更新逻辑
async function processMessageUpdate(messageId, newText, originalContent) {
    // 1. 更新用户消息内容
    const userMsgIndex = chatData.findIndex(msg => msg.id === messageId);
    if (userMsgIndex === -1) return;

    const originalMsg = chatData[userMsgIndex];
    chatData[userMsgIndex] = {
        ...originalMsg,
        content: newText,
    };

    // 2. 查找对应的AI回复
    const aiReplyIndex = chatData.findIndex(msg =>
        msg.replyTo === messageId && msg.sender === 'backend'
    );

    try {
        if (aiReplyIndex === -1) {
            // 添加"等待回复..."的占位消息
            chatData.splice(userMsgIndex + 1, 0, {
                id: messageIdCounter++,
                sender: 'backend',
                content: "正在生成回复...",
                time: new Date().toLocaleString(),
                conversation: currentChat,
                replyTo: messageId,
            });
        } else {
            // 显示"正在更新回复..."
            chatData[aiReplyIndex].content = "正在更新回复...";
            chatData[aiReplyIndex].isUpdating = true;
        }

        // 发送编辑后的消息到后端
        const response = await axios.post('/api/chat', { message: newText });

        // 处理API响应
        const now = new Date();
        const replyTime = `${now.getFullYear()}/${String(now.getMonth() + 1).padStart(2, '0')}/${String(now.getDate()).padStart(2, '0')} ${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}:${String(now.getSeconds()).padStart(2, '0')}`;

        if (aiReplyIndex === -1) {
            // 替换占位消息
            chatData[userMsgIndex + 1] = {
                id: messageIdCounter++,
                sender: 'backend',
                content: response.data.reply || "未能获取回复",
                time: replyTime,
                conversation: currentChat,
                replyTo: messageId,
            };
        } else {
            // 更新已有回复
            chatData[aiReplyIndex] = {
                ...chatData[aiReplyIndex],
                content: response.data.reply || "未能获取更新后的回复",
                time: replyTime,
            };
        }

        // 更新UI并同步历史
        renderChatMessages();
        //sendHistory();

    } catch (error) {
        console.error('更新回复失败:', error);
        alert('更新回复失败，请重试');
        throw error; // 抛出错误以便外层处理
    }
}

// 恢复原始消息显示
function restoreOriginalMessage(messageBubble, originalContent) {
    messageBubble.textContent = originalContent;
    messageBubble.dataset.editing = 'false';
}
function addReloadButton(messageId) {
    const reloadBtn = document.createElement('button');
    reloadBtn.innerHTML = '🔄';
    reloadBtn.className = 'reload-btn';

    reloadBtn.onclick = async function() {
        // 1. 获取当前消息和关联的用户消息
        const currentMsg = chatData.find(msg => msg.id === messageId);
        if (!currentMsg || currentMsg.sender !== 'backend') return;

        const userMsg = chatData.find(msg => msg.id === currentMsg.replyTo);
        if (!userMsg) {
            console.error('找不到关联的用户消息');
            return;
        }

        // 2. 显示加载状态
        reloadBtn.classList.add('spinning');
        reloadBtn.disabled = true;

        try {
            // 3. 调用API重新生成回答
            const response = await axios.post('/api/chat', {
                message: userMsg.content,

            });

            // 4. 更新现有消息内容
            const updatedMsg = {
                ...currentMsg,
                content: response.data.reply || '重新生成失败',
                time: new Date().toLocaleString(),
                isRegenerated: true  // 标记这是重新生成的消息
            };

            // 5. 替换原消息
            const msgIndex = chatData.findIndex(msg => msg.id === messageId);
            chatData[msgIndex] = updatedMsg;

            // 6. 更新UI
            renderChatMessages();
            //sendHistory();

        } catch (error) {
            console.error('重新生成失败:', error);
            // 可以添加错误提示
        } finally {
            reloadBtn.classList.remove('spinning');
            reloadBtn.disabled = false;
        }
    };

    return reloadBtn;
}
// 在现有JS代码中添加以下函数

// 初始化发送数据按钮
document.getElementById('sendDataBtn').addEventListener('click', function() {
    document.getElementById('jsonFileInput').click();
});

// 监听文件选择
document.getElementById('jsonFileInput').addEventListener('change', handleFileSelect);

// 处理文件选择
function handleFileSelect(event) {
    const file = event.target.files[0];
    if (!file) return;

    // 检查文件类型
    if (file.type !== "application/json" && !file.name.endsWith('.json')) {
        alert('请选择JSON文件');
        return;
    }

    // 显示加载状态
    const btn = document.getElementById('sendDataBtn');
    const originalText = btn.textContent;
    btn.innerHTML = '<span class="loading-spinner"></span>正在上传...';
    btn.disabled = true;

    const reader = new FileReader();
    reader.onload = function(e) {
        try {
            const jsonData = JSON.parse(e.target.result);
            sendJsonData(jsonData);
        } catch (error) {
            alert('解析JSON文件失败: ' + error.message);
            resetButton(btn, originalText);
        }
    };
    reader.onerror = function() {
        alert('读取文件失败');
        resetButton(btn, originalText);
    };
    reader.readAsText(file);
}

// 发送JSON数据到服务器
function sendJsonData(jsonData) {
    const btn = document.getElementById('sendDataBtn');
    const originalText = btn.textContent;

    axios.post('/api/upload_data', jsonData, {
        headers: {
            'Content-Type': 'application/json'
        }
    })
        .then(response => {
            alert('数据发送成功！');
            // 可以在这里处理服务器返回的数据
            console.log('服务器响应:', response.data);
        })
        .catch(error => {
            console.error('发送数据失败:', error);
            alert('发送数据失败: ' + (error.response?.data?.message || error.message));
        })
        .finally(() => {
            resetButton(btn, '发送数据');
            // 清空文件输入，允许重复选择同一文件
            document.getElementById('jsonFileInput').value = '';
        });
}

// 重置按钮状态
function resetButton(button, text) {
    button.textContent = text;
    button.disabled = false;
}
function setupDownloadButton() {
    const downloadBtn = document.getElementById('downloadDataBtn');

    downloadBtn.addEventListener('click', function() {
        // 显示加载状态
        const originalText = downloadBtn.textContent;
        downloadBtn.innerHTML = '<span class="loading-spinner"></span>下载中...';
        downloadBtn.disabled = true;

        // 调用Spring Boot后端下载接口
        fetch('/api/download/json')
            .then(response => {
                if (!response.ok) throw new Error('下载失败');
                return response.blob();
            })
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = '数据';
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
            })
            .catch(error => {
                alert(error.message);
            })
            .finally(() => {
                downloadBtn.textContent = originalText;
                downloadBtn.disabled = false;
            });
    });
}


// 在DOM加载时初始化
document.addEventListener('DOMContentLoaded', setupDownloadButton);
// 在DOM加载完成后初始化
document.addEventListener('DOMContentLoaded', function() {
    setupDownloadButton(); // 添加这行初始化下载按钮
    // ... 其他初始化代码 ...
});
// 生成唯一会话ID
function generateConversationId() {
    return Date.now().toString(36) + Math.random().toString(36).substr(2, 5);
}
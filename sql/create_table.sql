create table chat_list
(
    id              bigint auto_increment comment '主键ID'
        primary key,
    user_id         bigint       not null comment '用户ID',
    conversation_id bigint       not null comment '会话ID',
    description     varchar(512) null comment '聊天描述'
)
    comment '聊天列表' collate = utf8mb4_unicode_ci;

create index idx_conversation_id
    on chat_list (conversation_id);

create index idx_user_id
    on chat_list (user_id);

create table chat_message
(
    id              bigint auto_increment comment '消息ID'
        primary key,
    message         text     not null comment '用户消息',
    reply           text     null comment '回复消息',
    conversation_id bigint   not null comment '会话ID',
    user_id         bigint   not null comment '发送消息的用户ID',
    time            datetime not null comment '消息时间'
)
    comment '聊天消息' collate = utf8mb4_unicode_ci;

create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256) not null comment '账号',
    userPassword varchar(512) not null comment '密码',
    userName     varchar(256) null comment '用户名'
)
    comment '用户' collate = utf8mb4_unicode_ci;


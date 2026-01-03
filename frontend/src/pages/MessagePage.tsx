import React, { useEffect, useRef, useState } from "react";
import styles from "./message.module.css";
import {
    createAiConversation,
    sendAiMessage,
    getAiMessages,
    getAiConversations,
} from "../services/ai";
import { AxiosResponse } from "axios";

type ConversationType = "AI" | "PRIVATE" | "GROUP";

interface ApiConversation {
    id: number;
    title: string;
    type: string;
    updatedAt?: string;
}

interface Conversation {
    id: number;
    title: string;
    type: ConversationType;
}

interface ApiMessage {
    id: number;
    senderType: "USER" | "AI";
    content: string;
}

interface Message {
    id: number;
    senderType: "USER" | "AI";
    content: string;
}

function mapConversation(c: ApiConversation): Conversation {
    return {
        id: c.id,
        title: c.title,
        type: c.type === "PRIVATE" || c.type === "GROUP" ? c.type : "AI",
    };
}

const MessagePage: React.FC = () => {
    const [conversations, setConversations] = useState<Conversation[]>([]);
    const [activeConversation, setActiveConversation] =
        useState<Conversation | null>(null);

    const [messagesMap, setMessagesMap] = useState<Record<number, Message[]>>(
        {}
    );

    const [input, setInput] = useState("");
    const [sending, setSending] = useState(false);
    const [loading, setLoading] = useState(true);

    const messageEndRef = useRef<HTMLDivElement | null>(null);

    useEffect(() => {
        loadConversations();
    }, []);

    /** 加载会话列表 */
    const loadConversations = async () => {
        setLoading(true);
        try {
            const res: AxiosResponse<ApiConversation[]> =
                await getAiConversations();

            console.log("加载的会话列表响应:", res.data);

            const list = res.data.map(mapConversation);
            console.log("转换后的列表:", list);

            setConversations(list);

            // 如果有会话，设置第一个为活跃会话
            if (list.length > 0) {
                setActiveConversation(list[0]);
                loadMessages(list[0].id);
            } else {
                setActiveConversation(null);
            }
        } catch (error) {
            console.error("加载会话失败:", error);
            setConversations([]);
        } finally {
            setLoading(false);
        }
    };

    /** 加载消息 */
    const loadMessages = async (conversationId: number) => {
        try {
            const res: AxiosResponse<ApiMessage[]> = await getAiMessages(conversationId);

            setMessagesMap((prev) => ({
                ...prev,
                [conversationId]: res.data,
            }));
        } catch (error) {
            console.error("加载消息失败:", error);
        }
    };

    /** 新建 AI 会话 */
    const handleCreateAi = async () => {
        if (sending) return;

        setSending(true);

        try {
            console.log("开始创建 AI 会话...");

            const res: AxiosResponse<ApiConversation> =
                await createAiConversation();

            console.log("API 响应:", res.data);

            const conv = mapConversation(res.data);
            console.log("转换后的会话:", conv);

            // 使用函数式更新确保状态正确
            setConversations(prev => {
                // 检查是否已存在相同 ID 的会话
                const exists = prev.some(c => c.id === conv.id);
                if (exists) {
                    console.warn("会话已存在:", conv.id);
                    return prev;
                }
                return [conv, ...prev];
            });

            // 设置活跃会话
            setActiveConversation(conv);

            // 初始化消息数组
            setMessagesMap(prev => ({
                ...prev,
                [conv.id]: []
            }));

        } catch (error) {
            console.error("创建 AI 会话失败:", error);
            alert("创建会话失败，请稍后重试");
        } finally {
            setSending(false);
        }
    };

    /** 发送消息 */
    const handleSend = async () => {
        if (!input.trim() || !activeConversation || sending) return;

        setSending(true);

        const userMsg: Message = {
            id: Date.now(),
            senderType: "USER",
            content: input.trim(),
        };

        setMessagesMap((prev) => ({
            ...prev,
            [activeConversation.id]: [
                ...(prev[activeConversation.id] || []),
                userMsg,
            ],
        }));

        const content = input.trim();
        setInput("");

        try {
            const res: AxiosResponse<ApiMessage> =
                await sendAiMessage(activeConversation.id, content);

            const ai = res.data;

            setMessagesMap((prev) => ({
                ...prev,
                [activeConversation.id]: [
                    ...(prev[activeConversation.id] || []),
                    {
                        id: ai.id,
                        senderType: ai.senderType,
                        content: ai.content,
                    },
                ],
            }));
        } catch (error) {
            console.error("发送消息失败:", error);
            setMessagesMap((prev) => ({
                ...prev,
                [activeConversation.id]: [
                    ...(prev[activeConversation.id] || []),
                    {
                        id: Date.now(),
                        senderType: "AI",
                        content: "⚠️ AI 服务暂时不可用",
                    },
                ],
            }));
        } finally {
            setSending(false);
        }
    };

    useEffect(() => {
        messageEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messagesMap, activeConversation]);

    return (
        <div className={styles.pageRoot}>
            {/* 临时调试区域 - 上线前删除 */}
            <div style={{
                position: 'fixed',
                top: 0,
                right: 0,
                background: 'rgba(0,0,0,0.8)',
                color: 'white',
                padding: '10px',
                zIndex: 9999,
                fontSize: '12px'
            }}>
                <div>会话数: {conversations.length}</div>
                <div>活跃会话ID: {activeConversation?.id || '无'}</div>
                <div>最新会话: {conversations[0]?.title || '无'}</div>
                <div>加载状态: {loading ? '加载中...' : '加载完成'}</div>
            </div>

            <div className={styles.container}>
                <aside className={styles.sidebar}>
                    <div className={styles.sidebarHeader}>
                        <h3>消息</h3>
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <button
                                className={styles.newChatBtn}
                                onClick={handleCreateAi}
                                disabled={sending || loading}
                            >
                                {sending ? "创建中..." : "＋ 新建 AI"}
                            </button>
                        </div>
                    </div>

                    <div className={styles.conversationList}>
                        {loading ? (
                            <div className={styles.loading}>加载中...</div>
                        ) : conversations.length === 0 ? (
                            <div className={styles.emptyList}>暂无会话</div>
                        ) : (
                            conversations.map((c) => (
                                <div
                                    key={c.id}
                                    className={`${styles.conversationItem} ${
                                        activeConversation?.id === c.id
                                            ? styles.active
                                            : ""
                                    }`}
                                    onClick={() => {
                                        setActiveConversation(c);
                                        loadMessages(c.id);
                                    }}
                                >
                                    <span className={styles.icon}>🤖</span>
                                    <span className={styles.title}>
                                        {c.title}
                                    </span>
                                </div>
                            ))
                        )}
                    </div>
                </aside>

                <main className={styles.chatPanel}>
                    {loading ? (
                        <div className={styles.empty}>加载中...</div>
                    ) : activeConversation ? (
                        <>
                            <div className={styles.chatHeader}>
                                <h3>{activeConversation.title}</h3>
                            </div>

                            <div className={styles.messageList}>
                                {(messagesMap[activeConversation.id] || []).map(
                                    (msg) => (
                                        <div
                                            key={msg.id}
                                            className={`${styles.messageItem} ${
                                                msg.senderType === "USER"
                                                    ? styles.fromUser
                                                    : styles.fromAI
                                            }`}
                                        >
                                            <div className={styles.bubble}>
                                                {msg.content}
                                            </div>
                                        </div>
                                    )
                                )}
                                <div ref={messageEndRef} />
                            </div>

                            <div className={styles.inputBox}>
                                <textarea
                                    value={input}
                                    onChange={(e) =>
                                        setInput(e.target.value)
                                    }
                                    placeholder="向 AI 提问..."
                                    onKeyDown={(e) => {
                                        if (
                                            e.key === "Enter" &&
                                            !e.shiftKey
                                        ) {
                                            e.preventDefault();
                                            handleSend();
                                        }
                                    }}
                                    disabled={sending}
                                />
                                <button
                                    disabled={!input.trim() || sending}
                                    onClick={handleSend}
                                >
                                    {sending ? "发送中..." : "发送"}
                                </button>
                            </div>
                        </>
                    ) : (
                        <div className={styles.empty}>
                            {conversations.length === 0 ? "暂无会话，点击上方按钮创建" : "请选择一个会话"}
                        </div>
                    )}
                </main>
            </div>
        </div>
    );
};

export default MessagePage;
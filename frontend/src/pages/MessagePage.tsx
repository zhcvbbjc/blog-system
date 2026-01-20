import React, { useEffect, useRef, useState } from "react";
import styles from "./message.module.css";
import {
    createAiConversation,
    sendAiMessage,
    getAiMessages,
    getAiConversations,
    updateConversationTitle,   // 👈 新增
    deleteConversation,        // 👈 新增
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

            const list = res.data.map(mapConversation);
            setConversations(list);

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
            const res: AxiosResponse<ApiConversation> = await createAiConversation();
            const conv = mapConversation(res.data);

            setConversations((prev) => {
                const exists = prev.some(c => c.id === conv.id);
                if (exists) return prev;
                return [conv, ...prev];
            });

            setActiveConversation(conv);
            setMessagesMap((prev) => ({
                ...prev,
                [conv.id]: [],
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
            [activeConversation.id]: [...(prev[activeConversation.id] || []), userMsg],
        }));

        const content = input.trim();
        setInput("");

        try {
            const res: AxiosResponse<ApiMessage> = await sendAiMessage(activeConversation.id, content);
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

    /** 修改会话标题 */
    const handleUpdateTitle = async (conversation: Conversation) => {
        const newTitle = prompt("请输入新标题", conversation.title);
        if (!newTitle || newTitle.trim() === "" || newTitle.trim() === conversation.title) {
            return;
        }

        try {
            await updateConversationTitle(conversation.id, newTitle.trim());
            // 更新本地状态
            setConversations(prev =>
                prev.map(c => c.id === conversation.id ? { ...c, title: newTitle.trim() } : c)
            );
            // 如果是当前激活的会话，也更新它
            if (activeConversation?.id === conversation.id) {
                setActiveConversation({ ...activeConversation, title: newTitle.trim() });
            }
        } catch (error) {
            console.error("修改标题失败:", error);
            alert("修改标题失败，请稍后重试");
        }
    };

    /** 删除会话 */
    const handleDeleteConversation = async (conversationId: number) => {
        if (conversations.length <= 1) {
            alert("至少保留一个会话");
            return;
        }

        if (!confirm("确定要删除这个会话吗？")) {
            return;
        }

        try {
            await deleteConversation(conversationId);

            // 从列表中移除
            const updated = conversations.filter(c => c.id !== conversationId);
            setConversations(updated);

            // 如果删除的是当前激活的会话，切换到第一个
            if (activeConversation?.id === conversationId) {
                const newActive = updated[0];
                if (newActive) {
                    setActiveConversation(newActive);
                    loadMessages(newActive.id);
                } else {
                    setActiveConversation(null);
                }
            }
        } catch (error) {
            console.error("删除会话失败:", error);
            alert("删除失败，请稍后重试");
        }
    };

    useEffect(() => {
        messageEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messagesMap, activeConversation]);

    return (
        <div className={styles.pageRoot}>
            {/* 调试面板（上线前可删除） */}
            <div className={styles.debugPanel}>
                <div>会话数: {conversations.length}</div>
                <div>活跃会话ID: {activeConversation?.id || '无'}</div>
                <div>最新会话: {conversations[0]?.title || '无'}</div>
                <div>加载状态: {loading ? '加载中...' : '加载完成'}</div>
            </div>

            <div className={styles.container}>
                <aside className={styles.sidebar}>
                    <div className={styles.sidebarHeader}>
                        <h3>消息</h3>
                        <button
                            className={styles.newChatBtn}
                            onClick={handleCreateAi}
                            disabled={sending || loading}
                        >
                            {sending ? "创建中..." : "＋ 新建 AI"}
                        </button>
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
                                        activeConversation?.id === c.id ? styles.active : ""
                                    }`}
                                >
                                    {/* 左侧：点击切换会话 */}
                                    <div
                                        className={styles.conversationContent}
                                        onClick={() => {
                                            setActiveConversation(c);
                                            loadMessages(c.id);
                                        }}
                                    >
                                        <span className={styles.icon}>🤖</span>
                                        <span className={styles.title}>{c.title}</span>
                                    </div>

                                    {/* 右侧：操作按钮 */}
                                    <div className={styles.conversationActions}>
                                        <button
                                            className={styles.actionBtn}
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                handleUpdateTitle(c);
                                            }}
                                            title="修改标题"
                                        >
                                            ✏️
                                        </button>
                                        <button
                                            className={styles.actionBtn}
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                handleDeleteConversation(c.id);
                                            }}
                                            title="删除会话"
                                        >
                                            🗑️
                                        </button>
                                    </div>
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
                                {(messagesMap[activeConversation.id] || []).map((msg) => (
                                    <div
                                        key={msg.id}
                                        className={`${styles.messageItem} ${
                                            msg.senderType === "USER"
                                                ? styles.fromUser
                                                : styles.fromAI
                                        }`}
                                    >
                                        <div className={styles.bubble}>{msg.content}</div>
                                    </div>
                                ))}
                                <div ref={messageEndRef} />
                            </div>

                            <div className={styles.inputBox}>
                                <textarea
                                    value={input}
                                    onChange={(e) => setInput(e.target.value)}
                                    placeholder="向 AI 提问..."
                                    onKeyDown={(e) => {
                                        if (e.key === "Enter" && !e.shiftKey) {
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
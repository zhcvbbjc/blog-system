import request from "./request";

export interface ApiConversation {
    id: number;
    title: string;
    type: string;
    updatedAt?: string;  // 后端返回了这个字段
}

export interface ApiMessage {
    id: number;
    senderType: "USER" | "AI";
    senderId: number | null;
    content: string;
    createdAt: string;
}

/** 创建 AI 会话 */
export function createAiConversation() {
    return request.post<ApiConversation>("/ai/chat/conversations");
}

/** 获取 AI 会话列表 */
export function getAiConversations() {
    return request.get<ApiConversation[]>("/ai/chat/conversations");
}

/** 发送消息 */
export function sendAiMessage(conversationId: number, content: string) {
    return request.post<ApiMessage>(`/ai/chat/${conversationId}/messages`, {
        content,
    });
}

/** 获取历史消息 */
export function getAiMessages(conversationId: number) {
    return request.get<ApiMessage[]>(`/ai/chat/${conversationId}/messages`);
}
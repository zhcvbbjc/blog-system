// API 基础URL
const API_BASE = '/api';

// 工具函数
function getToken() {
    return localStorage.getItem('token');
}

function setToken(token) {
    localStorage.setItem('token', token);
}

function removeToken() {
    localStorage.removeItem('token');
}

function getAuthHeaders() {
    const token = getToken();
    return {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` })
    };
}

// API 请求封装
async function apiRequest(url, options = {}) {
    const config = {
        headers: getAuthHeaders(),
        ...options
    };

    try {
        const response = await fetch(`${API_BASE}${url}`, config);
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || '请求失败');
        }

        return data.data;
    } catch (error) {
        console.error('API请求错误:', error);
        throw error;
    }
}

// 搜索功能
function search() {
    const keyword = document.getElementById('searchInput')?.value;
    if (keyword) {
        window.location.href = `/search?q=${encodeURIComponent(keyword)}`;
    }
}

// 回车搜索
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                search();
            }
        });
    }
});

// 用户菜单下拉
function toggleDropdown() {
    const dropdown = document.getElementById('userDropdown');
    if (dropdown) {
        dropdown.classList.toggle('show');
    }
}

// 关闭下拉菜单（点击外部）
document.addEventListener('click', function(event) {
    const dropdown = document.getElementById('userDropdown');
    const userMenu = document.querySelector('.user-menu');
    
    if (dropdown && userMenu && !userMenu.contains(event.target)) {
        dropdown.classList.remove('show');
    }
});

// 退出登录
async function logout() {
    try {
        removeToken();
        window.location.href = '/login';
    } catch (error) {
        console.error('退出登录失败:', error);
    }
}

// 点赞文章
async function likeArticle(articleId) {
    try {
        await apiRequest(`/likes/article/${articleId}`, {
            method: 'POST'
        });
        
        // 更新点赞状态
        const likeBtn = document.querySelector(`[data-article-id="${articleId}"].like-btn`);
        if (likeBtn) {
            likeBtn.classList.add('active');
            const count = likeBtn.querySelector('.count');
            if (count) {
                count.textContent = parseInt(count.textContent) + 1;
            }
        }
        
        showMessage('点赞成功', 'success');
    } catch (error) {
        showMessage(error.message || '点赞失败', 'error');
    }
}

// 取消点赞
async function unlikeArticle(articleId) {
    try {
        await apiRequest(`/likes/article/${articleId}`, {
            method: 'DELETE'
        });
        
        // 更新点赞状态
        const likeBtn = document.querySelector(`[data-article-id="${articleId}"].like-btn`);
        if (likeBtn) {
            likeBtn.classList.remove('active');
            const count = likeBtn.querySelector('.count');
            if (count) {
                count.textContent = Math.max(0, parseInt(count.textContent) - 1);
            }
        }
        
        showMessage('取消点赞成功', 'success');
    } catch (error) {
        showMessage(error.message || '操作失败', 'error');
    }
}

// 切换点赞状态
function toggleLike(articleId) {
    const likeBtn = document.querySelector(`[data-article-id="${articleId}"].like-btn`);
    if (likeBtn && likeBtn.classList.contains('active')) {
        unlikeArticle(articleId);
    } else {
        likeArticle(articleId);
    }
}

// 检查点赞状态
async function checkLikeStatus(articleId) {
    try {
        const isLiked = await apiRequest(`/likes/article/${articleId}/status`);
        const likeBtn = document.querySelector(`[data-article-id="${articleId}"].like-btn`);
        if (likeBtn) {
            if (isLiked) {
                likeBtn.classList.add('active');
            }
        }
    } catch (error) {
        console.error('检查点赞状态失败:', error);
    }
}

// 消息提示
function showMessage(message, type = 'info') {
    // 创建消息元素
    const messageDiv = document.createElement('div');
    messageDiv.className = `message message-${type}`;
    messageDiv.textContent = message;
    
    // 添加到页面
    document.body.appendChild(messageDiv);
    
    // 显示动画
    setTimeout(() => messageDiv.classList.add('show'), 10);
    
    // 3秒后移除
    setTimeout(() => {
        messageDiv.classList.remove('show');
        setTimeout(() => messageDiv.remove(), 300);
    }, 3000);
}

// 格式化日期
function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;
    const seconds = Math.floor(diff / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) {
        return `${days}天前`;
    } else if (hours > 0) {
        return `${hours}小时前`;
    } else if (minutes > 0) {
        return `${minutes}分钟前`;
    } else {
        return '刚刚';
    }
}

// 加载文章列表
async function loadArticles(page = 0, size = 10, tag = null) {
    try {
        let url = `/articles?page=${page}&size=${size}`;
        if (tag) {
            url += `&tag=${encodeURIComponent(tag)}`;
        }
        
        const data = await apiRequest(url, { method: 'GET' });
        return data;
    } catch (error) {
        console.error('加载文章失败:', error);
        throw error;
    }
}

// 分享功能
function shareArticle(title, url) {
    if (navigator.share) {
        navigator.share({
            title: title,
            url: url
        }).catch(err => console.error('分享失败:', err));
    } else {
        // 复制链接到剪贴板
        navigator.clipboard.writeText(url).then(() => {
            showMessage('链接已复制到剪贴板', 'success');
        }).catch(err => {
            console.error('复制失败:', err);
        });
    }
}


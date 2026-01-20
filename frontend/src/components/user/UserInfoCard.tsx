// src/components/user/UserInfoCard.tsx
import { Link } from 'react-router-dom';
import styles from './user-info-card.module.css';

interface UserInfoCardProps {
    username: string;
    bio?: string;
    articleCount: number;
    likeCount: number;
    userId: string;
    onClose?: () => void;
}

function UserInfoCard({
                          username,
                          bio,
                          articleCount,
                          likeCount,
                          userId,
                          onClose
                      }: UserInfoCardProps) {
    return (
        <div className={styles.card}>
            {/* 关闭按钮 */}
            {onClose && (
                <button onClick={onClose} className={styles.closeButton} aria-label="关闭">
                    ×
                </button>
            )}

            {/* 头像 */}
            <div className={styles.avatar}>
                {username.charAt(0).toUpperCase()}
            </div>

            {/* 用户信息 */}
            <div className={styles.info}>
                <h3 className={styles.username}>{username}</h3>
                <p className={styles.bio}>
                    {bio || '这位用户很神秘，什么也没写。'}
                </p>

                <div className={styles.stats}>
                    <div className={styles.statItem}>
                        <span className={styles.statLabel}>文章</span>
                        <span className={styles.statValue}>{articleCount}</span>
                    </div>
                    <div className={styles.statItem}>
                        <span className={styles.statLabel}>获赞</span>
                        <span className={styles.statValue}>{likeCount}</span>
                    </div>
                </div>
            </div>

            {/* 操作按钮 */}
            <div className={styles.actions}>
                <Link to={`/users/${userId}`} className={styles.button}>
                    进入主页
                </Link>
                <button className={`${styles.button} ${styles.primary}`}>
                    发送消息
                </button>
            </div>
        </div>
    );
}

export default UserInfoCard;
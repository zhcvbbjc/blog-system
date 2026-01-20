import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { articleService } from '../../services/article';
import styles from './articleCreate.module.css';

function ArticleCreatePage() {
    const navigate = useNavigate();

    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [tags, setTags] = useState('');

    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            await articleService.create({
                title,
                content,
                tags: tags.split(',').map(t => t.trim())
            });

            navigate('/articles'); //创建成功后跳转
        } catch (err: any) {
            setError(err.message || '创建文章失败');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className={styles.wrapper}>
            <div className={styles.card}>
                <h1 className={styles.title}>✍️ 创作文章</h1>

                <form className={styles.form} onSubmit={handleSubmit}>
                    {error && <p className={styles.error}>{error}</p>}

                    <label className={styles.label}>标题</label>
                    <input
                        type="text"
                        value={title}
                        onChange={e => setTitle(e.target.value)}
                        required
                        className={styles.input}
                        placeholder="输入文章标题"
                    />

                    <label className={styles.label}>内容</label>
                    <textarea
                        value={content}
                        onChange={e => setContent(e.target.value)}
                        rows={10}
                        required
                        className={styles.textarea}
                        placeholder="输入内容..."
                    />

                    <label className={styles.label}>标签（使用逗号分隔）</label>
                    <input
                        type="text"
                        value={tags}
                        onChange={e => setTags(e.target.value)}
                        className={styles.input}
                        placeholder="例如：AI, React, Spring"
                    />

                    <button type="submit" disabled={loading} className={styles.button}>
                        {loading ? '提交中...' : '发布文章'}
                    </button>
                </form>
            </div>
        </div>
    );
}

export default ArticleCreatePage;

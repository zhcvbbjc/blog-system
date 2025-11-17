import styles from './loading.module.css';

interface LoadingProps {
  message?: string;
}

function Loading({ message = '加载中...' }: LoadingProps) {
  return (
    <div className={styles.wrapper}>
      <div className={styles.spinner} />
      <span>{message}</span>
    </div>
  );
}

export default Loading;


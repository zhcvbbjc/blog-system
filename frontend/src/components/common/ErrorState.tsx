import styles from './error-state.module.css';

interface ErrorStateProps {
  message?: string;
  retry?: () => void;
}

function ErrorState({ message = '出错了，请稍后重试', retry }: ErrorStateProps) {
  return (
    <div className={styles.wrapper}>
      <p>{message}</p>
      {retry && (
        <button type="button" onClick={retry}>
          重试
        </button>
      )}
    </div>
  );
}

export default ErrorState;


import styles from './footer.module.css';

function Footer() {
  return (
    <footer className={styles.footer}>
      <div className={styles.inner}>
        <p>© {new Date().getFullYear()} 智能博客系统 · 由 Spring Boot + React 驱动</p>
      </div>
    </footer>
  );
}

export default Footer;


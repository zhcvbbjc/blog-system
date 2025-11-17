import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import styles from './navbar.module.css';

function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className={styles.navbar}>
      <div className={styles.inner}>
        <Link to="/" className={styles.brand}>
          智能博客
        </Link>
        <nav className={styles.links}>
          <NavLink to="/" className={({ isActive }) => (isActive ? styles.active : undefined)}>
            首页
          </NavLink>
          <NavLink
            to="/articles"
            className={({ isActive }) => (isActive ? styles.active : undefined)}
          >
            文章
          </NavLink>
          {user && (
            <NavLink
              to="/dashboard"
              className={({ isActive }) => (isActive ? styles.active : undefined)}
            >
              控制台
            </NavLink>
          )}
        </nav>
        <div className={styles.actions}>
          {user ? (
            <>
              <Link to="/profile" className={styles.avatar}>
                {user.avatarUrl ? (
                  <img src={user.avatarUrl} alt={user.username} />
                ) : (
                  user.username[0]?.toUpperCase()
                )}
              </Link>
              <button onClick={handleLogout} className={styles.logoutBtn}>
                退出
              </button>
            </>
          ) : (
            <>
              <Link to="/login" className={styles.secondaryBtn}>
                登录
              </Link>
              <Link to="/register" className={styles.primaryBtn}>
                注册
              </Link>
            </>
          )}
        </div>
      </div>
    </header>
  );
}

export default Navbar;


import { useQuery } from '@tanstack/react-query';
import { authService } from '../services/auth';
import Loading from '../components/common/Loading';
import ErrorState from '../components/common/ErrorState';
import styles from './profile.module.css';

function ProfilePage() {
  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['profile'],
    queryFn: () => authService.profile()
  });

  if (isLoading) return <Loading />;
  if (error) return <ErrorState message={error.message} retry={refetch} />;
  if (!data) return null;

  return (
    <div className={styles.card}>
      <div className={styles.header}>
        <div className={styles.avatar}>
          {data.avatarUrl ? <img src={data.avatarUrl} alt={data.username} /> : data.username[0]}
        </div>
        <div>
          <h1>{data.username}</h1>
          <p>{data.email}</p>
        </div>
      </div>
      <dl className={styles.meta}>
        <div>
          <dt>角色</dt>
          <dd>{data.role}</dd>
        </div>
        <div>
          <dt>加入时间</dt>
            <dd>
                {data.createdAt
                    ? new Date(data.createdAt).toLocaleDateString()
                    : '未知'}
            </dd>
        </div>
      </dl>
      {data.bio && (
        <section className={styles.bio}>
          <h2>简介</h2>
          <p>{data.bio}</p>
        </section>
      )}
    </div>
  );
}

export default ProfilePage;


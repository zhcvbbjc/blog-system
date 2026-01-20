import { Navigate, Route, Routes } from 'react-router-dom';
import Layout from './components/layout/Layout';
import HomePage from './pages/HomePage';
import ArticlesPage from './pages/articles/ArticlesPage';
import ArticleDetailPage from './pages/articles/ArticleDetailPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import ProfilePage from './pages/ProfilePage';
import ProtectedRoute from './components/common/ProtectedRoute';
import ArticleCreatePage from './pages/articles/ArticleCreatePage';
import MessagePage from "./pages/MessagePage";
import ToolPage from "./pages/ToolPage";
import NewsPage from "./pages/Tools/NewsPage";
import MarketPage from "./pages/Tools/MarketPage";
import KnowledgePage from "./pages/Tools/KnowledgePage";
import AnalysisToolPage from "./pages/Tools/AnalysisToolPage";
import ArticleCommentPage from "./pages/articles/ArticleCommentPage";

function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/articles" element={<ArticlesPage />} />
        <Route path="/articles/:slug" element={<ArticleDetailPage />} />
        <Route path="/articles/:slug/comment" element={<ArticleCommentPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/messages" element={<MessagePage />} />
        <Route path="/tools" element={<ToolPage />} />
        <Route path="/tools/news" element={<NewsPage />} />
        <Route path="/tools/market" element={<MarketPage />} />
        <Route path="/tools/knowledge" element={<KnowledgePage />} />
        <Route path="/tools/analysis" element={<AnalysisToolPage />} />
        <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        }
        />
        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <ProfilePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/articles/create"
          element={
              <ProtectedRoute>
                  <ArticleCreatePage />
              </ProtectedRoute>
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Layout>
  );
}

export default App;


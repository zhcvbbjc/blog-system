blog-system/
├── README.md                          # 项目说明文档
├── pom.xml                           # Maven 项目配置
├── docker-compose.yml                # Docker 容器编排
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │           └── blog/
│   │   │               ├── BlogApplication.java       # 应用启动类
│   │   │               ├── config/                    # 配置类目录
│   │   │               │   ├── SecurityConfig.java    # 安全配置
│   │   │               │   ├── JpaConfig.java         # JPA 配置
│   │   │               │   ├── OpenAIConfig.java      # Spring AI 配置
│   │   │               │   ├── RedisConfig.java       # Redis 配置
│   │   │               │   └── WebConfig.java         # Web 配置
│   │   │               ├── controller/                # 控制器层
│   │   │               │   ├── AuthController.java    # 认证相关接口
│   │   │               │   ├── ArticleController.java # 文章管理接口
│   │   │               │   ├── CommentController.java # 评论管理接口
│   │   │               │   ├── UserController.java    # 用户管理接口
│   │   │               │   └── ProfileController.java # 个人主页接口
│   │   │               ├── service/                   # 服务层
│   │   │               │   ├── UserService.java       # 用户服务
│   │   │               │   ├── ArticleService.java    # 文章服务
│   │   │               │   ├── CommentService.java    # 评论服务
│   │   │               │   ├── AIContentService.java  # AI 内容服务
│   │   │               │   ├── RecommendationService.java # 推荐服务
│   │   │               │   └── SearchService.java     # 搜索服务
│   │   │               ├── repository/                # 数据访问层
│   │   │               │   ├── UserRepository.java    # 用户数据访问
│   │   │               │   ├── ArticleRepository.java # 文章数据访问
│   │   │               │   ├── CommentRepository.java # 评论数据访问
│   │   │               │   └── LikeRepository.java    # 点赞数据访问
│   │   │               ├── entity/                    # 实体类
│   │   │               │   ├── User.java              # 用户实体
│   │   │               │   ├── Article.java           # 文章实体
│   │   │               │   ├── Comment.java           # 评论实体
│   │   │               │   ├── Like.java              # 点赞实体
│   │   │               │   └── Tag.java               # 标签实体
│   │   │               ├── dto/                       # 数据传输对象
│   │   │               │   ├── request/               # 请求 DTO
│   │   │               │   │   ├── LoginRequest.java
│   │   │               │   │   ├── RegisterRequest.java
│   │   │               │   │   ├── ArticleRequest.java
│   │   │               │   │   └── CommentRequest.java
│   │   │               │   └── response/              # 响应 DTO
│   │   │               │       ├── UserResponse.java
│   │   │               │       ├── ArticleResponse.java
│   │   │               │       ├── ProfileResponse.java
│   │   │               │       └── ApiResponse.java
│   │   │               ├── security/                  # 安全相关
│   │   │               │   ├── JwtTokenProvider.java  # JWT 令牌提供者
│   │   │               │   ├── JwtAuthenticationFilter.java # JWT 认证过滤器
│   │   │               │   └── CustomUserDetails.java # 用户详情
│   │   │               ├── exception/                 # 异常处理
│   │   │               │   ├── GlobalExceptionHandler.java # 全局异常处理
│   │   │               │   ├── BlogException.java     # 自定义异常
│   │   │               │   └── ErrorResponse.java     # 错误响应
│   │   │               └── util/                      # 工具类
│   │   │                   ├── FileUploadUtil.java    # 文件上传工具
│   │   │                   ├── SlugUtil.java          # Slug 生成工具
│   │   │                   └── DateUtil.java          # 日期工具
│   │   └── resources/
│   │       ├── application.yml            # 主配置文件
│   │       ├── application-dev.yml        # 开发环境配置
│   │       ├── application-prod.yml       # 生产环境配置
│   │       ├── static/                    # 静态资源
│   │       │   ├── css/                   # CSS 文件
│   │       │   ├── js/                    # JavaScript 文件
│   │       │   └── images/                # 图片资源
│   │       └── templates/                 # 模板文件
│   └── test/
│       └── java/
│           └── com/
│               └── yourdomain/
│                   └── blog/
│                       ├── service/       # 服务层测试
│                       └── controller/    # 控制器测试
├── docs/                              # 项目文档
│   ├── api.md                         # API 接口文档
│   ├── database.md                    # 数据库设计文档
│   └── deployment.md                  # 部署文档
└── scripts/                           # 部署脚本
    ├── deploy.sh                      # 部署脚本
    └── backup.sh                      # 备份脚本
import dotenv from 'dotenv';
dotenv.config();

export const ENV = {
  PORT: process.env.PORT || 3000,
  NODE_ENV: process.env.NODE_ENV || 'development',
  APP_NAME: process.env.APP_NAME || 'Entre Copas',
  DB: {
    HOST: process.env.DB_HOST || 'localhost',
    PORT: parseInt(process.env.DB_PORT || '3306', 10),
    USER: process.env.DB_USER || 'root',
    PASSWORD: process.env.DB_PASSWORD || '',
    NAME: process.env.DB_NAME || 'entrecopas_db'
  },
  JWT: {
    SECRET: process.env.JWT_SECRET || 'entre_copas_secret_default_key_2026',
    EXPIRES_IN: process.env.JWT_EXPIRES_IN || '24h'
  },
  BCRYPT_ROUNDS: parseInt(process.env.BCRYPT_SALT_ROUNDS || '12', 10),
  CORS_ORIGIN: process.env.CORS_ORIGIN || 'http://localhost:3000'
};

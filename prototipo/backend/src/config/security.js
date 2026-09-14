import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import { ENV } from './env.js';

export async function hashPassword(password) {
  const salt = await bcrypt.genSalt(ENV.BCRYPT_ROUNDS);
  return bcrypt.hash(password, salt);
}

export async function comparePassword(password, hashedPassword) {
  return bcrypt.compare(password, hashedPassword);
}

export function generateToken(payload) {
  return jwt.sign(payload, ENV.JWT.SECRET, {
    expiresIn: ENV.JWT.EXPIRES_IN
  });
}

export function verifyToken(token) {
  try {
    return jwt.verify(token, ENV.JWT.SECRET);
  } catch (err) {
    return null;
  }
}

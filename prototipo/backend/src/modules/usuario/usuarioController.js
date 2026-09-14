import { loginUser, registerUser, getUserProfile } from './usuarioService.js';

export async function login(req, res, next) {
  try {
    const { email, password } = req.body;
    const result = await loginUser(email, password);
    res.json({
      success: true,
      message: 'Inicio de sesión exitoso.',
      data: result
    });
  } catch (err) {
    next(err);
  }
}

export async function register(req, res, next) {
  try {
    const result = await registerUser(req.body);
    res.status(201).json({
      success: true,
      message: 'Usuario registrado exitosamente.',
      data: result
    });
  } catch (err) {
    next(err);
  }
}

export async function me(req, res, next) {
  try {
    const profile = await getUserProfile(req.user.id);
    res.json({
      success: true,
      data: profile
    });
  } catch (err) {
    next(err);
  }
}

import { memoryStore } from '../../config/db.js';
import { hashPassword, comparePassword, generateToken } from '../../config/security.js';

export async function loginUser(email, password) {
  if (!email || !password) {
    const error = new Error('Correo y contraseña son obligatorios.');
    error.status = 400;
    throw error;
  }

  const user = memoryStore.usuarios.find(u => u.email.toLowerCase() === email.toLowerCase());
  if (!user) {
    const error = new Error('Credenciales incorrectas. Verifica tu correo y contraseña.');
    error.status = 401;
    throw error;
  }

  if (!user.activo) {
    const error = new Error('Tu cuenta se encuentra desactivada. Contacta al soporte.');
    error.status = 403;
    throw error;
  }

  const isValidPassword = await comparePassword(password, user.password);
  if (!isValidPassword) {
    const error = new Error('Credenciales incorrectas. Verifica tu correo y contraseña.');
    error.status = 401;
    throw error;
  }

  // Obtener roles del usuario
  const userRoles = memoryStore.usuario_rol
    .filter(ur => ur.usuario_id === user.id)
    .map(ur => {
      const rol = memoryStore.roles.find(r => r.id === ur.rol_id);
      return rol ? rol.nombre : null;
    })
    .filter(Boolean);

  // Perfil asociado según rol
  const productor = memoryStore.productores.find(p => p.usuario_id === user.id);
  const establecimiento = memoryStore.establecimientos.find(e => e.usuario_id === user.id);

  const tokenPayload = {
    id: user.id,
    email: user.email,
    nombre: user.nombre_completo,
    roles: userRoles,
    productorId: productor ? productor.id : null,
    establecimientoId: establecimiento ? establecimiento.id : null
  };

  const token = generateToken(tokenPayload);

  return {
    token,
    user: {
      id: user.id,
      email: user.email,
      nombre: user.nombre_completo,
      nombre_completo: user.nombre_completo,
      telefono: user.telefono,
      rol: userRoles[0] || 'ROLE_CONSUMIDOR',
      roles: userRoles,
      productor: productor || null,
      establecimiento: establecimiento || null
    }
  };
}

export async function registerUser(data) {
  const email = data.email;
  const password = data.password;
  const nombre_completo = data.nombre_completo || data.nombre;
  const telefono = data.telefono;
  const rol = data.rol || 'ROLE_CONSUMIDOR';
  const nombre_comercial = data.nombre_comercial || data.nombre_productor;
  const ubicacion = data.ubicacion || data.region || data.direccion;
  const registro_sanitario = data.registro_sanitario;

  if (!email || !password || !nombre_completo) {
    const error = new Error('Nombre completo, correo y contraseña son obligatorios.');
    error.status = 400;
    throw error;
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    const error = new Error('El formato del correo electrónico no es válido.');
    error.status = 400;
    throw error;
  }

  if (password.length < 8) {
    const error = new Error('La contraseña debe tener al menos 8 caracteres.');
    error.status = 400;
    throw error;
  }

  const existing = memoryStore.usuarios.find(u => u.email.toLowerCase() === email.toLowerCase());
  if (existing) {
    const error = new Error('El correo electrónico ya se encuentra registrado.');
    error.status = 409;
    throw error;
  }

  const hashedPassword = await hashPassword(password);
  const newUserId = memoryStore.usuarios.length ? Math.max(...memoryStore.usuarios.map(u => u.id)) + 1 : 1;

  const newUser = {
    id: newUserId,
    email: email.trim().toLowerCase(),
    password: hashedPassword,
    nombre_completo: nombre_completo.trim(),
    telefono: telefono ? telefono.trim() : null,
    activo: 1,
    created_at: new Date()
  };

  memoryStore.usuarios.push(newUser);

  // Asignar rol
  const roleRecord = memoryStore.roles.find(r => r.nombre === rol) || memoryStore.roles.find(r => r.nombre === 'ROLE_CONSUMIDOR');
  memoryStore.usuario_rol.push({
    usuario_id: newUserId,
    rol_id: roleRecord.id
  });

  let createdProductor = null;
  let createdEstablecimiento = null;

  // Si es productor, crear registro correspondiente
  if (rol === 'ROLE_PRODUCTOR') {
    const newProdId = memoryStore.productores.length ? Math.max(...memoryStore.productores.map(p => p.id)) + 1 : 1;
    createdProductor = {
      id: newProdId,
      usuario_id: newUserId,
      nombre_comercial: nombre_comercial || nombre_completo,
      registro_sanitario: registro_sanitario || null,
      descripcion_historia: 'Elaborador artesanal de bebidas de alta calidad.',
      ubicacion_origen: ubicacion || 'Colombia',
      created_at: new Date()
    };
    memoryStore.productores.push(createdProductor);
  } else if (rol === 'ROLE_HOSTELERIA') {
    const newEstId = memoryStore.establecimientos.length ? Math.max(...memoryStore.establecimientos.map(e => e.id)) + 1 : 1;
    createdEstablecimiento = {
      id: newEstId,
      usuario_id: newUserId,
      nombre: nombre_comercial || nombre_completo,
      direccion: ubicacion || 'Dirección comercial',
      ciudad: 'Bogotá D.C.',
      activo: 1,
      created_at: new Date()
    };
    memoryStore.establecimientos.push(createdEstablecimiento);
  }

  const token = generateToken({
    id: newUser.id,
    email: newUser.email,
    nombre: newUser.nombre_completo,
    roles: [roleRecord.nombre],
    productorId: createdProductor ? createdProductor.id : null,
    establecimientoId: createdEstablecimiento ? createdEstablecimiento.id : null
  });

  return {
    token,
    user: {
      id: newUser.id,
      email: newUser.email,
      nombre: newUser.nombre_completo,
      nombre_completo: newUser.nombre_completo,
      telefono: newUser.telefono,
      rol: roleRecord.nombre,
      roles: [roleRecord.nombre],
      productor: createdProductor,
      establecimiento: createdEstablecimiento
    }
  };
}

export async function getUserProfile(userId) {
  const user = memoryStore.usuarios.find(u => u.id === userId);
  if (!user) {
    const error = new Error('Usuario no encontrado.');
    error.status = 404;
    throw error;
  }

  const roles = memoryStore.usuario_rol
    .filter(ur => ur.usuario_id === user.id)
    .map(ur => {
      const rol = memoryStore.roles.find(r => r.id === ur.rol_id);
      return rol ? rol.nombre : null;
    })
    .filter(Boolean);

  const productor = memoryStore.productores.find(p => p.usuario_id === user.id) || null;
  const establecimiento = memoryStore.establecimientos.find(e => e.usuario_id === user.id) || null;

  return {
    id: user.id,
    email: user.email,
    nombre_completo: user.nombre_completo,
    telefono: user.telefono,
    roles,
    productor,
    establecimiento
  };
}

export type Rol = 'ADMIN' | 'CAJERO';
export type Permiso = 'VENDER' | 'DEVOLUCIONES' | 'PRODUCTOS' | 'CATEGORIAS' | 'AJUSTAR_STOCK' | 'REPORTES' | 'USUARIOS' | 'CONFIGURACION';
export type Unidad = 'UNIDAD' | 'GRANEL';
export type MetodoPago = 'EFECTIVO' | 'TARJETA' | 'TRANSFERENCIA';

export const PERMISOS: { id: Permiso; label: string; desc: string }[] = [
  { id: 'VENDER', label: 'Vender', desc: 'Abrir caja y registrar ventas' },
  { id: 'DEVOLUCIONES', label: 'Devoluciones', desc: 'Devolver productos de una venta' },
  { id: 'PRODUCTOS', label: 'Productos', desc: 'Crear, editar, importar y eliminar productos' },
  { id: 'CATEGORIAS', label: 'Categorías', desc: 'Administrar categorías' },
  { id: 'AJUSTAR_STOCK', label: 'Ajustar stock', desc: 'Entradas, salidas y conteos de inventario' },
  { id: 'REPORTES', label: 'Reportes', desc: 'Ver reportes, todas las ventas y cierres de caja' },
  { id: 'USUARIOS', label: 'Usuarios', desc: 'Administrar usuarios y permisos' },
  { id: 'CONFIGURACION', label: 'Configuración', desc: 'Datos del negocio y del ticket' },
];

export interface Usuario {
  id: number; nombre: string; username: string; rol: Rol; activo: boolean;
  permisos: Permiso[]; creadoEn: string;
}

export interface Page<T> { content: T[]; page: number; size: number; totalElements: number; totalPages: number; }

export interface Categoria { id: number; nombre: string; productos: number; }

export interface Producto {
  id: number; codigo: string; descripcion: string; categoriaId: number | null; categoria: string | null;
  precioCompra: number; precioVenta: number; existencias: number; stockMinimo: number; unidad: Unidad;
  ubicacion: string | null; controlaInventario: boolean; activo: boolean; bajoStock: boolean; actualizadoEn: string;
}

export interface ProductoRequest {
  codigo: string; descripcion: string; categoriaId: number | null; precioCompra: number; precioVenta: number;
  existencias: number; stockMinimo: number; unidad: Unidad; ubicacion: string | null;
  controlaInventario: boolean; activo: boolean;
}

export interface Movimiento { id: number; tipo: string; cantidad: number; saldo: number; motivo: string; fechaHora: string; }

export interface Sesion {
  id: number; usuarioId: number; usuario: string; apertura: string; montoInicial: number; cierre: string | null;
  cantidadVentas: number; totalVentas: number; totalDevoluciones: number; montoEsperado: number;
  montoDeclarado: number | null; diferencia: number | null; observacion: string | null; estado: 'ABIERTA' | 'CERRADA';
}

export interface VentaItem {
  id: number; productoId: number | null; codigo: string; descripcion: string; cantidad: number;
  cantidadDevuelta: number; precioUnitario: number; importe: number;
}

export interface Devolucion {
  id: number; ventaId: number; itemId: number; codigo: string; descripcion: string; cantidad: number;
  importe: number; motivo: string; usuario: string; fechaHora: string;
}

export interface Venta {
  id: number; fechaHora: string; cajeroId: number; cajero: string; metodoPago: MetodoPago; total: number;
  pagado: number; cambio: number; totalDevuelto: number; items: VentaItem[]; devoluciones: Devolucion[];
}

export interface VentaResumen { id: number; fechaHora: string; cajero: string; metodoPago: MetodoPago; total: number; }

export interface Grupo { nombre: string; cantidad: number; total: number; }

export interface Resumen {
  desde: string; hasta: string; cantidadVentas: number; totalBruto: number; totalDevoluciones: number;
  totalNeto: number; ticketPromedio: number; costo: number; ganancia: number;
  porDia: { fecha: string; cantidad: number; total: number }[];
  topProductos: { codigo: string; descripcion: string; cantidad: number; total: number }[];
  porCajero: Grupo[]; porCategoria: Grupo[]; porMetodo: Grupo[];
}

export interface Inventario {
  productos: number; activos: number; bajoStock: number; sinStock: number; unidades: number;
  valorCosto: number; valorVenta: number; porCategoria: Grupo[];
}

export interface Negocio {
  nombre: string; direccion: string | null; telefono: string | null; ruc: string | null; moneda: string;
  decimales: number; lineaExtra: string | null; mensajeGracias: string | null; mensajeFinal: string | null;
}

export const METODOS: { id: MetodoPago; label: string }[] = [
  { id: 'EFECTIVO', label: 'Efectivo' },
  { id: 'TARJETA', label: 'Tarjeta' },
  { id: 'TRANSFERENCIA', label: 'Transferencia' },
];

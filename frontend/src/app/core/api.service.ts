import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  Categoria, Devolucion, Inventario, MetodoPago, Movimiento, Negocio, Page, Producto, ProductoRequest,
  Resumen, Sesion, Usuario, Venta, VentaResumen,
} from './models';

type Params = Record<string, string | number | boolean | null | undefined>;

function toParams(p: Params = {}): HttpParams {
  let hp = new HttpParams();
  for (const [k, v] of Object.entries(p)) {
    if (v !== null && v !== undefined && v !== '') hp = hp.set(k, String(v));
  }
  return hp;
}

/** Acceso tipado a la API REST. Todos los métodos devuelven Promesas. */
@Injectable({ providedIn: 'root' })
export class Api {
  private http = inject(HttpClient);
  private base = '/api';

  private get<T>(url: string, params?: Params) { return firstValueFrom(this.http.get<T>(this.base + url, { params: toParams(params) })); }
  private post<T>(url: string, body: unknown) { return firstValueFrom(this.http.post<T>(this.base + url, body)); }
  private put<T>(url: string, body: unknown) { return firstValueFrom(this.http.put<T>(this.base + url, body)); }
  private del(url: string) { return firstValueFrom(this.http.delete<void>(this.base + url)); }

  // auth
  login(username: string, password: string) { return this.post<{ token: string; usuario: Usuario }>('/auth/login', { username, password }); }
  me() { return this.get<Usuario>('/auth/me'); }
  cambiarPassword(actual: string, nueva: string) { return this.post<void>('/auth/cambiar-password', { actual, nueva }); }

  // usuarios
  usuarios() { return this.get<Usuario[]>('/usuarios'); }
  crearUsuario(u: unknown) { return this.post<Usuario>('/usuarios', u); }
  actualizarUsuario(id: number, u: unknown) { return this.put<Usuario>(`/usuarios/${id}`, u); }
  eliminarUsuario(id: number) { return this.del(`/usuarios/${id}`); }
  resetPassword(id: number, nueva: string) { return this.post<void>(`/usuarios/${id}/reset-password`, { nueva }); }

  // categorías
  categorias() { return this.get<Categoria[]>('/categorias'); }
  crearCategoria(nombre: string) { return this.post<Categoria>('/categorias', { nombre }); }
  actualizarCategoria(id: number, nombre: string) { return this.put<Categoria>(`/categorias/${id}`, { nombre }); }
  eliminarCategoria(id: number) { return this.del(`/categorias/${id}`); }

  // productos
  productos(p: Params) { return this.get<Page<Producto>>('/productos', p); }
  productoPorCodigo(codigo: string) { return this.get<Producto>(`/productos/codigo/${encodeURIComponent(codigo)}`); }
  crearProducto(p: ProductoRequest) { return this.post<Producto>('/productos', p); }
  actualizarProducto(id: number, p: ProductoRequest) { return this.put<Producto>(`/productos/${id}`, p); }
  eliminarProducto(id: number) { return this.del(`/productos/${id}`); }
  ajustarStock(id: number, tipo: 'ENTRADA' | 'SALIDA' | 'FIJAR', cantidad: number, motivo: string) {
    return this.post<Producto>(`/productos/${id}/ajustes`, { tipo, cantidad, motivo });
  }
  movimientos(id: number) { return this.get<Movimiento[]>(`/productos/${id}/movimientos`); }
  exportarProductos() { return firstValueFrom(this.http.get(this.base + '/productos/exportar', { responseType: 'blob' })); }
  importarProductos(file: File) {
    const fd = new FormData();
    fd.append('archivo', file);
    return this.post<{ creados: number; actualizados: number; errores: string[] }>('/productos/importar', fd);
  }

  // caja
  cajaActual() { return this.get<Sesion | null>('/caja/actual'); }
  abrirCaja(montoInicial: number) { return this.post<Sesion>('/caja/abrir', { montoInicial }); }
  cerrarCaja(montoDeclarado: number, observacion: string) { return this.post<Sesion>('/caja/cerrar', { montoDeclarado, observacion }); }
  sesiones(page = 0) { return this.get<Page<Sesion>>('/caja/sesiones', { page }); }

  // ventas
  vender(items: { productoId: number; cantidad: number }[], metodoPago: MetodoPago, pagado: number) {
    return this.post<Venta>('/ventas', { items, metodoPago, pagado });
  }
  ventas(p: Params) { return this.get<Page<VentaResumen>>('/ventas', p); }
  venta(id: number) { return this.get<Venta>(`/ventas/${id}`); }
  devolver(ventaId: number, itemId: number, cantidad: number, motivo: string) {
    return this.post<Venta>(`/ventas/${ventaId}/devoluciones`, { itemId, cantidad, motivo });
  }
  devoluciones(desde: string, hasta: string) { return this.get<Devolucion[]>('/devoluciones', { desde, hasta }); }

  // reportes
  resumen(desde: string, hasta: string) { return this.get<Resumen>('/reportes/resumen', { desde, hasta }); }
  inventario() { return this.get<Inventario>('/reportes/inventario'); }

  // negocio
  negocio() { return this.get<Negocio>('/negocio'); }
  guardarNegocio(n: Negocio) { return this.put<Negocio>('/negocio', n); }
}

/** Extrae un mensaje legible de un error HTTP. */
export function errorMsg(e: unknown): string {
  const err = e as { error?: { message?: string; fields?: Record<string, string> }; status?: number; message?: string };
  if (err?.error?.fields) {
    return Object.entries(err.error.fields).map(([k, v]) => `${k}: ${v}`).join(' · ');
  }
  if (err?.error?.message) return err.error.message;
  if (err?.status === 0) return 'No se pudo conectar con el servidor';
  return err?.message ?? 'Error inesperado';
}

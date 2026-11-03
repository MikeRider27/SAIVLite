import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Api } from './api.service';
import { Permiso, Usuario } from './models';

const TOKEN_KEY = 'saiv.token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = inject(Api);
  private router = inject(Router);

  readonly usuario = signal<Usuario | null>(null);
  readonly logueado = computed(() => this.usuario() !== null);

  get token(): string | null {
    try { return localStorage.getItem(TOKEN_KEY); } catch { return null; }
  }

  /** Restaura la sesión al iniciar la app (si hay token válido). */
  async restaurar(): Promise<void> {
    if (!this.token) return;
    try {
      this.usuario.set(await this.api.me());
    } catch {
      this.limpiar();
    }
  }

  async login(username: string, password: string): Promise<void> {
    const r = await this.api.login(username, password);
    try { localStorage.setItem(TOKEN_KEY, r.token); } catch { /* modo privado */ }
    this.usuario.set(r.usuario);
  }

  logout(): void {
    this.limpiar();
    this.router.navigate(['/login']);
  }

  limpiar(): void {
    try { localStorage.removeItem(TOKEN_KEY); } catch { /* */ }
    this.usuario.set(null);
  }

  puede(p: Permiso): boolean {
    return this.usuario()?.permisos.includes(p) ?? false;
  }

  /** Ruta de inicio según permisos. */
  inicio(): string {
    if (this.puede('VENDER')) return '/vender';
    if (this.puede('REPORTES')) return '/reportes';
    return '/productos';
  }
}

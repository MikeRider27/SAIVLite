import { Injectable, inject, signal } from '@angular/core';
import { Api } from './api.service';
import { Negocio } from './models';

const DEFAULT: Negocio = {
  nombre: 'Mi Negocio', direccion: null, telefono: null, ruc: null, moneda: 'Gs.', decimales: 0,
  lineaExtra: null, mensajeGracias: null, mensajeFinal: null,
};

/** Datos del negocio + formateadores de moneda/fecha compartidos. */
@Injectable({ providedIn: 'root' })
export class NegocioService {
  private api = inject(Api);
  readonly datos = signal<Negocio>(DEFAULT);

  async cargar(): Promise<void> {
    try { this.datos.set(await this.api.negocio()); } catch { /* usa valores por defecto */ }
  }

  dinero(v: number | null | undefined, conSimbolo = true): string {
    const n = this.datos();
    const s = new Intl.NumberFormat('es-PY', {
      minimumFractionDigits: n.decimales, maximumFractionDigits: n.decimales,
    }).format(Number(v ?? 0));
    return conSimbolo ? `${n.moneda} ${s}` : s;
  }
}

export function cantidad(v: number | null | undefined): string {
  return new Intl.NumberFormat('es-PY', { maximumFractionDigits: 3 }).format(Number(v ?? 0));
}

export function fechaHora(iso: string | null | undefined): string {
  if (!iso) return '—';
  const d = new Date(iso);
  return d.toLocaleString('es-PY', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
}

export function fecha(iso: string | null | undefined): string {
  if (!iso) return '—';
  const d = iso.length === 10 ? new Date(iso + 'T00:00:00') : new Date(iso);
  return d.toLocaleDateString('es-PY', { day: '2-digit', month: '2-digit', year: 'numeric' });
}

/** Fecha local en formato yyyy-MM-dd. */
export function isoLocal(d = new Date()): string {
  const p = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())}`;
}

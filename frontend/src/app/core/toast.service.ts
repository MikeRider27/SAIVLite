import { Injectable, signal } from '@angular/core';

export interface Toast { id: number; tipo: 'ok' | 'error' | 'info'; texto: string; }

@Injectable({ providedIn: 'root' })
export class ToastService {
  readonly toasts = signal<Toast[]>([]);
  private seq = 0;

  ok(texto: string) { this.push('ok', texto); }
  error(texto: string) { this.push('error', texto, 6000); }
  info(texto: string) { this.push('info', texto); }

  cerrar(id: number) { this.toasts.update(t => t.filter(x => x.id !== id)); }

  private push(tipo: Toast['tipo'], texto: string, ms = 3500) {
    const id = ++this.seq;
    this.toasts.update(t => [...t, { id, tipo, texto }]);
    setTimeout(() => this.cerrar(id), ms);
  }
}

export interface ConfirmReq { titulo: string; texto: string; ok: string; peligro: boolean; resolve: (v: boolean) => void; }

@Injectable({ providedIn: 'root' })
export class ConfirmService {
  readonly actual = signal<ConfirmReq | null>(null);

  preguntar(titulo: string, texto: string, opts: { ok?: string; peligro?: boolean } = {}): Promise<boolean> {
    return new Promise(resolve => {
      this.actual.set({ titulo, texto, ok: opts.ok ?? 'Confirmar', peligro: opts.peligro ?? false, resolve });
    });
  }

  responder(v: boolean) {
    this.actual()?.resolve(v);
    this.actual.set(null);
  }
}

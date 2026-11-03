import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Api, errorMsg } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { DineroPipe, FechaHoraPipe } from '../core/format.pipes';
import { Page, Sesion } from '../core/models';
import { ConfirmService, ToastService } from '../core/toast.service';
import { IconComponent } from '../shared/icon.component';

@Component({
  selector: 'app-caja',
  standalone: true,
  imports: [FormsModule, RouterLink, IconComponent, DineroPipe, FechaHoraPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page">
      <div class="page-head">
        <div><div class="eyebrow">Turno</div><h1>Caja</h1></div>
      </div>

      @if (auth.puede('VENDER')) {
        @if (sesion() === undefined) {
          <div class="card empty"><span class="spinner"></span></div>
        } @else if (sesion() === null) {
          <div class="card card-pad closed">
            <app-icon name="cash" />
            <div class="grow"><h2>No tenés una caja abierta</h2><p class="muted">Abrí la caja desde la pantalla de venta para comenzar el turno.</p></div>
            <a class="btn btn-accent" routerLink="/vender">Ir a vender</a>
          </div>
        } @else {
          @let s = sesion()!;
          <div class="grid grid-4">
            <div class="card stat"><span class="label">Monto inicial</span><span class="value">{{ s.montoInicial | dinero }}</span><span class="hint">Apertura {{ s.apertura | fh }}</span></div>
            <div class="card stat"><span class="label">Ventas</span><span class="value">{{ s.totalVentas | dinero }}</span><span class="hint">{{ s.cantidadVentas }} tickets</span></div>
            <div class="card stat"><span class="label">Devoluciones</span><span class="value">{{ s.totalDevoluciones | dinero }}</span><span class="hint">Reintegrado al cliente</span></div>
            <div class="card stat accent"><span class="label">Esperado en caja</span><span class="value">{{ s.montoEsperado | dinero }}</span><span class="hint">Inicial + ventas − devoluciones</span></div>
          </div>

          <form class="card close-form" (ngSubmit)="cerrar(s)">
            <div class="card-head"><h2>Cerrar caja</h2><span class="badge ok"><span class="dot"></span>Abierta</span></div>
            <div class="card-body grid grid-3">
              <div class="field">
                <label for="dec">Dinero contado</label>
                <input id="dec" class="input input-lg num mono" type="number" min="0" step="any" name="dec"
                       [ngModel]="declarado()" (ngModelChange)="declarado.set($event === '' || $event === null ? null : +$event)">
                <span class="help">Contá el efectivo físico en la caja.</span>
              </div>
              <div class="field">
                <label>Diferencia</label>
                @if (diferencia() !== null) {
                  <div class="diff" [class.ok]="diferencia() === 0" [class.pos]="diferencia()! > 0" [class.neg]="diferencia()! < 0">
                    <span class="mono">{{ diferencia()! > 0 ? '+' : '' }}{{ diferencia() | dinero }}</span>
                    <span class="lbl">{{ diferencia() === 0 ? 'Cuadra' : diferencia()! > 0 ? 'Sobrante' : 'Faltante' }}</span>
                  </div>
                } @else { <div class="diff"><span class="muted">—</span></div> }
              </div>
              <div class="field">
                <label for="obs">Observación</label>
                <textarea id="obs" class="input" name="obs" [(ngModel)]="observacion" maxlength="300" rows="2"></textarea>
              </div>
            </div>
            <div footer class="foot">
              <button class="btn btn-primary" [disabled]="declarado() === null || ocupado()"><app-icon name="lock" /> Cerrar caja</button>
            </div>
          </form>
        }
      }

      @if (auth.puede('REPORTES')) {
        <div class="card hist">
          <div class="card-head"><h2>Historial de cierres</h2></div>
          @if (historial(); as h) {
            <div class="table-wrap">
              <table class="table">
                <thead><tr><th>#</th><th>Cajero</th><th>Apertura</th><th>Cierre</th><th class="right">Inicial</th><th class="right">Ventas</th><th class="right">Esperado</th><th class="right">Contado</th><th class="right">Diferencia</th></tr></thead>
                <tbody>
                  @for (x of h.content; track x.id) {
                    <tr>
                      <td class="mono">{{ x.id }}</td>
                      <td>{{ x.usuario }}</td>
                      <td class="nowrap">{{ x.apertura | fh }}</td>
                      <td class="nowrap">@if (x.cierre) { {{ x.cierre | fh }} } @else { <span class="badge ok"><span class="dot"></span>Abierta</span> }</td>
                      <td class="right mono">{{ x.montoInicial | dinero:false }}</td>
                      <td class="right mono">{{ x.totalVentas | dinero:false }}</td>
                      <td class="right mono">{{ x.montoEsperado | dinero:false }}</td>
                      <td class="right mono">{{ x.montoDeclarado === null ? '—' : (x.montoDeclarado | dinero:false) }}</td>
                      <td class="right">
                        @if (x.diferencia !== null) {
                          <span class="badge mono" [class.ok]="x.diferencia === 0" [class.warn]="x.diferencia > 0" [class.danger]="x.diferencia < 0">
                            {{ x.diferencia > 0 ? '+' : '' }}{{ x.diferencia | dinero:false }}</span>
                        }
                      </td>
                    </tr>
                  } @empty {
                    <tr><td colspan="9" class="empty">Sin registros</td></tr>
                  }
                </tbody>
              </table>
            </div>
            <div class="pager">
              <span class="grow">Página {{ h.page + 1 }} de {{ h.totalPages || 1 }}</span>
              <button class="btn btn-sm btn-icon" [disabled]="h.page === 0" (click)="cargarHistorial(h.page - 1)"><app-icon name="left" /></button>
              <button class="btn btn-sm btn-icon" [disabled]="h.page + 1 >= h.totalPages" (click)="cargarHistorial(h.page + 1)"><app-icon name="right" /></button>
            </div>
          }
        </div>
      }
    </div>
  `,
  styles: [`
    .closed { display: flex; align-items: center; gap: 16px; flex-wrap: wrap; }
    .closed > app-icon { width: 40px; height: 40px; color: var(--muted); }
    .close-form { margin-top: 16px; }
    .foot { display: flex; justify-content: flex-end; padding: 14px 20px; border-top: 1px solid var(--line); background: var(--surface-2); border-radius: 0 0 var(--radius) var(--radius); }
    .diff { height: 52px; border-radius: var(--radius-sm); background: var(--surface-3); display: flex; align-items: center; justify-content: space-between; padding: 0 14px; font-weight: 700; }
    .diff .mono { font-size: 1.2rem; }
    .diff .lbl { font-size: .78rem; text-transform: uppercase; letter-spacing: .08em; }
    .diff.ok { background: var(--ok-soft); color: var(--ok); }
    .diff.pos { background: var(--warn-soft); color: var(--warn); }
    .diff.neg { background: var(--danger-soft); color: var(--danger); }
    .hist { margin-top: 20px; }
  `],
})
export class CajaComponent {
  private api = inject(Api);
  private toast = inject(ToastService);
  private confirm = inject(ConfirmService);
  auth = inject(AuthService);

  sesion = signal<Sesion | null | undefined>(undefined);
  declarado = signal<number | null>(null);
  observacion = '';
  ocupado = signal(false);
  historial = signal<Page<Sesion> | null>(null);

  diferencia = computed(() => {
    const s = this.sesion(), d = this.declarado();
    return s && d !== null ? Math.round((d - s.montoEsperado) * 100) / 100 : null;
  });

  constructor() {
    if (this.auth.puede('VENDER')) this.cargar();
    if (this.auth.puede('REPORTES')) this.cargarHistorial(0);
  }

  async cargar() {
    try { this.sesion.set(await this.api.cajaActual()); } catch (e) { this.toast.error(errorMsg(e)); }
  }

  async cargarHistorial(page: number) {
    try { this.historial.set(await this.api.sesiones(page)); } catch (e) { this.toast.error(errorMsg(e)); }
  }

  async cerrar(s: Sesion) {
    const dif = this.diferencia();
    if (dif === null) return;
    const ok = await this.confirm.preguntar('Cerrar caja',
      dif === 0 ? 'El arqueo cuadra. ¿Cerrar el turno?' : `Hay una diferencia de ${dif}. ¿Cerrar el turno de todas formas?`,
      { ok: 'Cerrar caja' });
    if (!ok) return;
    this.ocupado.set(true);
    try {
      await this.api.cerrarCaja(this.declarado()!, this.observacion);
      this.toast.ok(`Caja #${s.id} cerrada`);
      this.declarado.set(null);
      this.observacion = '';
      this.sesion.set(null);
      if (this.auth.puede('REPORTES')) this.cargarHistorial(0);
    } catch (e) { this.toast.error(errorMsg(e)); } finally { this.ocupado.set(false); }
  }
}

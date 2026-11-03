import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Api, errorMsg } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { CantidadPipe, DineroPipe, FechaHoraPipe } from '../core/format.pipes';
import { Devolucion, METODOS, Page, Venta, VentaItem, VentaResumen } from '../core/models';
import { NegocioService, isoLocal } from '../core/negocio.service';
import { ToastService } from '../core/toast.service';
import { IconComponent } from '../shared/icon.component';
import { ModalComponent } from '../shared/modal.component';
import { imprimirTicket } from '../shared/ticket';

@Component({
  selector: 'app-ventas',
  standalone: true,
  imports: [FormsModule, IconComponent, ModalComponent, DineroPipe, CantidadPipe, FechaHoraPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page">
      <div class="page-head">
        <div>
          <div class="eyebrow">Historial</div>
          <h1>Ventas</h1>
        </div>
        <div class="actions">
          @if (puedeVerDevoluciones) {
            <div class="segmented">
              <button [class.on]="vista() === 'ventas'" (click)="vista.set('ventas')">Ventas</button>
              <button [class.on]="vista() === 'devoluciones'" (click)="vista.set('devoluciones'); cargarDevoluciones()">Devoluciones</button>
            </div>
          }
        </div>
      </div>

      <div class="card filters">
        <div class="field"><label>Desde</label><input class="input" type="date" [(ngModel)]="desde" (change)="buscar(0)"></div>
        <div class="field"><label>Hasta</label><input class="input" type="date" [(ngModel)]="hasta" (change)="buscar(0)"></div>
        <div class="field"><label>Ir a venta N°</label>
          <form class="row" (ngSubmit)="irA()"><input class="input mono" type="number" min="1" name="n" [(ngModel)]="numero" placeholder="#"><button class="btn">Ver</button></form>
        </div>
        <div class="spacer"></div>
        @if (vista() === 'ventas' && pagina(); as pg) {
          <div class="sum"><span class="muted">{{ pg.totalElements }} ventas en el período</span></div>
        }
      </div>

      @if (!auth.puede('REPORTES')) {
        <p class="muted note">Se muestran solo tus ventas.</p>
      }

      @if (vista() === 'ventas') {
        <div class="card">
          @if (pagina(); as pg) {
            @if (pg.content.length === 0) {
              <div class="empty"><app-icon name="receipt" /><p>No hay ventas en este período.</p></div>
            } @else {
              <div class="table-wrap">
                <table class="table">
                  <thead><tr><th>N°</th><th>Fecha y hora</th><th>Cajero</th><th>Método</th><th class="right">Total</th></tr></thead>
                  <tbody>
                    @for (v of pg.content; track v.id) {
                      <tr class="clickable" (click)="abrir(v.id)">
                        <td class="mono strong">#{{ v.id }}</td>
                        <td class="nowrap">{{ v.fechaHora | fh }}</td>
                        <td>{{ v.cajero }}</td>
                        <td><span class="badge">{{ metodo(v.metodoPago) }}</span></td>
                        <td class="right mono strong">{{ v.total | dinero }}</td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
              <div class="pager">
                <span class="grow">Página {{ pg.page + 1 }} de {{ pg.totalPages || 1 }}</span>
                <button class="btn btn-sm btn-icon" [disabled]="pg.page === 0" (click)="buscar(pg.page - 1)" aria-label="Anterior"><app-icon name="left" /></button>
                <button class="btn btn-sm btn-icon" [disabled]="pg.page + 1 >= pg.totalPages" (click)="buscar(pg.page + 1)" aria-label="Siguiente"><app-icon name="right" /></button>
              </div>
            }
          } @else { <div class="empty"><span class="spinner"></span></div> }
        </div>
      } @else {
        <div class="card">
          @if (devoluciones().length === 0) {
            <div class="empty"><app-icon name="undo" /><p>No hay devoluciones en este período.</p></div>
          } @else {
            <div class="table-wrap">
              <table class="table">
                <thead><tr><th>Fecha</th><th>Venta</th><th>Producto</th><th class="right">Cant.</th><th class="right">Importe</th><th>Motivo</th><th>Usuario</th></tr></thead>
                <tbody>
                  @for (d of devoluciones(); track d.id) {
                    <tr class="clickable" (click)="abrir(d.ventaId)">
                      <td class="nowrap">{{ d.fechaHora | fh }}</td>
                      <td class="mono">#{{ d.ventaId }}</td>
                      <td>{{ d.descripcion }}</td>
                      <td class="right mono">{{ d.cantidad | cant }}</td>
                      <td class="right mono">{{ d.importe | dinero }}</td>
                      <td class="ink-2">{{ d.motivo }}</td>
                      <td>{{ d.usuario }}</td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
            <div class="pager"><span class="grow">{{ devoluciones().length }} devoluciones</span><span class="mono strong">{{ totalDevuelto() | dinero }}</span></div>
          }
        </div>
      }
    </div>

    @if (detalle(); as v) {
      <app-modal [titulo]="'Venta #' + v.id" [subtitulo]="(v.fechaHora | fh) + ' · ' + v.cajero + ' · ' + metodo(v.metodoPago)" [ancho]="720" (cerrar)="detalle.set(null)">
        <div class="table-wrap">
          <table class="table">
            <thead><tr><th>Producto</th><th class="right">Cant.</th><th class="right">Precio</th><th class="right">Importe</th><th></th></tr></thead>
            <tbody>
              @for (i of v.items; track i.id) {
                <tr>
                  <td>{{ i.descripcion }}<div class="muted mono small">{{ i.codigo }}
                    @if (i.cantidadDevuelta > 0) { <span class="badge warn">devuelto {{ i.cantidadDevuelta | cant }}</span> }</div></td>
                  <td class="right mono">{{ i.cantidad | cant }}</td>
                  <td class="right mono">{{ i.precioUnitario | dinero:false }}</td>
                  <td class="right mono">{{ i.importe | dinero:false }}</td>
                  <td class="actions">
                    @if (auth.puede('DEVOLUCIONES') && i.cantidad - i.cantidadDevuelta > 0) {
                      <button class="btn btn-sm" (click)="iniciarDevolucion(i)"><app-icon name="undo" /> Devolver</button>
                    }
                  </td>
                </tr>
              }
            </tbody>
          </table>
        </div>
        <div class="totals">
          <div><span class="muted">Total</span><span class="mono strong">{{ v.total | dinero }}</span></div>
          <div><span class="muted">Pagado</span><span class="mono">{{ v.pagado | dinero }}</span></div>
          <div><span class="muted">Vuelto</span><span class="mono">{{ v.cambio | dinero }}</span></div>
          @if (v.totalDevuelto > 0) { <div class="dev"><span>Devuelto</span><span class="mono">-{{ v.totalDevuelto | dinero }}</span></div> }
        </div>

        @if (devolviendo(); as it) {
          <form class="dev-form card" (ngSubmit)="confirmarDevolucion(v, it)">
            <h3>Devolver: {{ it.descripcion }}</h3>
            <div class="grid grid-2">
              <div class="field">
                <label>Cantidad (máx. {{ it.cantidad - it.cantidadDevuelta | cant }})</label>
                <input class="input num mono" type="number" min="0" step="any" name="dc" [(ngModel)]="devCantidad" required>
              </div>
              <div class="field">
                <label>Reintegro</label>
                <div class="input mono reint">{{ it.precioUnitario * (devCantidad || 0) | dinero }}</div>
              </div>
            </div>
            <div class="field">
              <label>Motivo</label>
              <input class="input" name="dm" [(ngModel)]="devMotivo" placeholder="Ej.: producto vencido, cambio de opinión…" required maxlength="300">
            </div>
            <div class="row">
              <span class="spacer"></span>
              <button type="button" class="btn" (click)="devolviendo.set(null)">Cancelar</button>
              <button class="btn btn-primary" [disabled]="ocupado() || !devMotivo.trim() || !(devCantidad > 0)">Registrar devolución</button>
            </div>
          </form>
        }

        @if (v.devoluciones.length) {
          <h3 class="sec">Devoluciones</h3>
          <ul class="devs">
            @for (d of v.devoluciones; track d.id) {
              <li><span class="mono">{{ d.fechaHora | fh }}</span> · {{ d.cantidad | cant }} × {{ d.descripcion }} · <span class="mono">{{ d.importe | dinero }}</span>
                <div class="muted">{{ d.motivo }} — {{ d.usuario }}</div></li>
            }
          </ul>
        }
        <div footer>
          <button class="btn" (click)="detalle.set(null)">Cerrar</button>
          <button class="btn btn-primary" (click)="imprimir(v)"><app-icon name="printer" /> Imprimir ticket</button>
        </div>
      </app-modal>
    }
  `,
  styles: [`
    .filters { display: flex; gap: 14px; padding: 14px 16px; margin-bottom: 14px; align-items: flex-end; flex-wrap: wrap; }
    .filters .field { width: 170px; }
    .filters form .input { width: 100px; }
    .note { margin: -4px 0 12px; font-size: .85rem; }
    .strong { font-weight: 700; }
    .small { font-size: .76rem; display: flex; gap: 8px; align-items: center; }
    .totals { display: flex; flex-direction: column; gap: 4px; align-items: flex-end; margin-top: 14px; }
    .totals > div { display: flex; gap: 20px; min-width: 240px; justify-content: space-between; }
    .totals .dev { color: var(--warn); font-weight: 700; }
    .dev-form { padding: 16px; margin-top: 16px; display: flex; flex-direction: column; gap: 12px; background: var(--surface-2); }
    .reint { display: flex; align-items: center; justify-content: flex-end; background: var(--surface-3); }
    .sec { margin: 18px 0 8px; }
    .devs { margin: 0; padding-left: 18px; display: flex; flex-direction: column; gap: 6px; font-size: .88rem; }
  `],
})
export class VentasComponent {
  private api = inject(Api);
  private toast = inject(ToastService);
  private negocio = inject(NegocioService);
  auth = inject(AuthService);

  puedeVerDevoluciones = this.auth.puede('DEVOLUCIONES') || this.auth.puede('REPORTES');
  vista = signal<'ventas' | 'devoluciones'>('ventas');
  desde = isoLocal(new Date(Date.now() - 6 * 864e5));
  hasta = isoLocal();
  numero: number | null = null;
  pagina = signal<Page<VentaResumen> | null>(null);
  devoluciones = signal<Devolucion[]>([]);
  totalDevuelto = computed(() => this.devoluciones().reduce((s, d) => s + d.importe, 0));

  detalle = signal<Venta | null>(null);
  devolviendo = signal<VentaItem | null>(null);
  devCantidad = 1;
  devMotivo = '';
  ocupado = signal(false);

  constructor() { this.buscar(0); }

  metodo(m: string) { return METODOS.find(x => x.id === m)?.label ?? m; }

  async buscar(page: number) {
    if (this.vista() === 'devoluciones') { this.cargarDevoluciones(); return; }
    try {
      this.pagina.set(await this.api.ventas({ desde: this.desde, hasta: this.hasta, page, size: 25 }));
    } catch (e) { this.toast.error(errorMsg(e)); }
  }

  async cargarDevoluciones() {
    try { this.devoluciones.set(await this.api.devoluciones(this.desde, this.hasta)); }
    catch (e) { this.toast.error(errorMsg(e)); }
  }

  irA() { if (this.numero) this.abrir(this.numero); }

  async abrir(id: number) {
    try {
      this.devolviendo.set(null);
      this.detalle.set(await this.api.venta(id));
    } catch (e) { this.toast.error(errorMsg(e)); }
  }

  iniciarDevolucion(i: VentaItem) {
    this.devCantidad = i.cantidad - i.cantidadDevuelta;
    this.devMotivo = '';
    this.devolviendo.set(i);
  }

  async confirmarDevolucion(v: Venta, i: VentaItem) {
    this.ocupado.set(true);
    try {
      const nv = await this.api.devolver(v.id, i.id, Number(this.devCantidad), this.devMotivo.trim());
      this.detalle.set(nv);
      this.devolviendo.set(null);
      this.toast.ok('Devolución registrada; stock repuesto');
      if (this.vista() === 'devoluciones') this.cargarDevoluciones();
    } catch (e) { this.toast.error(errorMsg(e)); } finally { this.ocupado.set(false); }
  }

  imprimir(v: Venta) { imprimirTicket(v, this.negocio.datos(), (x, s) => this.negocio.dinero(x, s)); }
}

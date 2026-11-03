import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Api, errorMsg } from '../core/api.service';
import { CantidadPipe, DineroPipe, FechaPipe } from '../core/format.pipes';
import { Grupo, Inventario, METODOS, Resumen } from '../core/models';
import { NegocioService, isoLocal } from '../core/negocio.service';
import { ToastService } from '../core/toast.service';
import { IconComponent } from '../shared/icon.component';

type Rango = 'hoy' | '7d' | '30d' | 'mes' | 'custom';

interface Barra { fecha: string; total: number; cantidad: number; x: number; y: number; h: number; w: number; }

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [FormsModule, RouterLink, IconComponent, DineroPipe, CantidadPipe, FechaPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page">
      <div class="page-head">
        <div><div class="eyebrow">Análisis</div><h1>Reportes</h1></div>
        <div class="actions no-print">
          <button class="btn" (click)="imprimir()"><app-icon name="printer" /> Imprimir</button>
        </div>
      </div>

      <div class="card filters no-print">
        <div class="segmented">
          <button [class.on]="rango() === 'hoy'" (click)="setRango('hoy')">Hoy</button>
          <button [class.on]="rango() === '7d'" (click)="setRango('7d')">7 días</button>
          <button [class.on]="rango() === '30d'" (click)="setRango('30d')">30 días</button>
          <button [class.on]="rango() === 'mes'" (click)="setRango('mes')">Este mes</button>
          <button [class.on]="rango() === 'custom'" (click)="rango.set('custom')">Personalizado</button>
        </div>
        @if (rango() === 'custom') {
          <input class="input date" type="date" [(ngModel)]="desde" (change)="cargar()">
          <span class="muted">a</span>
          <input class="input date" type="date" [(ngModel)]="hasta" (change)="cargar()">
        }
      </div>

      @if (r(); as r) {
        <p class="period muted">Período: {{ r.desde | f }} — {{ r.hasta | f }}</p>
        <div class="grid grid-5">
          <div class="card stat accent"><span class="label">Ventas netas</span><span class="value">{{ r.totalNeto | dinero }}</span><span class="hint">Bruto {{ r.totalBruto | dinero }}</span></div>
          <div class="card stat"><span class="label">Tickets</span><span class="value">{{ r.cantidadVentas }}</span><span class="hint">ventas registradas</span></div>
          <div class="card stat"><span class="label">Ticket promedio</span><span class="value">{{ r.ticketPromedio | dinero }}</span></div>
          <div class="card stat"><span class="label">Ganancia bruta</span><span class="value">{{ r.ganancia | dinero }}</span><span class="hint">Margen {{ margen() }}</span></div>
          <div class="card stat"><span class="label">Devoluciones</span><span class="value">{{ r.totalDevoluciones | dinero }}</span></div>
        </div>

        <div class="card chart-card">
          <div class="card-head"><h2>Ventas por día</h2><span class="muted small">Total vendido (bruto)</span></div>
          <div class="card-body">
            @if (barras().length === 0) {
              <div class="empty">Sin ventas en el período.</div>
            } @else {
              <div class="chart">
                <div class="y-labels">
                  @for (t of ticks(); track t.v) { <span [style.top.%]="t.y / H * 100">{{ compacto(t.v) }}</span> }
                </div>
                <div class="plot" (mouseleave)="hover.set(null)">
                <svg [attr.viewBox]="'0 0 ' + W + ' ' + H" preserveAspectRatio="none" role="img" aria-label="Gráfico de ventas por día">
                  @for (t of ticks(); track t.v) {
                    <line [attr.x1]="PL" [attr.x2]="W" [attr.y1]="t.y" [attr.y2]="t.y" class="grid-line" />
                  }
                  @for (b of barras(); track b.fecha; let i = $index) {
                    <rect class="hit" [attr.x]="b.x - 2" y="0" [attr.width]="b.w + 4" [attr.height]="H - PB" (mouseenter)="hover.set(i)" />
                    <path class="bar" [class.on]="hover() === i" [attr.d]="barPath(b)" />
                  }
                  <line [attr.x1]="PL" [attr.x2]="W" [attr.y1]="H - PB" [attr.y2]="H - PB" class="axis" />
                </svg>
                <div class="x-labels">
                  @for (b of barras(); track b.fecha; let i = $index) {
                    @if (mostrarEtiqueta(i)) { <span [style.left.%]="(b.x + b.w / 2) / W * 100">{{ dia(b.fecha) }}</span> }
                  }
                </div>
                @if (hover() !== null) {
                  @let b = barras()[hover()!];
                  <div class="tooltip" [style.left.%]="(b.x + b.w / 2) / W * 100" [style.top.%]="b.y / H * 100">
                    <div class="tt-date">{{ b.fecha | f }}</div>
                    <div class="mono tt-val">{{ b.total | dinero }}</div>
                    <div class="tt-sub">{{ b.cantidad }} tickets</div>
                  </div>
                }
                </div>
              </div>
            }
          </div>
        </div>

        <div class="grid grid-2 lists">
          <div class="card">
            <div class="card-head"><h2>Productos más vendidos</h2></div>
            <div class="card-body">
              @if (r.topProductos.length === 0) { <div class="muted">Sin datos.</div> }
              <div class="hbar-list">
                @for (p of r.topProductos; track p.codigo) {
                  <div class="hbar" [title]="p.descripcion + ': ' + negocio.dinero(p.total)">
                    <span class="name">{{ p.descripcion }} <span class="muted">· {{ p.cantidad | cant }} u.</span></span>
                    <span class="val">{{ p.total | dinero:false }}</span>
                    <div class="track"><div class="fill" [style.width.%]="pct(p.total, maxTop())"></div></div>
                  </div>
                }
              </div>
            </div>
          </div>

          <div class="stack">
            <div class="card">
              <div class="card-head"><h2>Por cajero</h2></div>
              <div class="card-body">
                @if (r.porCajero.length === 0) { <div class="muted">Sin datos.</div> }
                <div class="hbar-list">
                  @for (g of r.porCajero; track g.nombre) {
                    <div class="hbar"><span class="name">{{ g.nombre }} <span class="muted">· {{ g.cantidad }} tickets</span></span><span class="val">{{ g.total | dinero:false }}</span>
                      <div class="track"><div class="fill" [style.width.%]="pct(g.total, max(r.porCajero))"></div></div></div>
                  }
                </div>
              </div>
            </div>
            <div class="card">
              <div class="card-head"><h2>Por método de pago</h2></div>
              <div class="card-body">
                @if (r.porMetodo.length === 0) { <div class="muted">Sin datos.</div> }
                <div class="hbar-list">
                  @for (g of r.porMetodo; track g.nombre) {
                    <div class="hbar"><span class="name">{{ metodo(g.nombre) }} <span class="muted">· {{ g.cantidad }}</span></span><span class="val">{{ g.total | dinero:false }}</span>
                      <div class="track"><div class="fill" [style.width.%]="pct(g.total, max(r.porMetodo))"></div></div></div>
                  }
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="card cat-card">
          <div class="card-head"><h2>Por categoría</h2></div>
          <div class="table-wrap">
            <table class="table">
              <thead><tr><th>Categoría</th><th class="right">Tickets</th><th class="right">Total</th><th class="right">% del total</th></tr></thead>
              <tbody>
                @for (g of r.porCategoria; track g.nombre) {
                  <tr><td>{{ g.nombre }}</td><td class="right mono">{{ g.cantidad }}</td><td class="right mono">{{ g.total | dinero }}</td>
                    <td class="right mono">{{ pct(g.total, sumaCat()).toFixed(1) }}%</td></tr>
                } @empty { <tr><td colspan="4" class="muted center">Sin datos.</td></tr> }
              </tbody>
            </table>
          </div>
        </div>
      } @else { <div class="card empty"><span class="spinner"></span></div> }

      @if (inv(); as i) {
        <h2 class="section">Inventario actual</h2>
        <div class="grid grid-4">
          <div class="card stat"><span class="label">Valor al costo</span><span class="value">{{ i.valorCosto | dinero }}</span></div>
          <div class="card stat"><span class="label">Valor a precio de venta</span><span class="value">{{ i.valorVenta | dinero }}</span></div>
          <div class="card stat"><span class="label">Productos activos</span><span class="value">{{ i.activos }}</span><span class="hint">de {{ i.productos }} registrados</span></div>
          <a class="card stat link" routerLink="/productos" [queryParams]="{ bajoStock: 1 }">
            <span class="label">Stock bajo</span>
            <span class="value" [class.warn-txt]="i.bajoStock > 0">{{ i.bajoStock }}</span>
            <span class="hint">@if (i.sinStock > 0) { <span class="badge danger"><app-icon name="alert" /> {{ i.sinStock }} sin stock</span> } @else { Ver productos → }</span>
          </a>
        </div>
      }
    </div>
  `,
  styles: [`
    .filters { display: flex; gap: 12px; padding: 12px 14px; margin-bottom: 10px; align-items: center; flex-wrap: wrap; }
    .date { width: 160px; }
    .period { margin-bottom: 12px; font-size: .85rem; }
    .small { font-size: .8rem; }
    .chart-card, .lists, .cat-card { margin-top: 16px; }
    .chart { position: relative; height: 260px; padding-left: 48px; }
    .plot { position: relative; height: 100%; }
    .chart svg { width: 100%; height: 100%; display: block; overflow: visible; }
    .grid-line { stroke: var(--grid); stroke-width: 1; vector-effect: non-scaling-stroke; }
    .axis { stroke: var(--line-strong); stroke-width: 1; vector-effect: non-scaling-stroke; }
    .bar { fill: var(--chart); transition: fill .12s; pointer-events: none; }
    .bar.on { fill: var(--chart-hover); }
    .hit { fill: transparent; cursor: pointer; }
    .y-labels { position: absolute; left: 0; top: 0; bottom: 0; width: 44px; pointer-events: none; }
    .y-labels span { position: absolute; right: 4px; transform: translateY(-50%); font-family: var(--mono); font-size: .7rem; color: var(--muted); }
    .x-labels { position: absolute; left: 0; right: 0; bottom: -2px; height: 18px; pointer-events: none; }
    .x-labels span { position: absolute; transform: translateX(-50%); font-size: .7rem; color: var(--muted); white-space: nowrap; }
    .tooltip { position: absolute; transform: translate(-50%, calc(-100% - 10px)); background: var(--ink); color: var(--bg); padding: 8px 10px;
      border-radius: 9px; pointer-events: none; white-space: nowrap; box-shadow: var(--shadow-lg); z-index: 2; }
    .tt-date { font-size: .72rem; opacity: .75; } .tt-val { font-weight: 600; } .tt-sub { font-size: .72rem; opacity: .75; }
    .section { margin: 28px 0 12px; }
    .link { text-decoration: none; color: inherit; } .link:hover { border-color: var(--brand-2); }
    .warn-txt { color: var(--warn); }
    .badge app-icon { width: 12px; height: 12px; }
  `],
})
export class ReportesComponent {
  private api = inject(Api);
  private toast = inject(ToastService);
  negocio = inject(NegocioService);

  readonly W = 1000;
  readonly H = 260;
  readonly PL = 0;
  readonly PB = 22;

  rango = signal<Rango>('7d');
  desde = '';
  hasta = '';
  r = signal<Resumen | null>(null);
  inv = signal<Inventario | null>(null);
  hover = signal<number | null>(null);

  constructor() {
    this.setRango('7d');
    this.api.inventario().then(i => this.inv.set(i)).catch(e => this.toast.error(errorMsg(e)));
  }

  setRango(r: Rango) {
    this.rango.set(r);
    const hoy = new Date();
    const atras = (d: number) => isoLocal(new Date(hoy.getTime() - d * 864e5));
    this.hasta = isoLocal(hoy);
    this.desde = r === 'hoy' ? this.hasta : r === '7d' ? atras(6) : r === '30d' ? atras(29)
      : isoLocal(new Date(hoy.getFullYear(), hoy.getMonth(), 1));
    this.cargar();
  }

  async cargar() {
    if (!this.desde || !this.hasta) return;
    try {
      this.hover.set(null);
      this.r.set(await this.api.resumen(this.desde, this.hasta));
    } catch (e) { this.toast.error(errorMsg(e)); }
  }

  /** Serie continua (rellena días sin ventas con 0) proyectada al viewBox. */
  barras = computed<Barra[]>(() => {
    const r = this.r();
    if (!r || r.cantidadVentas === 0) return [];
    const map = new Map(r.porDia.map(d => [d.fecha, d]));
    const dias: { fecha: string; total: number; cantidad: number }[] = [];
    const d = new Date(r.desde + 'T00:00:00');
    const fin = new Date(r.hasta + 'T00:00:00');
    while (d <= fin && dias.length < 400) {
      const k = isoLocal(d);
      const x = map.get(k);
      dias.push({ fecha: k, total: x?.total ?? 0, cantidad: x?.cantidad ?? 0 });
      d.setDate(d.getDate() + 1);
    }
    const max = this.escala();
    const ancho = (this.W - this.PL) / dias.length;
    const w = Math.max(2, Math.min(48, ancho * 0.62));
    const alto = this.H - this.PB - 8;
    return dias.map((x, i) => {
      const h = max ? (x.total / max) * alto : 0;
      return { ...x, w, h, x: this.PL + i * ancho + (ancho - w) / 2, y: this.H - this.PB - h };
    });
  });

  escala = computed(() => {
    const r = this.r();
    const max = Math.max(0, ...(r?.porDia.map(d => d.total) ?? [0]));
    if (max <= 0) return 0;
    const mag = Math.pow(10, Math.floor(Math.log10(max)));
    const paso = [1, 2, 2.5, 5, 10].find(s => s * mag * 4 >= max)! * mag;
    return paso * 4;
  });

  ticks = computed(() => {
    const m = this.escala();
    const alto = this.H - this.PB - 8;
    return [0, 1, 2, 3, 4].map(i => ({ v: (m / 4) * i, y: this.H - this.PB - (alto / 4) * i }));
  });

  barPath(b: Barra): string {
    if (b.h <= 0) return '';
    const r = Math.min(4, b.w / 2, b.h);
    const base = this.H - this.PB;
    return `M${b.x},${base} V${b.y + r} Q${b.x},${b.y} ${b.x + r},${b.y} H${b.x + b.w - r} Q${b.x + b.w},${b.y} ${b.x + b.w},${b.y + r} V${base} Z`;
  }

  mostrarEtiqueta(i: number): boolean {
    const n = this.barras().length;
    const cada = Math.ceil(n / 10);
    return i % cada === 0;
  }

  dia(iso: string) {
    const d = new Date(iso + 'T00:00:00');
    return d.toLocaleDateString('es-PY', { day: '2-digit', month: 'short' });
  }

  compacto(v: number) {
    return new Intl.NumberFormat('es-PY', { notation: 'compact', maximumFractionDigits: 1 }).format(v);
  }

  margen = computed(() => {
    const r = this.r();
    const venta = r ? r.costo + r.ganancia : 0;
    return venta > 0 ? ((r!.ganancia / venta) * 100).toFixed(1) + '%' : '—';
  });

  maxTop = computed(() => Math.max(0, ...(this.r()?.topProductos.map(p => p.total) ?? [0])));
  sumaCat = computed(() => (this.r()?.porCategoria ?? []).reduce((s, g) => s + g.total, 0));
  max(gs: Grupo[]) { return Math.max(0, ...gs.map(g => g.total)); }
  pct(v: number, max: number) { return max > 0 ? (v / max) * 100 : 0; }
  metodo(m: string) { return METODOS.find(x => x.id === m)?.label ?? m; }
  imprimir() { window.print(); }
}

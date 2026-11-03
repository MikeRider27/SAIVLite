import { ChangeDetectionStrategy, Component, ElementRef, HostListener, computed, effect, inject, signal, viewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Api, errorMsg } from '../core/api.service';
import { CantidadPipe, DineroPipe } from '../core/format.pipes';
import { METODOS, MetodoPago, Producto, Sesion, Venta } from '../core/models';
import { NegocioService } from '../core/negocio.service';
import { ToastService } from '../core/toast.service';
import { IconComponent } from '../shared/icon.component';
import { ModalComponent } from '../shared/modal.component';
import { imprimirTicket } from '../shared/ticket';

interface Linea { producto: Producto; cantidad: number; }

const CARRITO_KEY = 'saiv.carrito';

@Component({
  selector: 'app-pos',
  standalone: true,
  imports: [FormsModule, RouterLink, IconComponent, ModalComponent, DineroPipe, CantidadPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (sesion() === undefined) {
      <div class="page"><div class="empty"><span class="spinner"></span></div></div>
    } @else if (sesion() === null) {
      <div class="page">
        <div class="open-box card">
          <div class="open-icon"><app-icon name="cash" /></div>
          <h1>Abrir caja</h1>
          <p class="muted">Ingresá el dinero con el que comenzás el turno. Al cerrar la caja se calcula el arqueo.</p>
          <form class="stack" (ngSubmit)="abrirCaja()">
            <div class="field">
              <label for="mi">Monto inicial ({{ negocio.datos().moneda }})</label>
              <input id="mi" class="input input-lg num mono" type="number" min="0" step="any" name="mi" [(ngModel)]="montoInicial" autofocus>
            </div>
            <button class="btn btn-accent btn-lg btn-block" [disabled]="ocupado()">Abrir caja y empezar a vender</button>
          </form>
        </div>
      </div>
    } @else {
      <div class="pos">
        <section class="left">
          <div class="search card">
            <div class="input-group">
              <app-icon name="scan" />
              <input #buscador class="input input-lg" placeholder="Escaneá o escribí código / nombre…  (ej. 3*7790001)"
                     [ngModel]="busqueda()" (ngModelChange)="onBuscar($event)" (keydown)="teclaBuscador($event)" autocomplete="off" autofocus>
              <kbd class="kbd-hint hide-sm">F3</kbd>
            </div>
            @if (resultados().length > 0) {
              <ul class="results">
                @for (p of resultados(); track p.id; let i = $index) {
                  <li [class.sel]="i === resIdx()" (mousedown)="$event.preventDefault(); agregar(p)">
                    <div class="grow">
                      <div class="r-desc">{{ p.descripcion }}</div>
                      <div class="r-meta mono">{{ p.codigo }} @if (p.categoria) { · {{ p.categoria }} }</div>
                    </div>
                    @if (p.controlaInventario) {
                      <span class="badge" [class.danger]="p.existencias <= 0" [class.warn]="p.bajoStock && p.existencias > 0">{{ p.existencias | cant }}</span>
                    }
                    <span class="mono r-price">{{ p.precioVenta | dinero }}</span>
                  </li>
                }
              </ul>
            }
          </div>

          <div class="cart card">
            @if (carrito().length === 0) {
              <div class="empty">
                <app-icon name="cart" />
                <p>El carrito está vacío.<br>Escaneá un producto para comenzar.</p>
              </div>
            } @else {
              <div class="table-wrap">
                <table class="table">
                  <thead><tr><th>Producto</th><th class="right">Precio</th><th class="center">Cantidad</th><th class="right">Importe</th><th></th></tr></thead>
                  <tbody>
                    @for (l of carrito(); track l.producto.id; let i = $index) {
                      <tr [class.sel]="i === carrito().length - 1">
                        <td>
                          <div class="c-desc">{{ l.producto.descripcion }}</div>
                          <div class="muted mono c-code">{{ l.producto.codigo }}
                            @if (excede(l)) { <span class="badge danger">stock {{ l.producto.existencias | cant }}</span> }
                          </div>
                        </td>
                        <td class="right mono nowrap">{{ l.producto.precioVenta | dinero:false }}</td>
                        <td class="center">
                          <div class="qty">
                            <button class="btn btn-sm btn-icon" (click)="sumar(l, -1)" aria-label="Menos"><app-icon name="minus" /></button>
                            <input class="input num mono" type="number" [step]="l.producto.unidad === 'GRANEL' ? 0.001 : 1" min="0"
                                   [ngModel]="l.cantidad" (ngModelChange)="setCantidad(l, $event)">
                            <button class="btn btn-sm btn-icon" (click)="sumar(l, 1)" aria-label="Más"><app-icon name="plus" /></button>
                          </div>
                        </td>
                        <td class="right mono nowrap strong">{{ importe(l) | dinero:false }}</td>
                        <td class="actions"><button class="btn btn-ghost btn-icon btn-sm" (click)="quitar(l)" aria-label="Quitar"><app-icon name="trash" /></button></td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            }
          </div>
        </section>

        <aside class="right-panel">
          <div class="total-card">
            <div class="t-label">Total a cobrar</div>
            <div class="t-value mono">{{ total() | dinero }}</div>
            <div class="t-meta">{{ carrito().length }} {{ carrito().length === 1 ? 'producto' : 'productos' }} · {{ unidades() | cant }} u.</div>
          </div>
          <button class="btn btn-accent btn-lg btn-block" [disabled]="carrito().length === 0" (click)="abrirCobro()">
            <app-icon name="cash" /> Cobrar <kbd>F2</kbd>
          </button>
          <button class="btn btn-block" [disabled]="carrito().length === 0" (click)="vaciar()"><app-icon name="trash" /> Vaciar carrito</button>
          @if (ultima(); as u) {
            <div class="last card">
              <div class="row"><span class="label-sm grow">Última venta #{{ u.id }}</span><span class="mono">{{ u.total | dinero }}</span></div>
              @if (u.metodoPago === 'EFECTIVO') { <div class="row"><span class="muted grow">Vuelto</span><span class="mono strong">{{ u.cambio | dinero }}</span></div> }
              <button class="btn btn-sm btn-block" (click)="reimprimir(u)"><app-icon name="printer" /> Reimprimir ticket</button>
            </div>
          }
          <div class="session muted">
            <app-icon name="cash" />
            <span>Caja #{{ sesion()!.id }} abierta · <a routerLink="/caja">ver caja</a></span>
          </div>
        </aside>
      </div>
    }

    @if (cobrando()) {
      <app-modal titulo="Cobrar" [subtitulo]="carrito().length + ' productos'" [ancho]="460" (cerrar)="cobrando.set(false)">
        <form id="cobro" class="stack" (ngSubmit)="confirmarVenta()">
          <div class="cobro-total">
            <span class="muted">Total</span>
            <span class="mono">{{ total() | dinero }}</span>
          </div>
          <div class="segmented metodos">
            @for (m of metodos; track m.id) {
              <button type="button" [class.on]="metodo() === m.id" (click)="metodo.set(m.id)">{{ m.label }}</button>
            }
          </div>
          @if (metodo() === 'EFECTIVO') {
            <div class="field">
              <label for="rec">Recibido</label>
              <input #recibidoInput id="rec" class="input input-lg num mono" type="number" min="0" step="any" name="rec"
                     [ngModel]="recibido()" (ngModelChange)="recibido.set(+$event || 0)">
            </div>
            <div class="quick">
              <button type="button" class="btn btn-sm" (click)="recibido.set(total())">Exacto</button>
              @for (q of rapidos(); track q) {
                <button type="button" class="btn btn-sm mono" (click)="recibido.set(q)">{{ q | dinero:false }}</button>
              }
            </div>
            <div class="vuelto" [class.neg]="vuelto() < 0">
              <span>{{ vuelto() < 0 ? 'Falta' : 'Vuelto' }}</span>
              <span class="mono">{{ (vuelto() < 0 ? -vuelto() : vuelto()) | dinero }}</span>
            </div>
          }
          <label class="check"><input type="checkbox" name="imp" [(ngModel)]="imprimir"> Imprimir ticket</label>
        </form>
        <div footer>
          <button class="btn" type="button" (click)="cobrando.set(false)">Cancelar</button>
          <button class="btn btn-primary" form="cobro" [disabled]="ocupado() || (metodo() === 'EFECTIVO' && vuelto() < 0)">
            @if (ocupado()) { <span class="spinner"></span> } Confirmar venta <kbd>Enter</kbd>
          </button>
        </div>
      </app-modal>
    }
  `,
  styles: [`
    .open-box { max-width: 440px; margin: 8vh auto 0; padding: 32px; text-align: center; display: flex; flex-direction: column; gap: 12px; }
    .open-box form { text-align: left; margin-top: 8px; }
    .open-icon { width: 64px; height: 64px; margin: 0 auto 4px; border-radius: 18px; background: var(--accent-soft); color: var(--warn); display: grid; place-items: center; }
    .open-icon app-icon { width: 32px; height: 32px; }

    .pos { display: grid; grid-template-columns: minmax(0, 1fr) 340px; gap: 20px; padding: 24px; max-width: 1440px; margin: 0 auto; min-height: 100vh; align-items: start; }
    @media (max-width: 1000px) { .pos { grid-template-columns: minmax(0, 1fr); padding: 16px; } }
    .left { display: flex; flex-direction: column; gap: 16px; min-width: 0; }
    .search { padding: 14px; position: relative; z-index: 5; }
    .kbd-hint { position: absolute; right: 12px; }
    .results { list-style: none; margin: 8px 0 0; padding: 4px; border-top: 1px solid var(--line); max-height: 340px; overflow: auto; }
    .results li { display: flex; align-items: center; gap: 12px; padding: 9px 10px; border-radius: 9px; cursor: pointer; }
    .results li:hover, .results li.sel { background: var(--brand-soft); }
    .r-desc { font-weight: 650; }
    .r-meta { font-size: .76rem; color: var(--muted); }
    .r-price { font-weight: 600; min-width: 90px; text-align: right; }
    .cart { min-height: 320px; }
    .c-desc { font-weight: 650; }
    .c-code { font-size: .76rem; display: flex; gap: 8px; align-items: center; }
    .qty { display: inline-flex; gap: 4px; align-items: center; }
    .qty .input { width: 82px; height: 32px; text-align: center; }
    .strong { font-weight: 700; }
    tr.sel td { background: color-mix(in srgb, var(--accent) 7%, transparent); }

    .right-panel { position: sticky; top: 24px; display: flex; flex-direction: column; gap: 12px; }
    .total-card { background: var(--side-bg); color: #f4efe2; border-radius: var(--radius); padding: 22px; box-shadow: var(--shadow);
      background-image: radial-gradient(circle at 100% 0, rgba(242,181,68,.2), transparent 50%); }
    .t-label { font-size: .75rem; text-transform: uppercase; letter-spacing: .14em; color: #a9c3b9; font-weight: 700; }
    .t-value { font-size: 2.3rem; font-weight: 600; letter-spacing: -.04em; margin: 6px 0 2px; color: var(--accent); word-break: break-all; }
    .t-meta { color: #a9c3b9; font-size: .85rem; }
    .last { padding: 14px; display: flex; flex-direction: column; gap: 8px; font-size: .88rem; }
    .session { display: flex; gap: 8px; align-items: center; font-size: .82rem; padding: 4px; }

    .cobro-total { display: flex; justify-content: space-between; align-items: baseline; }
    .cobro-total .mono { font-size: 2rem; font-weight: 600; letter-spacing: -.03em; }
    .metodos { width: 100%; } .metodos button { flex: 1; }
    .quick { display: flex; gap: 6px; flex-wrap: wrap; }
    .vuelto { display: flex; justify-content: space-between; align-items: baseline; padding: 14px 16px; border-radius: 12px; background: var(--ok-soft); color: var(--ok); font-weight: 700; }
    .vuelto .mono { font-size: 1.5rem; }
    .vuelto.neg { background: var(--danger-soft); color: var(--danger); }
  `],
})
export class PosComponent {
  private api = inject(Api);
  private toast = inject(ToastService);
  negocio = inject(NegocioService);

  buscador = viewChild<ElementRef<HTMLInputElement>>('buscador');
  recibidoInput = viewChild<ElementRef<HTMLInputElement>>('recibidoInput');

  sesion = signal<Sesion | null | undefined>(undefined);
  montoInicial = 0;
  ocupado = signal(false);

  busqueda = signal('');
  resultados = signal<Producto[]>([]);
  resIdx = signal(0);
  private timer?: ReturnType<typeof setTimeout>;

  carrito = signal<Linea[]>(this.cargarCarrito());
  total = computed(() => this.carrito().reduce((s, l) => s + this.importe(l), 0));
  unidades = computed(() => this.carrito().reduce((s, l) => s + l.cantidad, 0));

  cobrando = signal(false);
  metodos = METODOS;
  metodo = signal<MetodoPago>('EFECTIVO');
  recibido = signal(0);
  vuelto = computed(() => this.recibido() - this.total());
  imprimir = true;
  ultima = signal<Venta | null>(null);

  rapidos = computed(() => {
    const t = this.total();
    const billetes = [1000, 2000, 5000, 10000, 20000, 50000, 100000];
    const set = new Set<number>();
    for (const b of billetes) {
      const r = Math.ceil(t / b) * b;
      if (r > t) set.add(r);
    }
    return [...set].sort((a, b) => a - b).slice(0, 4);
  });

  constructor() {
    this.cargarSesion();
    effect(() => {
      try { localStorage.setItem(CARRITO_KEY, JSON.stringify(this.carrito())); } catch { /* */ }
    });
  }

  private cargarCarrito(): Linea[] {
    try { return JSON.parse(localStorage.getItem(CARRITO_KEY) ?? '[]'); } catch { return []; }
  }

  async cargarSesion() {
    try { this.sesion.set(await this.api.cajaActual()); } catch (e) { this.toast.error(errorMsg(e)); this.sesion.set(null); }
  }

  async abrirCaja() {
    this.ocupado.set(true);
    try {
      this.sesion.set(await this.api.abrirCaja(Number(this.montoInicial) || 0));
      this.toast.ok('Caja abierta');
      setTimeout(() => this.enfocar());
    } catch (e) { this.toast.error(errorMsg(e)); } finally { this.ocupado.set(false); }
  }

  enfocar() { this.buscador()?.nativeElement.focus(); }

  importe(l: Linea) { return Math.round(l.producto.precioVenta * l.cantidad * 100) / 100; }
  excede(l: Linea) { return l.producto.controlaInventario && l.cantidad > l.producto.existencias; }

  private parse(texto: string): { mult: number; termino: string } {
    const m = texto.match(/^\s*(\d+(?:[.,]\d+)?)\s*\*\s*(.+)$/);
    return m ? { mult: Number(m[1].replace(',', '.')), termino: m[2].trim() } : { mult: 1, termino: texto.trim() };
  }

  onBuscar(v: string) {
    this.busqueda.set(v);
    clearTimeout(this.timer);
    const { termino } = this.parse(v);
    if (termino.length < 2) { this.resultados.set([]); return; }
    this.timer = setTimeout(async () => {
      try {
        const r = await this.api.productos({ q: termino, soloActivos: true, size: 8 });
        if (this.parse(this.busqueda()).termino === termino) { this.resultados.set(r.content); this.resIdx.set(0); }
      } catch { /* silencioso */ }
    }, 180);
  }

  async teclaBuscador(ev: KeyboardEvent) {
    const res = this.resultados();
    if (ev.key === 'ArrowDown' && res.length) { ev.preventDefault(); this.resIdx.set((this.resIdx() + 1) % res.length); return; }
    if (ev.key === 'ArrowUp' && res.length) { ev.preventDefault(); this.resIdx.set((this.resIdx() - 1 + res.length) % res.length); return; }
    if (ev.key === 'Escape') { this.resultados.set([]); return; }
    if (ev.key !== 'Enter') return;
    ev.preventDefault();
    const { mult, termino } = this.parse(this.busqueda());
    if (!termino) return;
    clearTimeout(this.timer);
    try {
      // Primero código exacto (lector de barras), luego selección de la lista
      const p = await this.api.productoPorCodigo(termino).catch(() => null);
      if (p) {
        if (!p.activo) { this.toast.error(`${p.descripcion} está inactivo`); return; }
        this.agregar(p, mult);
      } else if (res.length) {
        this.agregar(res[this.resIdx()] ?? res[0], mult);
      } else {
        this.toast.error(`No se encontró "${termino}"`);
      }
    } catch (e) { this.toast.error(errorMsg(e)); }
  }

  agregar(p: Producto, cant?: number) {
    const mult = cant ?? this.parse(this.busqueda()).mult;
    const lineas = [...this.carrito()];
    const i = lineas.findIndex(l => l.producto.id === p.id);
    if (i >= 0) {
      const [l] = lineas.splice(i, 1);
      lineas.push({ producto: p, cantidad: this.redondear(l.cantidad + mult, p) });
    } else {
      lineas.push({ producto: p, cantidad: this.redondear(mult, p) });
    }
    this.carrito.set(lineas);
    this.busqueda.set('');
    this.resultados.set([]);
    this.enfocar();
  }

  private redondear(c: number, p: Producto) {
    return p.unidad === 'GRANEL' ? Math.round(c * 1000) / 1000 : Math.max(0, Math.round(c));
  }

  sumar(l: Linea, d: number) { this.setCantidad(l, l.cantidad + d); }

  setCantidad(l: Linea, v: number | string) {
    const c = this.redondear(Number(v) || 0, l.producto);
    if (c <= 0) { this.quitar(l); return; }
    this.carrito.update(ls => ls.map(x => x.producto.id === l.producto.id ? { ...x, cantidad: c } : x));
  }

  quitar(l: Linea) { this.carrito.update(ls => ls.filter(x => x.producto.id !== l.producto.id)); }

  vaciar() { this.carrito.set([]); this.enfocar(); }

  abrirCobro() {
    if (!this.carrito().length || !this.sesion()) return;
    this.metodo.set('EFECTIVO');
    this.recibido.set(this.total());
    this.cobrando.set(true);
    setTimeout(() => this.recibidoInput()?.nativeElement.select(), 50);
  }

  async confirmarVenta() {
    if (this.ocupado()) return;
    if (this.metodo() === 'EFECTIVO' && this.vuelto() < 0) return;
    this.ocupado.set(true);
    try {
      const v = await this.api.vender(
        this.carrito().map(l => ({ productoId: l.producto.id, cantidad: l.cantidad })),
        this.metodo(), this.metodo() === 'EFECTIVO' ? this.recibido() : this.total());
      this.ultima.set(v);
      this.carrito.set([]);
      this.cobrando.set(false);
      this.toast.ok(`Venta #${v.id} registrada`);
      if (this.imprimir) this.reimprimir(v);
      setTimeout(() => this.enfocar(), 50);
    } catch (e) {
      this.toast.error(errorMsg(e));
    } finally {
      this.ocupado.set(false);
    }
  }

  reimprimir(v: Venta) {
    imprimirTicket(v, this.negocio.datos(), (x, s) => this.negocio.dinero(x, s));
  }

  @HostListener('document:keydown', ['$event'])
  atajos(ev: KeyboardEvent) {
    if (ev.key === 'F2') { ev.preventDefault(); if (!this.cobrando()) this.abrirCobro(); }
    if (ev.key === 'F3') { ev.preventDefault(); this.enfocar(); }
  }
}

import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Api, errorMsg } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { CantidadPipe, DineroPipe, FechaHoraPipe } from '../core/format.pipes';
import { Categoria, Movimiento, Page, Producto, ProductoRequest } from '../core/models';
import { isoLocal } from '../core/negocio.service';
import { ConfirmService, ToastService } from '../core/toast.service';
import { IconComponent } from '../shared/icon.component';
import { ModalComponent } from '../shared/modal.component';

const VACIO: ProductoRequest = {
  codigo: '', descripcion: '', categoriaId: null, precioCompra: 0, precioVenta: 0, existencias: 0,
  stockMinimo: 0, unidad: 'UNIDAD', ubicacion: '', controlaInventario: true, activo: true,
};

@Component({
  selector: 'app-productos',
  standalone: true,
  imports: [FormsModule, IconComponent, ModalComponent, DineroPipe, CantidadPipe, FechaHoraPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page">
      <div class="page-head">
        <div><div class="eyebrow">Almacén</div><h1>Productos</h1></div>
        <div class="actions">
          @if (auth.puede('CATEGORIAS')) { <button class="btn" (click)="abrirCategorias()"><app-icon name="tag" /> Categorías</button> }
          @if (auth.puede('PRODUCTOS') || auth.puede('REPORTES')) { <button class="btn" (click)="exportar()"><app-icon name="download" /> <span class="hide-sm">Exportar</span></button> }
          @if (auth.puede('PRODUCTOS')) {
            <button class="btn" (click)="importando.set(true)"><app-icon name="upload" /> <span class="hide-sm">Importar</span></button>
            <button class="btn btn-primary" (click)="nuevo()"><app-icon name="plus" /> Nuevo producto</button>
          }
        </div>
      </div>

      <div class="card filters">
        <div class="input-group grow">
          <app-icon name="search" />
          <input class="input" placeholder="Buscar por código o descripción" [(ngModel)]="q" (ngModelChange)="buscarDebounced()">
        </div>
        <select class="input sel" [(ngModel)]="categoriaId" (change)="buscar(0)">
          <option [ngValue]="null">Todas las categorías</option>
          @for (c of categorias(); track c.id) { <option [ngValue]="c.id">{{ c.nombre }}</option> }
        </select>
        <label class="check"><input type="checkbox" [(ngModel)]="bajoStock" (change)="buscar(0)"> Solo stock bajo</label>
      </div>

      <div class="card">
        @if (pagina(); as pg) {
          @if (pg.content.length === 0) {
            <div class="empty"><app-icon name="box" /><p>No se encontraron productos.</p></div>
          } @else {
            <div class="table-wrap">
              <table class="table">
                <thead><tr>
                  <th class="sortable" (click)="ordenar('codigo')">Código</th>
                  <th class="sortable" (click)="ordenar('descripcion')">Descripción</th>
                  <th class="hide-sm">Categoría</th>
                  <th class="right sortable" (click)="ordenar('precioVenta')">Precio</th>
                  <th class="right sortable" (click)="ordenar('existencias')">Stock</th>
                  <th class="hide-sm">Ubicación</th>
                  <th></th>
                </tr></thead>
                <tbody>
                  @for (p of pg.content; track p.id) {
                    <tr [class.inactive]="!p.activo">
                      <td class="mono">{{ p.codigo }}</td>
                      <td>
                        <div class="strong">{{ p.descripcion }}</div>
                        <div class="tags">
                          @if (p.unidad === 'GRANEL') { <span class="badge brand">granel</span> }
                          @if (!p.activo) { <span class="badge">inactivo</span> }
                        </div>
                      </td>
                      <td class="hide-sm ink-2">{{ p.categoria ?? '—' }}</td>
                      <td class="right mono nowrap">{{ p.precioVenta | dinero }}</td>
                      <td class="right">
                        @if (p.controlaInventario) {
                          <span class="badge mono" [class.danger]="p.existencias <= 0" [class.warn]="p.bajoStock && p.existencias > 0" [class.ok]="!p.bajoStock">
                            {{ p.existencias | cant }}</span>
                        } @else { <span class="muted small">no controla</span> }
                      </td>
                      <td class="hide-sm ink-2">{{ p.ubicacion ?? '' }}</td>
                      <td class="actions">
                        @if (auth.puede('AJUSTAR_STOCK') && p.controlaInventario) {
                          <button class="btn btn-sm btn-ghost btn-icon" title="Ajustar stock" (click)="abrirAjuste(p)"><app-icon name="layers" /></button>
                        }
                        <button class="btn btn-sm btn-ghost btn-icon" title="Movimientos" (click)="verKardex(p)"><app-icon name="history" /></button>
                        @if (auth.puede('PRODUCTOS')) {
                          <button class="btn btn-sm btn-ghost btn-icon" title="Editar" (click)="editar(p)"><app-icon name="edit" /></button>
                          <button class="btn btn-sm btn-ghost btn-icon" title="Eliminar" (click)="eliminar(p)"><app-icon name="trash" /></button>
                        }
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
            <div class="pager">
              <span class="grow">{{ pg.totalElements }} productos · página {{ pg.page + 1 }} de {{ pg.totalPages || 1 }}</span>
              <button class="btn btn-sm btn-icon" [disabled]="pg.page === 0" (click)="buscar(pg.page - 1)"><app-icon name="left" /></button>
              <button class="btn btn-sm btn-icon" [disabled]="pg.page + 1 >= pg.totalPages" (click)="buscar(pg.page + 1)"><app-icon name="right" /></button>
            </div>
          }
        } @else { <div class="empty"><span class="spinner"></span></div> }
      </div>
    </div>

    <!-- Formulario de producto -->
    @if (form(); as f) {
      <app-modal [titulo]="editId ? 'Editar producto' : 'Nuevo producto'" [ancho]="640" (cerrar)="form.set(null)">
        <form id="pf" class="grid grid-2" (ngSubmit)="guardar(f)">
          <div class="field"><label>Código de barras</label><input class="input mono" name="codigo" [(ngModel)]="f.codigo" required maxlength="60" autofocus></div>
          <div class="field"><label>Categoría</label>
            <select class="input" name="cat" [(ngModel)]="f.categoriaId">
              <option [ngValue]="null">Sin categoría</option>
              @for (c of categorias(); track c.id) { <option [ngValue]="c.id">{{ c.nombre }}</option> }
            </select>
          </div>
          <div class="field span2"><label>Descripción</label><input class="input" name="desc" [(ngModel)]="f.descripcion" required maxlength="200"></div>
          <div class="field"><label>Precio de compra</label><input class="input num mono" type="number" min="0" step="any" name="pc" [(ngModel)]="f.precioCompra" required></div>
          <div class="field"><label>Precio de venta</label><input class="input num mono" type="number" min="0" step="any" name="pv" [(ngModel)]="f.precioVenta" required>
            @if (f.precioVenta > 0 && f.precioCompra > 0) { <span class="help">Margen {{ ((f.precioVenta - f.precioCompra) / f.precioVenta * 100).toFixed(1) }}%</span> }
          </div>
          <div class="field"><label>Se vende por</label>
            <div class="segmented"><button type="button" [class.on]="f.unidad === 'UNIDAD'" (click)="f.unidad = 'UNIDAD'">Unidad</button><button type="button" [class.on]="f.unidad === 'GRANEL'" (click)="f.unidad = 'GRANEL'">Granel (kg, l…)</button></div>
          </div>
          <div class="field"><label>Ubicación</label><input class="input" name="ub" [(ngModel)]="f.ubicacion" maxlength="80" placeholder="Ej.: Góndola 3"></div>
          <div class="field span2"><label class="check"><input type="checkbox" name="ci" [(ngModel)]="f.controlaInventario"> Controlar inventario de este producto</label></div>
          @if (f.controlaInventario) {
            @if (!editId) { <div class="field"><label>Stock inicial</label><input class="input num mono" type="number" min="0" step="any" name="ex" [(ngModel)]="f.existencias"></div> }
            <div class="field"><label>Stock mínimo</label><input class="input num mono" type="number" min="0" step="any" name="sm" [(ngModel)]="f.stockMinimo"><span class="help">Avisa cuando el stock llegue a este valor.</span></div>
          }
          <div class="field span2"><label class="check"><input type="checkbox" name="ac" [(ngModel)]="f.activo"> Activo (disponible para la venta)</label></div>
        </form>
        <div footer>
          <button class="btn" (click)="form.set(null)">Cancelar</button>
          <button class="btn btn-primary" form="pf" [disabled]="ocupado()">Guardar</button>
        </div>
      </app-modal>
    }

    <!-- Ajuste de stock -->
    @if (ajuste(); as p) {
      <app-modal titulo="Ajustar stock" [subtitulo]="p.descripcion + ' · stock actual ' + (p.existencias)" [ancho]="460" (cerrar)="ajuste.set(null)">
        <form id="af" class="stack" (ngSubmit)="guardarAjuste(p)">
          <div class="segmented tipos">
            <button type="button" [class.on]="ajTipo === 'ENTRADA'" (click)="ajTipo = 'ENTRADA'">Entrada</button>
            <button type="button" [class.on]="ajTipo === 'SALIDA'" (click)="ajTipo = 'SALIDA'">Salida</button>
            <button type="button" [class.on]="ajTipo === 'FIJAR'" (click)="ajTipo = 'FIJAR'">Conteo</button>
          </div>
          <div class="field">
            <label>{{ ajTipo === 'FIJAR' ? 'Stock contado' : 'Cantidad' }}</label>
            <input class="input input-lg num mono" type="number" min="0" step="any" name="ac" [(ngModel)]="ajCantidad" required autofocus>
            <span class="help">Stock resultante: <b class="mono">{{ resultante(p) }}</b></span>
          </div>
          <div class="field"><label>Motivo</label><input class="input" name="am" [(ngModel)]="ajMotivo" required maxlength="200" placeholder="Ej.: compra a proveedor, merma, inventario"></div>
        </form>
        <div footer>
          <button class="btn" (click)="ajuste.set(null)">Cancelar</button>
          <button class="btn btn-primary" form="af" [disabled]="ocupado() || !ajMotivo.trim()">Aplicar ajuste</button>
        </div>
      </app-modal>
    }

    <!-- Kardex -->
    @if (kardex(); as k) {
      <app-modal titulo="Movimientos de stock" [subtitulo]="k.producto.descripcion" [ancho]="680" (cerrar)="kardex.set(null)">
        @if (k.movs.length === 0) { <div class="empty">Sin movimientos registrados.</div> } @else {
          <div class="table-wrap">
            <table class="table">
              <thead><tr><th>Fecha</th><th>Tipo</th><th>Motivo</th><th class="right">Cantidad</th><th class="right">Saldo</th></tr></thead>
              <tbody>
                @for (m of k.movs; track m.id) {
                  <tr>
                    <td class="nowrap">{{ m.fechaHora | fh }}</td>
                    <td><span class="badge" [class.ok]="m.cantidad > 0" [class.warn]="m.cantidad < 0">{{ m.tipo }}</span></td>
                    <td class="ink-2">{{ m.motivo }}</td>
                    <td class="right mono">{{ m.cantidad > 0 ? '+' : '' }}{{ m.cantidad | cant }}</td>
                    <td class="right mono strong">{{ m.saldo | cant }}</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }
      </app-modal>
    }

    <!-- Categorías -->
    @if (gestionCategorias()) {
      <app-modal titulo="Categorías" [ancho]="480" (cerrar)="gestionCategorias.set(false)">
        <form class="row" (ngSubmit)="crearCategoria()">
          <input class="input grow" name="nc" [(ngModel)]="nuevaCategoria" placeholder="Nueva categoría" maxlength="80">
          <button class="btn btn-primary" [disabled]="!nuevaCategoria.trim()"><app-icon name="plus" /> Agregar</button>
        </form>
        <ul class="cats">
          @for (c of categorias(); track c.id) {
            <li>
              @if (editCat === c.id) {
                <input class="input grow" [(ngModel)]="editCatNombre" (keydown.enter)="renombrar(c)" autofocus>
                <button class="btn btn-sm btn-primary" (click)="renombrar(c)">Guardar</button>
              } @else {
                <span class="grow strong">{{ c.nombre }}</span>
                <span class="muted small">{{ c.productos }} productos</span>
                <button class="btn btn-sm btn-ghost btn-icon" (click)="editCat = c.id; editCatNombre = c.nombre"><app-icon name="edit" /></button>
                <button class="btn btn-sm btn-ghost btn-icon" (click)="eliminarCategoria(c)"><app-icon name="trash" /></button>
              }
            </li>
          }
        </ul>
      </app-modal>
    }

    <!-- Importar -->
    @if (importando()) {
      <app-modal titulo="Importar productos (CSV)" [ancho]="560" (cerrar)="importando.set(false); importRes.set(null)">
        <div class="stack">
          <p class="ink-2">Archivo separado por <b>;</b> con encabezado. Los productos se crean o actualizan por código.</p>
          <pre class="mono fmt">codigo;descripcion;categoria;precio_compra;precio_venta;existencias;stock_minimo;unidad;ubicacion;controla_inventario;activo</pre>
          <p class="muted small">Solo las 5 primeras columnas son obligatorias. Tip: exportá primero para obtener la plantilla.</p>
          <input class="input file" type="file" accept=".csv,text/csv" (change)="importar($event)">
          @if (importRes(); as r) {
            <div class="alert info"><app-icon name="check" />{{ r.creados }} creados · {{ r.actualizados }} actualizados</div>
            @if (r.errores.length) {
              <div class="alert danger"><div><b>{{ r.errores.length }} líneas con error:</b>
                <ul>@for (e of r.errores.slice(0, 12); track $index) { <li>{{ e }}</li> }</ul></div></div>
            }
          }
        </div>
      </app-modal>
    }
  `,
  styles: [`
    .filters { display: flex; gap: 12px; padding: 14px 16px; margin-bottom: 14px; align-items: center; flex-wrap: wrap; }
    .filters .input-group { min-width: 220px; }
    .sel { width: 220px; }
    .strong { font-weight: 650; }
    .small { font-size: .8rem; }
    .tags { display: flex; gap: 4px; margin-top: 2px; }
    .tags:empty { display: none; }
    tr.inactive td { opacity: .55; }
    th.sortable { cursor: pointer; } th.sortable:hover { color: var(--ink); }
    .span2 { grid-column: 1 / -1; }
    .tipos { width: 100%; } .tipos button { flex: 1; }
    .cats { list-style: none; padding: 0; margin: 16px 0 0; display: flex; flex-direction: column; }
    .cats li { display: flex; align-items: center; gap: 8px; padding: 8px 4px; border-bottom: 1px solid var(--line); }
    .fmt { background: var(--surface-3); padding: 10px; border-radius: 8px; font-size: .72rem; white-space: pre-wrap; word-break: break-all; margin: 0; }
    .file { padding-top: 7px; }
    @media (max-width: 720px) { .sel { width: 100%; } }
  `],
})
export class ProductosComponent {
  private api = inject(Api);
  private toast = inject(ToastService);
  private confirm = inject(ConfirmService);
  auth = inject(AuthService);

  q = '';
  categoriaId: number | null = null;
  bajoStock = !!inject(ActivatedRoute).snapshot.queryParamMap.get('bajoStock');
  sort = 'descripcion';
  desc = false;
  pagina = signal<Page<Producto> | null>(null);
  categorias = signal<Categoria[]>([]);
  ocupado = signal(false);
  private timer?: ReturnType<typeof setTimeout>;

  form = signal<ProductoRequest | null>(null);
  editId: number | null = null;

  ajuste = signal<Producto | null>(null);
  ajTipo: 'ENTRADA' | 'SALIDA' | 'FIJAR' = 'ENTRADA';
  ajCantidad = 0;
  ajMotivo = '';

  kardex = signal<{ producto: Producto; movs: Movimiento[] } | null>(null);

  gestionCategorias = signal(false);
  nuevaCategoria = '';
  editCat: number | null = null;
  editCatNombre = '';

  importando = signal(false);
  importRes = signal<{ creados: number; actualizados: number; errores: string[] } | null>(null);

  constructor() {
    this.buscar(0);
    this.cargarCategorias();
  }

  async cargarCategorias() {
    try { this.categorias.set(await this.api.categorias()); } catch { /* */ }
  }

  buscarDebounced() {
    clearTimeout(this.timer);
    this.timer = setTimeout(() => this.buscar(0), 250);
  }

  ordenar(campo: string) {
    this.desc = this.sort === campo ? !this.desc : false;
    this.sort = campo;
    this.buscar(0);
  }

  async buscar(page: number) {
    try {
      this.pagina.set(await this.api.productos({
        q: this.q, categoriaId: this.categoriaId, bajoStock: this.bajoStock, page, size: 25, sort: this.sort, desc: this.desc,
      }));
    } catch (e) { this.toast.error(errorMsg(e)); }
  }

  nuevo() { this.editId = null; this.form.set({ ...VACIO }); }

  editar(p: Producto) {
    this.editId = p.id;
    this.form.set({
      codigo: p.codigo, descripcion: p.descripcion, categoriaId: p.categoriaId, precioCompra: p.precioCompra,
      precioVenta: p.precioVenta, existencias: p.existencias, stockMinimo: p.stockMinimo, unidad: p.unidad,
      ubicacion: p.ubicacion ?? '', controlaInventario: p.controlaInventario, activo: p.activo,
    });
  }

  async guardar(f: ProductoRequest) {
    this.ocupado.set(true);
    try {
      if (this.editId) await this.api.actualizarProducto(this.editId, f);
      else await this.api.crearProducto(f);
      this.toast.ok('Producto guardado');
      this.form.set(null);
      this.buscar(this.pagina()?.page ?? 0);
      this.cargarCategorias();
    } catch (e) { this.toast.error(errorMsg(e)); } finally { this.ocupado.set(false); }
  }

  async eliminar(p: Producto) {
    if (!await this.confirm.preguntar('Eliminar producto', `¿Eliminar "${p.descripcion}"? Las ventas históricas conservan sus datos.`, { ok: 'Eliminar', peligro: true })) return;
    try {
      await this.api.eliminarProducto(p.id);
      this.toast.ok('Producto eliminado');
      this.buscar(this.pagina()?.page ?? 0);
    } catch (e) { this.toast.error(errorMsg(e)); }
  }

  abrirAjuste(p: Producto) {
    this.ajTipo = 'ENTRADA';
    this.ajCantidad = 0;
    this.ajMotivo = '';
    this.ajuste.set(p);
  }

  resultante(p: Producto): string {
    const c = Number(this.ajCantidad) || 0;
    const r = this.ajTipo === 'ENTRADA' ? p.existencias + c : this.ajTipo === 'SALIDA' ? p.existencias - c : c;
    return new Intl.NumberFormat('es-PY', { maximumFractionDigits: 3 }).format(r);
  }

  async guardarAjuste(p: Producto) {
    this.ocupado.set(true);
    try {
      await this.api.ajustarStock(p.id, this.ajTipo, Number(this.ajCantidad) || 0, this.ajMotivo.trim());
      this.toast.ok('Stock actualizado');
      this.ajuste.set(null);
      this.buscar(this.pagina()?.page ?? 0);
    } catch (e) { this.toast.error(errorMsg(e)); } finally { this.ocupado.set(false); }
  }

  async verKardex(p: Producto) {
    try { this.kardex.set({ producto: p, movs: await this.api.movimientos(p.id) }); }
    catch (e) { this.toast.error(errorMsg(e)); }
  }

  abrirCategorias() { this.cargarCategorias(); this.gestionCategorias.set(true); }

  async crearCategoria() {
    try {
      await this.api.crearCategoria(this.nuevaCategoria.trim());
      this.nuevaCategoria = '';
      this.cargarCategorias();
    } catch (e) { this.toast.error(errorMsg(e)); }
  }

  async renombrar(c: Categoria) {
    try {
      await this.api.actualizarCategoria(c.id, this.editCatNombre.trim());
      this.editCat = null;
      this.cargarCategorias();
      this.buscar(this.pagina()?.page ?? 0);
    } catch (e) { this.toast.error(errorMsg(e)); }
  }

  async eliminarCategoria(c: Categoria) {
    if (!await this.confirm.preguntar('Eliminar categoría', `¿Eliminar "${c.nombre}"? ${c.productos} productos quedarán sin categoría.`, { ok: 'Eliminar', peligro: true })) return;
    try {
      await this.api.eliminarCategoria(c.id);
      this.cargarCategorias();
      this.buscar(this.pagina()?.page ?? 0);
    } catch (e) { this.toast.error(errorMsg(e)); }
  }

  async exportar() {
    try {
      const blob = await this.api.exportarProductos();
      const a = document.createElement('a');
      a.href = URL.createObjectURL(blob);
      a.download = `productos-${isoLocal()}.csv`;
      a.click();
      URL.revokeObjectURL(a.href);
    } catch (e) { this.toast.error(errorMsg(e)); }
  }

  async importar(ev: Event) {
    const file = (ev.target as HTMLInputElement).files?.[0];
    if (!file) return;
    try {
      this.importRes.set(await this.api.importarProductos(file));
      this.buscar(0);
      this.cargarCategorias();
    } catch (e) { this.toast.error(errorMsg(e)); }
    (ev.target as HTMLInputElement).value = '';
  }
}

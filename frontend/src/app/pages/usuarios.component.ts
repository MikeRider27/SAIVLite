import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Api, errorMsg } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { FechaPipe } from '../core/format.pipes';
import { PERMISOS, Permiso, Rol, Usuario } from '../core/models';
import { ConfirmService, ToastService } from '../core/toast.service';
import { IconComponent } from '../shared/icon.component';
import { ModalComponent } from '../shared/modal.component';

interface UsuarioForm { nombre: string; username: string; password: string; rol: Rol; activo: boolean; permisos: Permiso[]; }

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [FormsModule, IconComponent, ModalComponent, FechaPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page">
      <div class="page-head">
        <div><div class="eyebrow">Accesos</div><h1>Usuarios</h1></div>
        <div class="actions"><button class="btn btn-primary" (click)="nuevo()"><app-icon name="plus" /> Nuevo usuario</button></div>
      </div>

      <div class="cards">
        @for (u of usuarios(); track u.id) {
          <div class="card ucard" [class.off]="!u.activo">
            <div class="row">
              <div class="avatar">{{ iniciales(u.nombre) }}</div>
              <div class="grow">
                <div class="strong">{{ u.nombre }} @if (u.id === auth.usuario()?.id) { <span class="muted">(vos)</span> }</div>
                <div class="muted mono small">&#64;{{ u.username }}</div>
              </div>
              <span class="badge" [class.accent]="u.rol === 'ADMIN'" [class.brand]="u.rol === 'CAJERO'">{{ u.rol === 'ADMIN' ? 'Administrador' : 'Cajero' }}</span>
            </div>
            <div class="perms">
              @if (u.rol === 'ADMIN') { <span class="muted small">Acceso total</span> }
              @else {
                @for (p of u.permisos; track p) { <span class="badge">{{ label(p) }}</span> }
                @if (u.permisos.length === 0) { <span class="muted small">Sin permisos</span> }
              }
            </div>
            <div class="row foot">
              @if (!u.activo) { <span class="badge danger">Inactivo</span> }
              <span class="muted small grow">Alta {{ u.creadoEn | f }}</span>
              <button class="btn btn-sm btn-ghost" (click)="editar(u)"><app-icon name="edit" /> Editar</button>
              @if (u.id !== auth.usuario()?.id) {
                <button class="btn btn-sm btn-ghost btn-icon" title="Eliminar" (click)="eliminar(u)"><app-icon name="trash" /></button>
              }
            </div>
          </div>
        }
      </div>
    </div>

    @if (form(); as f) {
      <app-modal [titulo]="editId ? 'Editar usuario' : 'Nuevo usuario'" [ancho]="600" (cerrar)="form.set(null)">
        <form id="uf" class="stack" (ngSubmit)="guardar(f)">
          <div class="grid grid-2">
            <div class="field"><label>Nombre completo</label><input class="input" name="n" [(ngModel)]="f.nombre" required maxlength="120"></div>
            <div class="field"><label>Usuario</label><input class="input mono" name="u" [(ngModel)]="f.username" required maxlength="60" autocomplete="off"></div>
            <div class="field"><label>{{ editId ? 'Nueva contraseña (opcional)' : 'Contraseña' }}</label>
              <input class="input" type="password" name="p" [(ngModel)]="f.password" [required]="!editId" minlength="6" autocomplete="new-password">
              <span class="help">Mínimo 6 caracteres.</span></div>
            <div class="field"><label>Rol</label>
              <div class="segmented"><button type="button" [class.on]="f.rol === 'CAJERO'" (click)="f.rol = 'CAJERO'">Cajero</button><button type="button" [class.on]="f.rol === 'ADMIN'" (click)="f.rol = 'ADMIN'">Administrador</button></div>
            </div>
          </div>
          @if (f.rol === 'CAJERO') {
            <div>
              <div class="label-sm">Permisos</div>
              <div class="perm-grid">
                @for (p of permisos; track p.id) {
                  <label class="perm" [class.on]="f.permisos.includes(p.id)">
                    <input type="checkbox" [checked]="f.permisos.includes(p.id)" (change)="toggle(f, p.id)">
                    <span><b>{{ p.label }}</b><span class="muted">{{ p.desc }}</span></span>
                  </label>
                }
              </div>
            </div>
          } @else {
            <div class="alert info"><app-icon name="check" />Los administradores tienen todos los permisos.</div>
          }
          <label class="check"><input type="checkbox" name="a" [(ngModel)]="f.activo"> Usuario activo</label>
        </form>
        <div footer>
          <button class="btn" (click)="form.set(null)">Cancelar</button>
          <button class="btn btn-primary" form="uf" [disabled]="ocupado()">Guardar</button>
        </div>
      </app-modal>
    }
  `,
  styles: [`
    .cards { display: grid; grid-template-columns: repeat(auto-fill, minmax(320px, 1fr)); gap: 14px; }
    @media (max-width: 720px) { .cards { grid-template-columns: minmax(0, 1fr); } }
    .ucard { padding: 16px; display: flex; flex-direction: column; gap: 12px; }
    .ucard.off { opacity: .6; }
    .avatar { width: 40px; height: 40px; border-radius: 12px; background: var(--brand-soft); color: var(--brand-2); display: grid; place-items: center; font-weight: 800; }
    .strong { font-weight: 700; }
    .small { font-size: .8rem; }
    .perms { display: flex; flex-wrap: wrap; gap: 5px; min-height: 24px; }
    .foot { border-top: 1px solid var(--line); padding-top: 10px; gap: 6px; }
    .perm-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 8px; margin-top: 8px; }
    @media (max-width: 600px) { .perm-grid { grid-template-columns: minmax(0, 1fr); } }
    .perm { display: flex; gap: 10px; align-items: flex-start; padding: 10px 12px; border: 1px solid var(--line); border-radius: 10px; cursor: pointer; }
    .perm.on { border-color: var(--brand-2); background: var(--brand-soft); }
    .perm input { margin-top: 3px; accent-color: var(--brand-2); }
    .perm span { display: flex; flex-direction: column; font-size: .85rem; }
    .perm .muted { font-size: .76rem; }
  `],
})
export class UsuariosComponent {
  private api = inject(Api);
  private toast = inject(ToastService);
  private confirm = inject(ConfirmService);
  auth = inject(AuthService);

  permisos = PERMISOS;
  usuarios = signal<Usuario[]>([]);
  form = signal<UsuarioForm | null>(null);
  editId: number | null = null;
  ocupado = signal(false);

  constructor() { this.cargar(); }

  async cargar() {
    try { this.usuarios.set(await this.api.usuarios()); } catch (e) { this.toast.error(errorMsg(e)); }
  }

  label(p: Permiso) { return PERMISOS.find(x => x.id === p)?.label ?? p; }
  iniciales(n: string) { return n.split(/\s+/).slice(0, 2).map(s => s[0]?.toUpperCase()).join(''); }

  nuevo() {
    this.editId = null;
    this.form.set({ nombre: '', username: '', password: '', rol: 'CAJERO', activo: true, permisos: ['VENDER'] });
  }

  editar(u: Usuario) {
    this.editId = u.id;
    this.form.set({ nombre: u.nombre, username: u.username, password: '', rol: u.rol, activo: u.activo, permisos: [...u.permisos] });
  }

  toggle(f: UsuarioForm, p: Permiso) {
    f.permisos = f.permisos.includes(p) ? f.permisos.filter(x => x !== p) : [...f.permisos, p];
  }

  async guardar(f: UsuarioForm) {
    this.ocupado.set(true);
    const body = { ...f, password: f.password || null };
    try {
      if (this.editId) await this.api.actualizarUsuario(this.editId, body);
      else await this.api.crearUsuario(body);
      this.toast.ok('Usuario guardado');
      this.form.set(null);
      this.cargar();
    } catch (e) { this.toast.error(errorMsg(e)); } finally { this.ocupado.set(false); }
  }

  async eliminar(u: Usuario) {
    if (!await this.confirm.preguntar('Eliminar usuario', `¿Eliminar a ${u.nombre}? Si tiene ventas registradas, desactivalo en su lugar.`, { ok: 'Eliminar', peligro: true })) return;
    try {
      await this.api.eliminarUsuario(u.id);
      this.toast.ok('Usuario eliminado');
      this.cargar();
    } catch (e) { this.toast.error(errorMsg(e)); }
  }
}

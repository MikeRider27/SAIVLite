import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { AuthService } from '../core/auth.service';
import { Permiso } from '../core/models';
import { NegocioService } from '../core/negocio.service';
import { IconComponent } from '../shared/icon.component';

interface NavItem { ruta: string; label: string; icon: string; permisos?: Permiso[]; }

const NAV: NavItem[] = [
  { ruta: '/vender', label: 'Vender', icon: 'cart', permisos: ['VENDER'] },
  { ruta: '/ventas', label: 'Ventas', icon: 'receipt' },
  { ruta: '/caja', label: 'Caja', icon: 'cash', permisos: ['VENDER', 'REPORTES'] },
  { ruta: '/productos', label: 'Productos', icon: 'box' },
  { ruta: '/reportes', label: 'Reportes', icon: 'chart', permisos: ['REPORTES'] },
  { ruta: '/usuarios', label: 'Usuarios', icon: 'users', permisos: ['USUARIOS'] },
  { ruta: '/configuracion', label: 'Configuración', icon: 'settings' },
];

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <aside [class.open]="abierto()">
      <div class="brand">
        <div class="logo">S</div>
        <div class="grow">
          <div class="name">SAIV<span>lite</span></div>
          <div class="biz">{{ negocio.datos().nombre }}</div>
        </div>
        <button class="close-nav btn btn-ghost btn-icon" (click)="abierto.set(false)" aria-label="Cerrar menú"><app-icon name="x" /></button>
      </div>
      <nav>
        @for (n of nav(); track n.ruta) {
          <a [routerLink]="n.ruta" routerLinkActive="active">
            <app-icon [name]="n.icon" />
            <span>{{ n.label }}</span>
          </a>
        }
      </nav>
      <div class="me">
        <div class="avatar">{{ iniciales() }}</div>
        <div class="grow">
          <div class="who">{{ auth.usuario()?.nombre }}</div>
          <div class="role">{{ auth.usuario()?.rol === 'ADMIN' ? 'Administrador' : 'Cajero' }}</div>
        </div>
        <button class="logout" (click)="auth.logout()" title="Cerrar sesión" aria-label="Cerrar sesión"><app-icon name="logout" /></button>
      </div>
    </aside>
    <div class="scrim" [class.show]="abierto()" (click)="abierto.set(false)"></div>
    <main>
      <div class="topbar">
        <button class="btn btn-ghost btn-icon" (click)="abierto.set(true)" aria-label="Abrir menú"><app-icon name="menu" /></button>
        <div class="name">SAIV<span>lite</span></div>
      </div>
      <router-outlet />
    </main>
  `,
  styles: [`
    :host { display: grid; grid-template-columns: 248px minmax(0, 1fr); min-height: 100vh; }
    aside {
      position: sticky; top: 0; height: 100vh; display: flex; flex-direction: column;
      background: var(--side-bg); color: var(--side-ink); padding: 18px 14px;
      background-image: radial-gradient(circle at 0 100%, rgba(242,181,68,.10), transparent 45%);
    }
    .brand { display: flex; gap: 12px; align-items: center; padding: 4px 8px 22px; }
    .logo { width: 38px; height: 38px; border-radius: 11px; background: var(--accent); color: var(--accent-ink);
      display: grid; place-items: center; font-weight: 800; font-size: 1.25rem; transform: rotate(-6deg); }
    .name { font-weight: 800; font-size: 1.15rem; letter-spacing: -.02em; color: #f4efe2; }
    .name span { font-weight: 500; color: var(--accent); margin-left: 2px; font-style: italic; }
    .biz { font-size: .76rem; color: var(--side-muted); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    nav { display: flex; flex-direction: column; gap: 2px; flex: 1; }
    nav a { display: flex; align-items: center; gap: 12px; padding: 10px 12px; border-radius: 10px;
      color: var(--side-ink); text-decoration: none; font-weight: 600; font-size: .93rem; position: relative; }
    nav a app-icon { width: 19px; height: 19px; color: var(--side-muted); }
    nav a:hover { background: rgba(255,255,255,.05); }
    nav a.active { background: var(--side-active); color: #fff; }
    nav a.active app-icon { color: var(--accent); }
    nav a.active::before { content: ''; position: absolute; left: -14px; top: 9px; bottom: 9px; width: 3px; border-radius: 0 3px 3px 0; background: var(--accent); }
    .me { display: flex; align-items: center; gap: 10px; padding: 12px 8px 4px; border-top: 1px solid rgba(255,255,255,.08); }
    .avatar { width: 34px; height: 34px; border-radius: 50%; background: var(--side-active); display: grid; place-items: center;
      font-weight: 700; font-size: .8rem; color: var(--accent); }
    .who { font-weight: 700; font-size: .86rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; color: #f4efe2; }
    .role { font-size: .74rem; color: var(--side-muted); }
    .logout { background: none; border: 0; color: var(--side-muted); cursor: pointer; padding: 6px; border-radius: 8px; display: grid; }
    .logout:hover { color: #fff; background: rgba(255,255,255,.06); }
    main { min-width: 0; }
    .topbar, .close-nav, .scrim { display: none; }

    @media (max-width: 900px) {
      :host { grid-template-columns: minmax(0, 1fr); }
      aside { position: fixed; z-index: 40; left: 0; width: 264px; transform: translateX(-100%); transition: transform .2s ease; }
      aside.open { transform: none; }
      .close-nav { display: inline-flex; color: var(--side-ink); }
      .scrim { position: fixed; inset: 0; z-index: 30; background: rgba(0,0,0,.4); }
      .scrim.show { display: block; }
      .topbar { display: flex; align-items: center; gap: 8px; padding: 10px 12px; background: var(--side-bg); position: sticky; top: 0; z-index: 20; }
      .topbar .btn { color: #f4efe2; }
    }
    @media print { aside, .topbar { display: none !important; } :host { display: block; } }
  `],
})
export class ShellComponent {
  auth = inject(AuthService);
  negocio = inject(NegocioService);
  abierto = signal(false);

  nav = computed(() => {
    this.auth.usuario();
    return NAV.filter(n => !n.permisos || n.permisos.some(p => this.auth.puede(p)));
  });

  iniciales = computed(() =>
    (this.auth.usuario()?.nombre ?? '?').split(/\s+/).slice(0, 2).map(s => s[0]?.toUpperCase()).join(''));

  constructor() {
    inject(Router).events.pipe(filter(e => e instanceof NavigationEnd)).subscribe(() => this.abierto.set(false));
  }
}

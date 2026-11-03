import { Routes } from '@angular/router';
import { authGuard, inicioGuard, permisoGuard } from './core/guards';
import { ShellComponent } from './layout/shell.component';
import { LoginComponent } from './pages/login.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent, title: 'Ingresar · SAIV Lite' },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    canActivateChild: [permisoGuard],
    children: [
      { path: '', pathMatch: 'full', canActivate: [inicioGuard], children: [] },
      { path: 'vender', data: { permisos: ['VENDER'] }, title: 'Vender · SAIV Lite',
        loadComponent: () => import('./pages/pos.component').then(m => m.PosComponent) },
      { path: 'ventas', title: 'Ventas · SAIV Lite',
        loadComponent: () => import('./pages/ventas.component').then(m => m.VentasComponent) },
      { path: 'caja', data: { permisos: ['VENDER', 'REPORTES'] }, title: 'Caja · SAIV Lite',
        loadComponent: () => import('./pages/caja.component').then(m => m.CajaComponent) },
      { path: 'productos', title: 'Productos · SAIV Lite',
        loadComponent: () => import('./pages/productos.component').then(m => m.ProductosComponent) },
      { path: 'reportes', data: { permisos: ['REPORTES'] }, title: 'Reportes · SAIV Lite',
        loadComponent: () => import('./pages/reportes.component').then(m => m.ReportesComponent) },
      { path: 'usuarios', data: { permisos: ['USUARIOS'] }, title: 'Usuarios · SAIV Lite',
        loadComponent: () => import('./pages/usuarios.component').then(m => m.UsuariosComponent) },
      { path: 'configuracion', title: 'Configuración · SAIV Lite',
        loadComponent: () => import('./pages/configuracion.component').then(m => m.ConfiguracionComponent) },
    ],
  },
  { path: '**', redirectTo: '' },
];

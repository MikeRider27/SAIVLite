import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { errorMsg } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { NegocioService } from '../core/negocio.service';
import { IconComponent } from '../shared/icon.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <section class="art" aria-hidden="true">
      <div class="brand"><div class="logo">S</div><div class="name">SAIV<span>lite</span></div></div>
      <div class="pitch">
        <h1>Vendé rápido.<br>Controlá todo.</h1>
        <p>Caja, inventario y reportes en un solo lugar, desde cualquier navegador.</p>
      </div>
      <div class="receipt">
        <div class="r-row"><span>Café molido 500g</span><span>2 ×</span></div>
        <div class="r-row"><span>Pan casero</span><span>6 ×</span></div>
        <div class="r-row"><span>Queso Paraguay</span><span>0,75</span></div>
        <div class="r-total"><span>TOTAL</span><span class="mono">✓</span></div>
      </div>
    </section>
    <section class="form-side">
      <form (ngSubmit)="entrar()" class="box">
        <div>
          <h2>Ingresar</h2>
          <p class="muted">Usá tu usuario y contraseña asignados.</p>
        </div>
        @if (expirada) { <div class="alert warn"><app-icon name="alert" />Tu sesión expiró. Ingresá nuevamente.</div> }
        @if (error()) { <div class="alert danger"><app-icon name="alert" />{{ error() }}</div> }
        <div class="field">
          <label for="u">Usuario</label>
          <div class="input-group"><app-icon name="user" />
            <input id="u" class="input input-lg" name="u" [(ngModel)]="username" autocomplete="username" autofocus required>
          </div>
        </div>
        <div class="field">
          <label for="p">Contraseña</label>
          <div class="input-group"><app-icon name="lock" />
            <input id="p" class="input input-lg" type="password" name="p" [(ngModel)]="password" autocomplete="current-password" required
                   (keyup)="caps.set($any($event).getModifierState?.('CapsLock'))">
          </div>
          @if (caps()) { <span class="help">Bloq Mayús está activado</span> }
        </div>
        <button class="btn btn-primary btn-lg btn-block" [disabled]="cargando() || !username || !password">
          @if (cargando()) { <span class="spinner"></span> } @else { Entrar }
        </button>
      </form>
    </section>
  `,
  styles: [`
    :host { display: grid; grid-template-columns: 1.1fr 1fr; min-height: 100vh; }
    .art { background: var(--side-bg); color: #f4efe2; padding: 40px 48px; display: flex; flex-direction: column; position: relative; overflow: hidden;
      background-image: radial-gradient(circle at 100% 0, rgba(242,181,68,.18), transparent 40%),
                        repeating-linear-gradient(135deg, rgba(255,255,255,.025) 0 2px, transparent 2px 14px); }
    .brand { display: flex; align-items: center; gap: 12px; }
    .logo { width: 42px; height: 42px; border-radius: 12px; background: var(--accent); color: var(--accent-ink);
      display: grid; place-items: center; font-weight: 800; font-size: 1.4rem; transform: rotate(-6deg); }
    .name { font-weight: 800; font-size: 1.35rem; letter-spacing: -.02em; }
    .name span { font-weight: 500; color: var(--accent); font-style: italic; margin-left: 2px; }
    .pitch { margin-top: auto; max-width: 440px; }
    .pitch h1 { font-size: clamp(2rem, 4vw, 3.2rem); line-height: 1.02; letter-spacing: -.04em; }
    .pitch p { margin-top: 14px; color: #b9cfc6; font-size: 1.02rem; }
    .receipt { position: absolute; right: 48px; top: 110px; width: 230px; background: #fbf7ec; color: #2b2a24; padding: 18px 16px 22px;
      font-family: var(--mono); font-size: .76rem; transform: rotate(4deg); box-shadow: 0 30px 50px -20px rgba(0,0,0,.6);
      clip-path: polygon(0 0, 100% 0, 100% 96%, 92% 100%, 84% 96%, 76% 100%, 68% 96%, 60% 100%, 52% 96%, 44% 100%, 36% 96%, 28% 100%, 20% 96%, 12% 100%, 4% 96%, 0 100%); }
    .r-row, .r-total { display: flex; justify-content: space-between; padding: 4px 0; border-bottom: 1px dashed #c9c1ac; }
    .r-total { border: 0; margin-top: 6px; font-weight: 700; font-size: .9rem; }
    .form-side { display: grid; place-items: center; padding: 32px 20px; }
    .box { width: 100%; max-width: 380px; display: flex; flex-direction: column; gap: 18px; }
    .box h2 { font-size: 1.7rem; font-weight: 800; }
    @media (max-width: 900px) {
      :host { grid-template-columns: 1fr; }
      .art { padding: 24px 20px; min-height: 0; }
      .pitch { margin-top: 28px; } .pitch h1 { font-size: 1.8rem; } .receipt { display: none; }
    }
  `],
})
export class LoginComponent {
  private auth = inject(AuthService);
  private negocio = inject(NegocioService);
  private router = inject(Router);
  expirada = !!inject(ActivatedRoute).snapshot.queryParamMap.get('expirada');

  username = '';
  password = '';
  cargando = signal(false);
  error = signal('');
  caps = signal(false);

  constructor() {
    if (this.auth.logueado()) this.router.navigateByUrl(this.auth.inicio());
  }

  async entrar() {
    this.cargando.set(true);
    this.error.set('');
    try {
      await this.auth.login(this.username, this.password);
      await this.negocio.cargar();
      this.router.navigateByUrl(this.auth.inicio());
    } catch (e) {
      this.error.set(errorMsg(e));
    } finally {
      this.cargando.set(false);
    }
  }
}

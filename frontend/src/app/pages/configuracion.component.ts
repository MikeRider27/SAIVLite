import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Api, errorMsg } from '../core/api.service';
import { AuthService } from '../core/auth.service';
import { Negocio } from '../core/models';
import { NegocioService } from '../core/negocio.service';
import { ToastService } from '../core/toast.service';
import { IconComponent } from '../shared/icon.component';

@Component({
  selector: 'app-configuracion',
  standalone: true,
  imports: [FormsModule, IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="page">
      <div class="page-head"><div><div class="eyebrow">Sistema</div><h1>Configuración</h1></div></div>

      <div class="layout">
        @if (auth.puede('CONFIGURACION')) {
          <form class="card" (ngSubmit)="guardar()">
            <div class="card-head"><app-icon name="store" /><h2>Negocio y ticket</h2></div>
            <div class="card-body grid grid-2">
              <div class="field span2"><label>Nombre del negocio</label><input class="input" name="n" [(ngModel)]="n.nombre" required maxlength="120"></div>
              <div class="field"><label>RUC</label><input class="input" name="r" [(ngModel)]="n.ruc" maxlength="40"></div>
              <div class="field"><label>Teléfono</label><input class="input" name="t" [(ngModel)]="n.telefono" maxlength="60"></div>
              <div class="field span2"><label>Dirección</label><input class="input" name="d" [(ngModel)]="n.direccion" maxlength="200"></div>
              <div class="field"><label>Símbolo de moneda</label><input class="input" name="m" [(ngModel)]="n.moneda" required maxlength="10"></div>
              <div class="field"><label>Decimales</label>
                <select class="input" name="dec" [(ngModel)]="n.decimales"><option [ngValue]="0">0 (ej. Guaraníes)</option><option [ngValue]="2">2 (ej. 10,50)</option></select></div>
              <div class="field span2"><label>Línea adicional del encabezado</label><input class="input" name="le" [(ngModel)]="n.lineaExtra" maxlength="200" placeholder="Ej.: Horario 7 a 21 h"></div>
              <div class="field"><label>Mensaje de agradecimiento</label><input class="input" name="g" [(ngModel)]="n.mensajeGracias" maxlength="200"></div>
              <div class="field"><label>Mensaje final</label><input class="input" name="f" [(ngModel)]="n.mensajeFinal" maxlength="200" placeholder="Ej.: www.minegocio.com.py"></div>
            </div>
            <div class="foot"><button class="btn btn-primary" [disabled]="ocupado()">Guardar cambios</button></div>
          </form>

          <div class="preview">
            <div class="label-sm">Vista previa del ticket</div>
            <div class="ticket mono">
              <div class="c b big">{{ n.nombre }}</div>
              @if (n.ruc) { <div class="c">RUC: {{ n.ruc }}</div> }
              @if (n.direccion) { <div class="c">{{ n.direccion }}</div> }
              @if (n.telefono) { <div class="c">Tel: {{ n.telefono }}</div> }
              @if (n.lineaExtra) { <div class="c">{{ n.lineaExtra }}</div> }
              <hr>
              <div>Ticket N° 000123</div>
              <hr>
              <div class="r2"><span>2 x Producto ejemplo</span><span>{{ ej(2 * 7500) }}</span></div>
              <div class="r2"><span>1 x Otro producto</span><span>{{ ej(12000) }}</span></div>
              <hr>
              <div class="r2 b"><span>TOTAL</span><span>{{ n.moneda }} {{ ej(27000) }}</span></div>
              <hr>
              @if (n.mensajeGracias) { <div class="c b">{{ n.mensajeGracias }}</div> }
              @if (n.mensajeFinal) { <div class="c">{{ n.mensajeFinal }}</div> }
            </div>
          </div>
        }

        <form class="card account" (ngSubmit)="cambiarPassword()">
          <div class="card-head"><app-icon name="lock" /><h2>Mi contraseña</h2></div>
          <div class="card-body stack">
            <div class="field"><label>Contraseña actual</label><input class="input" type="password" name="pa" [(ngModel)]="actual" required autocomplete="current-password"></div>
            <div class="field"><label>Nueva contraseña</label><input class="input" type="password" name="pn" [(ngModel)]="nueva" required minlength="6" autocomplete="new-password"></div>
            <div class="field"><label>Repetir nueva contraseña</label><input class="input" type="password" name="pr" [(ngModel)]="repetir" required autocomplete="new-password">
              @if (repetir && nueva !== repetir) { <span class="err">Las contraseñas no coinciden</span> }</div>
          </div>
          <div class="foot"><button class="btn btn-primary" [disabled]="!actual || nueva.length < 6 || nueva !== repetir">Cambiar contraseña</button></div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .layout { display: grid; grid-template-columns: minmax(0, 1fr) 300px; gap: 20px; align-items: start; }
    .account { grid-column: 1; max-width: 520px; }
    @media (max-width: 1000px) { .layout { grid-template-columns: minmax(0, 1fr); } .account { max-width: none; } }
    .span2 { grid-column: 1 / -1; }
    .foot { display: flex; justify-content: flex-end; padding: 14px 20px; border-top: 1px solid var(--line); background: var(--surface-2); border-radius: 0 0 var(--radius) var(--radius); }
    .preview { position: sticky; top: 24px; grid-row: span 2; }
    .ticket { margin-top: 8px; background: #fffdf6; color: #222; padding: 18px 16px; font-size: .74rem; line-height: 1.5;
      box-shadow: var(--shadow); border-radius: 4px; word-break: break-word; }
    .ticket hr { border: 0; border-top: 1px dashed #999; margin: 6px 0; }
    .c { text-align: center; } .b { font-weight: 700; } .big { font-size: .95rem; }
    .r2 { display: flex; justify-content: space-between; gap: 8px; }
  `],
})
export class ConfiguracionComponent {
  private api = inject(Api);
  private toast = inject(ToastService);
  private negocio = inject(NegocioService);
  auth = inject(AuthService);

  n: Negocio = { ...this.negocio.datos() };
  ocupado = signal(false);
  actual = '';
  nueva = '';
  repetir = '';

  ej(v: number) {
    return new Intl.NumberFormat('es-PY', { minimumFractionDigits: this.n.decimales, maximumFractionDigits: this.n.decimales }).format(v);
  }

  async guardar() {
    this.ocupado.set(true);
    try {
      const r = await this.api.guardarNegocio(this.n);
      this.negocio.datos.set(r);
      this.n = { ...r };
      this.toast.ok('Configuración guardada');
    } catch (e) { this.toast.error(errorMsg(e)); } finally { this.ocupado.set(false); }
  }

  async cambiarPassword() {
    try {
      await this.api.cambiarPassword(this.actual, this.nueva);
      this.actual = this.nueva = this.repetir = '';
      this.toast.ok('Contraseña actualizada');
    } catch (e) { this.toast.error(errorMsg(e)); }
  }
}

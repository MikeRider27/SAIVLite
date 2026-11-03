import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ConfirmService, ToastService } from '../core/toast.service';
import { IconComponent } from './icon.component';
import { ModalComponent } from './modal.component';

@Component({
  selector: 'app-overlays',
  standalone: true,
  imports: [IconComponent, ModalComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="toasts" aria-live="polite">
      @for (t of toast.toasts(); track t.id) {
        <div class="toast" [class]="t.tipo" (click)="toast.cerrar(t.id)">
          <app-icon [name]="t.tipo === 'error' ? 'alert' : 'check'" />
          <span>{{ t.texto }}</span>
        </div>
      }
    </div>
    @if (confirm.actual(); as c) {
      <app-modal [titulo]="c.titulo" [ancho]="420" (cerrar)="confirm.responder(false)">
        <p class="ink-2">{{ c.texto }}</p>
        <div footer>
          <button class="btn" (click)="confirm.responder(false)">Cancelar</button>
          <button class="btn" [class.btn-danger]="c.peligro" [class.btn-primary]="!c.peligro"
                  (click)="confirm.responder(true)" #ok>{{ c.ok }}</button>
        </div>
      </app-modal>
    }
  `,
  styles: [`
    .toasts { position: fixed; right: 16px; bottom: 16px; z-index: 80; display: flex; flex-direction: column; gap: 8px; max-width: min(380px, calc(100vw - 32px)); }
    .toast { display: flex; gap: 10px; align-items: flex-start; padding: 12px 14px; border-radius: 12px; cursor: pointer;
      background: var(--ink); color: var(--bg); font-weight: 600; font-size: .9rem; box-shadow: var(--shadow-lg);
      animation: slide .2s ease; border-left: 4px solid var(--accent); }
    .toast.ok { border-left-color: var(--ok); }
    .toast.error { border-left-color: var(--danger); }
    .toast app-icon { margin-top: 2px; }
    @keyframes slide { from { opacity: 0; transform: translateY(10px); } }
  `],
})
export class OverlaysComponent {
  toast = inject(ToastService);
  confirm = inject(ConfirmService);
}

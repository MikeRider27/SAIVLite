import { ChangeDetectionStrategy, Component, HostListener, input, output } from '@angular/core';
import { IconComponent } from './icon.component';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [IconComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="backdrop" (mousedown)="cerrar.emit()"></div>
    <div class="dialog card" role="dialog" aria-modal="true" [style.max-width.px]="ancho()" (mousedown)="$event.stopPropagation()">
      <header>
        <div class="grow">
          <h2>{{ titulo() }}</h2>
          @if (subtitulo()) { <p class="muted sub">{{ subtitulo() }}</p> }
        </div>
        <button class="btn btn-ghost btn-icon" type="button" (click)="cerrar.emit()" aria-label="Cerrar"><app-icon name="x" /></button>
      </header>
      <div class="body"><ng-content /></div>
      <ng-content select="[footer]" />
    </div>
  `,
  styles: [`
    :host { position: fixed; inset: 0; z-index: 50; display: grid; place-items: center; padding: 16px; }
    .backdrop { position: absolute; inset: 0; background: rgba(10, 22, 19, .48); backdrop-filter: blur(2px); animation: fade .15s ease; }
    .dialog { position: relative; width: 100%; max-height: calc(100vh - 32px); display: flex; flex-direction: column;
      box-shadow: var(--shadow-lg); animation: pop .18s cubic-bezier(.2,.9,.3,1.2); }
    header { display: flex; align-items: flex-start; gap: 12px; padding: 18px 20px 14px; border-bottom: 1px solid var(--line); }
    .sub { font-size: .85rem; margin-top: 2px; }
    .body { padding: 20px; overflow: auto; }
    :host ::ng-deep [footer] { display: flex; gap: 8px; justify-content: flex-end; padding: 14px 20px; border-top: 1px solid var(--line); background: var(--surface-2); border-radius: 0 0 var(--radius) var(--radius); }
    @keyframes fade { from { opacity: 0 } }
    @keyframes pop { from { opacity: 0; transform: translateY(8px) scale(.98) } }
  `],
})
export class ModalComponent {
  titulo = input.required<string>();
  subtitulo = input<string>('');
  ancho = input<number>(520);
  cerrar = output<void>();

  @HostListener('document:keydown.escape')
  esc() { this.cerrar.emit(); }
}

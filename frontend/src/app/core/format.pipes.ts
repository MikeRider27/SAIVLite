import { Pipe, PipeTransform, inject } from '@angular/core';
import { NegocioService, cantidad, fecha, fechaHora } from './negocio.service';

@Pipe({ name: 'dinero', standalone: true, pure: false })
export class DineroPipe implements PipeTransform {
  private n = inject(NegocioService);
  transform(v: number | null | undefined, simbolo = true): string { return this.n.dinero(v, simbolo); }
}

@Pipe({ name: 'cant', standalone: true })
export class CantidadPipe implements PipeTransform {
  transform(v: number | null | undefined): string { return cantidad(v); }
}

@Pipe({ name: 'fh', standalone: true })
export class FechaHoraPipe implements PipeTransform {
  transform(v: string | null | undefined): string { return fechaHora(v); }
}

@Pipe({ name: 'f', standalone: true })
export class FechaPipe implements PipeTransform {
  transform(v: string | null | undefined): string { return fecha(v); }
}

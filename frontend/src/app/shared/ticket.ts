import { Negocio, Venta, METODOS } from '../core/models';
import { cantidad, fechaHora } from '../core/negocio.service';

function esc(s: string | null | undefined): string {
  return (s ?? '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]!));
}

/** Imprime un ticket de 80mm usando un iframe oculto. */
export function imprimirTicket(v: Venta, n: Negocio, dinero: (x: number, s?: boolean) => string): void {
  const items = v.items.map(i => `
    <tr><td colspan="3" class="desc">${esc(i.descripcion)}</td></tr>
    <tr><td>${cantidad(i.cantidad)} x ${dinero(i.precioUnitario, false)}</td><td></td><td class="r">${dinero(i.importe, false)}</td></tr>
    ${i.cantidadDevuelta > 0 ? `<tr><td colspan="3" class="dev">Devuelto: ${cantidad(i.cantidadDevuelta)}</td></tr>` : ''}
  `).join('');
  const metodo = METODOS.find(m => m.id === v.metodoPago)?.label ?? v.metodoPago;
  const html = `<!doctype html><html><head><meta charset="utf-8"><title>Ticket ${v.id}</title>
  <style>
    @page { size: 80mm auto; margin: 3mm; }
    body { font-family: 'Courier New', monospace; font-size: 12px; width: 72mm; margin: 0 auto; color: #000; }
    .c { text-align: center; } .r { text-align: right; } .b { font-weight: bold; }
    h1 { font-size: 16px; margin: 0 0 2px; }
    hr { border: 0; border-top: 1px dashed #000; margin: 6px 0; }
    table { width: 100%; border-collapse: collapse; } td { padding: 1px 0; vertical-align: top; }
    .desc { padding-top: 4px; } .dev { font-style: italic; font-size: 11px; }
    .tot td { font-size: 14px; font-weight: bold; padding-top: 4px; }
  </style></head><body>
  <div class="c">
    <h1>${esc(n.nombre)}</h1>
    ${n.ruc ? `<div>RUC: ${esc(n.ruc)}</div>` : ''}
    ${n.direccion ? `<div>${esc(n.direccion)}</div>` : ''}
    ${n.telefono ? `<div>Tel: ${esc(n.telefono)}</div>` : ''}
    ${n.lineaExtra ? `<div>${esc(n.lineaExtra)}</div>` : ''}
  </div>
  <hr>
  <div>Ticket N° <span class="b">${String(v.id).padStart(6, '0')}</span></div>
  <div>${fechaHora(v.fechaHora)}</div>
  <div>Cajero: ${esc(v.cajero)}</div>
  <hr>
  <table>${items}</table>
  <hr>
  <table>
    <tr class="tot"><td>TOTAL</td><td class="r">${dinero(v.total)}</td></tr>
    <tr><td>${esc(metodo)}</td><td class="r">${dinero(v.pagado)}</td></tr>
    ${v.metodoPago === 'EFECTIVO' ? `<tr><td>Vuelto</td><td class="r">${dinero(v.cambio)}</td></tr>` : ''}
    ${v.totalDevuelto > 0 ? `<tr><td>Devuelto</td><td class="r">-${dinero(v.totalDevuelto)}</td></tr>` : ''}
  </table>
  <hr>
  ${n.mensajeGracias ? `<div class="c b">${esc(n.mensajeGracias)}</div>` : ''}
  ${n.mensajeFinal ? `<div class="c">${esc(n.mensajeFinal)}</div>` : ''}
  </body></html>`;

  const iframe = document.createElement('iframe');
  iframe.style.cssText = 'position:fixed;right:0;bottom:0;width:0;height:0;border:0;';
  document.body.appendChild(iframe);
  const doc = iframe.contentDocument!;
  doc.open();
  doc.write(html);
  doc.close();
  setTimeout(() => {
    iframe.contentWindow?.focus();
    iframe.contentWindow?.print();
    setTimeout(() => iframe.remove(), 1000);
  }, 150);
}
